package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.contract.ThrowingConsumer;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.utils.StringUtil;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Context {

    private static final Logger log = LoggerFactory.getLogger(Context.class);
    private static final String CLASSPATH_PROPERTY_NAME = "java.class.path";
    private static final String DEFAULT_APPLICATION_ID = "sandboni.default.AppId";
    private static final String ALWAYS_RUN_ANNOTATION = "AlwaysRun";

    private final String filter;
    private Set<Link> links = new HashSet<>();
    private String currentLocation;
    private ChangeScope<Change> changeScope;
    private Collection<String> srcLocations;
    private Collection<String> testLocations;
    private String classPath;
    private String applicationId;
    private String alwaysRunAnnotation;
    private String seloniFilepath;

    private Set<LinkType> adoptedLinkTypes;

    private boolean inScope(String actor) {
        return filter == null || (actor != null && actor.startsWith(filter));
    }

    private boolean inScope(Vertex vertex) {
        return inScope(vertex.getActor());
    }

    public ChangeScope<Change> getChangeScope() {
        return changeScope;
    }

    // Visible for testing only
    public Context(String[] srcLocation, String[] testLocation, String filter, ChangeScope<Change> changes, String seloniFilepath) {
        this(DEFAULT_APPLICATION_ID, srcLocation, testLocation, new String[0], filter, changes, null, null, seloniFilepath);
    }

    @SuppressWarnings("squid:S00107")
    public Context(String applicationId, String[] srcLocation, String[] testLocation, String[] dependencies,
                   String filter, ChangeScope<Change> changes, String includeTestAnnotation, String seloniFilepath) {
        this(applicationId == null ? DEFAULT_APPLICATION_ID : applicationId,
                srcLocation, testLocation, dependencies, filter, changes, null, includeTestAnnotation, seloniFilepath);
    }

    @SuppressWarnings("squid:S00107")
    public Context(String applicationId, String[] srcLocation, String[] testLocation, String[] dependencies,
                   String filter, ChangeScope<Change> changes, String currentLocation, String includeTestAnnotation, String seloniFilepath) {
        this.applicationId = applicationId;
        this.srcLocations = getCollection(srcLocation);
        this.testLocations = getCollection(testLocation);

        this.classPath = getExecutionClasspath(srcLocations, testLocations, getCollection(dependencies));

        this.filter = filter;
        this.changeScope = changes;
        this.adoptedLinkTypes = new HashSet<>();
        this.currentLocation = currentLocation;
        this.alwaysRunAnnotation = StringUtil.isEmptyOrNull(includeTestAnnotation) ? ALWAYS_RUN_ANNOTATION : includeTestAnnotation;
        this.seloniFilepath = seloniFilepath;
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
        this.applicationId = source.applicationId;
        this.srcLocations = Collections.unmodifiableCollection(source.srcLocations);
        this.testLocations = Collections.unmodifiableCollection(source.testLocations);
        this.classPath = source.classPath;
        this.filter = source.filter;
        this.changeScope = source.changeScope;
        this.currentLocation = source.currentLocation;
        this.adoptedLinkTypes = new HashSet<>();
        this.alwaysRunAnnotation = source.alwaysRunAnnotation;
        this.seloniFilepath = source.seloniFilepath;
    }

    public Context getLocalContext() {
        return new Context(this);
    }

    public String getClassPath() {
        return classPath;
    }

    public synchronized Stream<Link> getLinks() {
        return new ArrayList<>(links).parallelStream();
    }

    private int adoptLink(Link link) {
        link.setFilter(filter);
        adoptedLinkTypes.add(link.getLinkType());
        return links.add(link) ? 1 : 0;
    }

    public synchronized int addLink(Link link) {
        if (inScope(link.getCaller()) || inScope(link.getCallee()) || (link.getCaller().isSpecial() && link.getCallee().isSpecial())) {
            return adoptLink(link);
        }
        return 0;
    }

    public synchronized String getCurrentLocation() {
        return currentLocation;
    }

    public synchronized int addLinks(Link... linksToAdd) {
        int result = 0;
        for (Link link : linksToAdd) {
            result = result + addLink(link);
        }
        return result;
    }

    public synchronized void forEachLocation(ThrowingConsumer<String> consumer) {
        testLocations.forEach(s -> {
            currentLocation = s;
            consumer.accept(currentLocation);
        });

        srcLocations.forEach(s -> {
            currentLocation = s;
            consumer.accept(currentLocation);
        });
    }

    public boolean isAdoptedLinkType(LinkType...linkTypes){
        return Arrays.stream(linkTypes).allMatch(t -> adoptedLinkTypes.contains(t));
    }

    public Collection<String> getSrcLocations() {
        return srcLocations;
    }

    public Collection<String> getTestLocations() {
        return testLocations;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getAlwaysRunAnnotation() {
        return alwaysRunAnnotation;
    }

    public String getSeloniFilepath() {
        return seloniFilepath;
    }
}