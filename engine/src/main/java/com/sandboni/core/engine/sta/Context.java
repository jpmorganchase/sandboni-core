package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.contract.ThrowingConsumer;
import com.sandboni.core.engine.sta.executor.AbstractParallelExecutor;
import com.sandboni.core.engine.sta.executor.LocationScannerExecutor;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.utils.StringUtil;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sandboni.core.engine.utils.TimeUtils.elapsedTime;

@Getter
public class Context {
    private static final Logger log = LoggerFactory.getLogger(Context.class);
    private static final String CLASSPATH_PROPERTY_NAME = "java.class.path";
    private static final String DEFAULT_APPLICATION_ID = "sandboni.default.AppId";
    private static final String ALWAYS_RUN_ANNOTATION = "AlwaysRun";

    private final Set<String> filters;
    private final ConcurrentHashMap<Link, Boolean> links;
    private String currentLocation;
    private final ChangeScope<Change> changeScope;
    private final Collection<String> srcLocations;
    private final Collection<String> testLocations;
    private final Collection<String> dependencyJars;
    private final String classPath;
    private final String applicationId;
    private final String alwaysRunAnnotation;
    private final String seloniFilepath;
    private final boolean enablePreview;
    private final ConcurrentHashMap<LinkType, Boolean> adoptedLinkTypes;

    public boolean inScope(String actor) {
        return filters.isEmpty() || (actor != null && filters.stream().anyMatch(actor::contains));
    }

    public ChangeScope<Change> getChangeScope() {
        return changeScope;
    }

    // Visible for testing only
    public Context(String[] srcLocation, String[] testLocation, String filter, ChangeScope<Change> changes, String seloniFilepath) {
        this(DEFAULT_APPLICATION_ID, srcLocation, testLocation, new String[0], filter, changes, null, seloniFilepath, true);
    }

    @SuppressWarnings("squid:S00107")
    public Context(String applicationId, String[] srcLocation, String[] testLocation, String[] dependencies,
                   String filter, ChangeScope<Change> changes, String includeTestAnnotation, String seloniFilepath,
                   boolean enablePreview) {
        this.links = new ConcurrentHashMap<>();
        this.adoptedLinkTypes = new ConcurrentHashMap<>();
        this.applicationId = applicationId == null ? DEFAULT_APPLICATION_ID : applicationId;
        this.srcLocations = getCollection(srcLocation);
        this.testLocations = getCollection(testLocation);
        this.dependencyJars = getCollection(dependencies);

        this.classPath = getExecutionClasspath(srcLocations, testLocations, getCollection(dependencies));

        this.filters = getFilters(filter);
        this.changeScope = changes;
        this.alwaysRunAnnotation = StringUtil.isEmptyOrNull(includeTestAnnotation) ? ALWAYS_RUN_ANNOTATION : includeTestAnnotation;
        this.seloniFilepath = seloniFilepath;
        this.enablePreview = enablePreview;
    }

    private Set<String> getFilters(String filter) {
        if (filter == null) {
            return Collections.emptySet();
        }
        List<String> tokens = Arrays.asList(filter.split(","));
        Set<String> result = new HashSet<>(tokens);
        result.addAll(tokens.stream().map(s -> s.replace(".", File.separator).trim()).collect(Collectors.toSet()));
        return result;
    }

    private Collection<String> getCollection(String[] array) {
        return array == null ? Collections.emptySet() :
                Arrays.stream(array).map(l -> new File(l).getAbsolutePath()).collect(Collectors.toSet());
    }

    private String getExecutionClasspath(Collection<String> srcLocation, Collection<String> testLocation, Collection<String> dependencies) {
        String currentJavaClasspath = System.getProperty(CLASSPATH_PROPERTY_NAME, "");
        log.debug("Current java.class.path is: {}", currentJavaClasspath);

        Set<String> projectClasspath = new HashSet<>(Arrays.asList(currentJavaClasspath.split(File.pathSeparator)));
        projectClasspath.addAll(srcLocation);
        projectClasspath.addAll(testLocation);
        projectClasspath.addAll(dependencies);

        String updatedClassPath = String.join(File.pathSeparator, projectClasspath);
        log.debug("Execution java.class.path is: {}", updatedClassPath);

        return updatedClassPath;
    }

    private Context(Context source) {
        this.links = new ConcurrentHashMap<>();
        this.adoptedLinkTypes = new ConcurrentHashMap<>();
        this.applicationId = source.applicationId;
        this.srcLocations = Collections.unmodifiableCollection(source.srcLocations);
        this.testLocations = Collections.unmodifiableCollection(source.testLocations);
        this.dependencyJars = Collections.unmodifiableCollection(source.dependencyJars);
        this.classPath = source.classPath;
        this.filters = Collections.unmodifiableSet(source.filters);
        this.changeScope = source.changeScope;
        this.currentLocation = source.currentLocation;
        this.alwaysRunAnnotation = source.alwaysRunAnnotation;
        this.seloniFilepath = source.seloniFilepath;
        this.enablePreview = source.enablePreview;
    }

    // Adding synchronized to safely handle currentLocation mutable state. This needs to be refactored out.
    public synchronized Context getLocalContext() {
        return new Context(this);
    }

    public String getClassPath() {
        return classPath;
    }

    public Stream<Link> getLinks() {
        return Collections.unmodifiableSet(links.keySet()).parallelStream();
    }

    private int adoptLink(Link link) {
        adoptedLinkTypes.put(link.getLinkType(), Boolean.TRUE);
        return links.put(link, Boolean.TRUE) == null ? 1 : 0;
    }

    public int addLink(Link link) {
        return adoptLink(link);
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void addLinks(Link... linksToAdd) {
        for (Link link : linksToAdd) {
            addLink(link);
        }
    }

    public void forEachLocation(ThrowingConsumer<String> consumer, boolean scanDependencies) {
        long start = System.nanoTime();
        log.info("[{}] Start traversing testLocations", Thread.currentThread().getName());
        testLocations.forEach(s -> {
            currentLocation = s;
            consumer.accept(currentLocation);
        });
        log.info("[{}] Finished traversing testLocations in {} milliseconds", Thread.currentThread().getName(), elapsedTime(start));

        start = System.nanoTime();
        log.info("[{}] Start traversing srcLocations", Thread.currentThread().getName());
        srcLocations.forEach(s -> {
            currentLocation = s;
            consumer.accept(currentLocation);
        });
        log.info("[{}] Finished traversing srcLocations in {} milliseconds", Thread.currentThread().getName(), elapsedTime(start));

        if (isEnablePreview() && scanDependencies) {
            start = System.nanoTime();
            log.info("[{}] Start traversing jars", Thread.currentThread().getName());
            currentLocation = "DependencyJars";
            getScannerExecutor(consumer).execute(new ArrayList<>(dependencyJars));
            log.info("[{}] Finished traversing jars in {} milliseconds", Thread.currentThread().getName(), elapsedTime(start));
        }
    }

    private AbstractParallelExecutor<String, String> getScannerExecutor(ThrowingConsumer<String> consumer) {
        return new LocationScannerExecutor(consumer);
    }

    public boolean isAdoptedLinkType(LinkType... linkTypes) {
        return Arrays.stream(linkTypes).allMatch(adoptedLinkTypes::containsKey);
    }
}