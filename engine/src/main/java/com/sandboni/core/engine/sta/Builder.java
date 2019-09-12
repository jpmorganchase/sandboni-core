package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.common.CachingSupplier;
import com.sandboni.core.engine.result.FilterIndicator;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.JiraVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.common.StreamHelper;
import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Builder {
    private static final int MAX_PATH_LENGTH = 100;

    private final DirectedGraph<Vertex, Edge> graph;

    private final Collection<String> sourceLocations;

    private FilterIndicator filterIndicator = FilterIndicator.SELECTIVE;

    private final Supplier<Set<Vertex>> exitPointsSupplier = new CachingSupplier<>(this::getExitPointsImpl);
    private final Supplier<Set<Vertex>> unreachableExitPointsSupplier = new CachingSupplier<>(this::getUnreachableExitPointsImpl);
    private final Supplier<Set<TestVertex>> allEntryPointsSupplier = new CachingSupplier<>(this::getAllEntryPointsImpl);
    private final Supplier<Set<TestVertex>> entryPointsSupplier = new CachingSupplier<>(this::getEntryPointsImpl);
    private final Supplier<Set<TestVertex>> disconnectedEntryPointsSupplier = new CachingSupplier<>(this::getDisconnectedEntryPointsImpl);
    private final Supplier<Set<Vertex>> allAffectedSupplier = new CachingSupplier<>(this::getAllAffectedImpl);
    private final Supplier<Set<Edge>> allReachableEdgesSupplier = new CachingSupplier<>(this::getAllReachableEdgesImpl);
    private final Supplier<Set<Edge>> jiraSupplier = new CachingSupplier<>(this::getJiraEntryPointsImpl);

    private static final Logger log = LoggerFactory.getLogger(Builder.class);

    public Builder(Context context) {
        graph = buildGraph(context);
        sourceLocations = Collections.unmodifiableCollection(context.getSrcLocations());
    }

    public Builder(Context context, FilterIndicator filterIndicator) {
        this(context);
        this.filterIndicator = filterIndicator;
    }

    private static DirectedGraph<Vertex, Edge> buildGraph(Context context) {
        Instant start = Instant.now();
        DefaultDirectedGraph<Vertex, Edge> directedGraph = new DefaultDirectedGraph<>(Edge.class);
        //reversing direction
        context.getLinks().sequential().forEach(l -> add(directedGraph, l.getCallee(), l.getCaller(), l.getLinkType()));
        Instant finish = Instant.now();

        log.debug("....Build Graph execution total time: {}", Duration.between(start, finish).toMillis());

        return directedGraph;
    }

    private static void add(DirectedGraph<Vertex, Edge> graph, Vertex source, Vertex target, LinkType linkType) {
        graph.addVertex(source);
        graph.addVertex(target);
        Edge edge = graph.addEdge(source, target);
        if (edge != null) {
            edge.setLinkType(linkType);
        }
    }

    public Stream<TestVertex> getEntryPoints() {
        return entryPointsSupplier.get().stream();
    }

    private Set<TestVertex> getEntryPointsImpl() {
        ShortestPathAlgorithm<Vertex, Edge> algorithm = new BellmanFordShortestPath<>(graph);
        return StreamHelper.emptyIfFalse(graph.containsVertex(VertexInitTypes.START_VERTEX) && graph.containsVertex(VertexInitTypes.END_VERTEX),
                () -> allEntryPointsSupplier.get().stream().filter(v -> isAffectedCucumberVertex(v) || algorithm.getPath(VertexInitTypes.END_VERTEX, v) != null));
    }

    private boolean isAffectedCucumberVertex(Vertex v) {
        return v instanceof CucumberVertex && ((CucumberVertex)v).isAffected();
    }

    public Stream<Vertex> getExitPoints() {
        return exitPointsSupplier.get().stream();
    }

    private Set<Vertex> getExitPointsImpl() {
        return StreamHelper.emptyIfFalse(graph.containsVertex(VertexInitTypes.END_VERTEX), () -> graph.edgesOf(VertexInitTypes.END_VERTEX).stream().map(Edge::getTarget));
    }

    public Stream<Vertex> getUnreachableExitPoints() {
        return unreachableExitPointsSupplier.get().stream();
    }

    private Set<Vertex> getUnreachableExitPointsImpl() {
        ShortestPathAlgorithm<Vertex, Edge> algorithm = new BellmanFordShortestPath<>(graph);
        return StreamHelper.emptyIfFalse(graph.containsVertex(VertexInitTypes.END_VERTEX) && graph.containsVertex(VertexInitTypes.START_VERTEX),
                () -> graph.edgesOf(VertexInitTypes.END_VERTEX).stream().map(Edge::getTarget).filter(v -> algorithm.getPath(v, VertexInitTypes.START_VERTEX) == null));
    }

    public Stream<TestVertex> getAllEntryPoints() {
        return allEntryPointsSupplier.get().stream();
    }

    private Set<TestVertex> getAllEntryPointsImpl() {
        return StreamHelper.emptyIfFalse(graph.containsVertex(VertexInitTypes.START_VERTEX),
                () -> graph.edgesOf(VertexInitTypes.START_VERTEX).stream().map(e-> (TestVertex)e.getSource()));
    }

    public Stream<TestVertex> getDisconnectedEntryPoints() {
        return disconnectedEntryPointsSupplier.get().stream();
    }

    Set<TestVertex> getDisconnectedEntryPointsImpl() {
        //getting only the not-ignored and unrelated tests
        Set<TestVertex> notRelatedTests = allEntryPointsSupplier.get().parallelStream()
                .filter(t -> !t.isIgnore() && !entryPointsSupplier.get().contains(t)).collect(Collectors.toSet());

        Set<TestVertex> disconnectedTests = new HashSet<>();

        notRelatedTests.forEach(tv -> {
            Deque<Vertex> stack = new LinkedList<>();
            Set<Vertex> visited = new HashSet<>();
            stack.push(tv);
            boolean found = false;
            while (!stack.isEmpty()){
                 Vertex v = stack.pop();
                 if (!visited.contains(v)) {
                     visited.add(v);
                     if (!v.isSpecial() && Objects.nonNull(v.getLocation()) && sourceLocations.contains(v.getLocation())) {
                         found = true;
                         break;
                     } else {
                         Set<Edge> edges = graph.incomingEdgesOf(v);
                         edges.forEach(e -> stack.push(e.getSource()));
                     }
                 }
            }
            if (!found){
                disconnectedTests.add(tv);
            }
        });

        return disconnectedTests;
   }

    public Stream<String> getJiraList() {
        final String format = "%s | %s | %s";
        return jiraSupplier.get().stream()
                .map(e -> String.format(format, e.getTarget().getAction(), ((JiraVertex)e.getTarget()).getDate(),
                        ((JiraVertex)e.getTarget()).getRevisionId())).distinct();
    }

    private Stream<Edge> getJiraEntryPoints() {
        return jiraSupplier.get().stream();
    }

    private Set<Edge> getJiraEntryPointsImpl() {
        return StreamHelper.emptyIfFalse(graph.containsVertex(VertexInitTypes.START_VERTEX) && graph.containsVertex(VertexInitTypes.CONTAINER_VERTEX), () ->
                Stream.concat(
                        allReachableEdgesSupplier.get().stream()
                                .flatMap(re -> graph.edgesOf(re.getSource()).stream().filter(e -> e.getTarget().getActor().equals(VertexInitTypes.TRACKING_VERTEX.getActor()))),
                        disconnectedEntryPointsSupplier.get().stream()
                                .flatMap(v -> graph.edgesOf(v).stream().filter(e -> e.getSource().getActor().equals(VertexInitTypes.TRACKING_VERTEX.getActor()))))
                        .filter(e -> e.getTarget().getActor().equals(VertexInitTypes.TRACKING_VERTEX.getActor())));
    }

    public Stream<Vertex> getAllAffected() {
        return allAffectedSupplier.get().stream();
    }

    private Set<Vertex> getAllAffectedImpl() {
        return StreamHelper.emptyIfFalse(true, () ->
                getAllReachableEdges()
                        .flatMap(e -> Arrays.stream(new Vertex[]{e.getSource(), e.getTarget()})).filter(v -> !v.isSpecial()).distinct());
    }

    private Stream<Edge> getAllReachableEdges() {
        return allReachableEdgesSupplier.get().stream();
    }

    private Set<Edge> getAllReachableEdgesImpl() {
        return StreamHelper.emptyIfFalse(graph.containsVertex(VertexInitTypes.START_VERTEX) && graph.containsVertex(VertexInitTypes.END_VERTEX), () -> {
            AllDirectedPaths<Vertex, Edge> algorithm = new AllDirectedPaths<>(graph);
            List<GraphPath<Vertex, Edge>> ways = algorithm.getAllPaths(VertexInitTypes.END_VERTEX, VertexInitTypes.START_VERTEX, true, MAX_PATH_LENGTH);
            return ways.stream().flatMap(w -> w.getEdgeList().stream()).distinct();
        });
    }

    public int getReachableLineNumberCount() {
        Set<Edge> reachable = allReachableEdgesSupplier.get();
        Set<TestVertex> entryPoints = allEntryPointsSupplier.get();
        return reachable.stream()
                .flatMap(e -> Arrays.stream(new Vertex[]{e.getSource(), e.getTarget()}))
                .filter(v -> !entryPoints.contains(v) && !v.isLineNumbersEmpty())
                .mapToInt(v -> v.getLineNumbers().size()).sum();
    }

    public FilterIndicator getFilterIndicator() {
        return filterIndicator;
    }

    public void outputMetadataLinks() {
        final String filename = "JiraReport.csv";
        final LinkedList<String> header = new LinkedList<>(Collections.singletonList("Class path,Class name,Method name,Jira number,Commit Weight,Commit Date,Commit id"));
        List<String> lines = getJiraEntryPoints().map(this::edgeToCsvLine).collect(Collectors.toList());
        header.addAll(lines);
        try {
            Path path = Paths.get(System.getProperty("user.dir"), filename);
            Files.write(path, header);
            log.info("Generated csv file: {}", path.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to write into file", e);
        }
    }

    private String edgeToCsvLine(Edge edge) {
        final String format = "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"";
        JiraVertex csvVertex = (JiraVertex)edge.getTarget();
        return String.format(format, edge.getSource().getFilePath(), edge.getSource().getActor(),
                edge.getSource().getAction(),edge.getTarget().getAction(),
                csvVertex.getDate(), csvVertex.getRevisionId());
    }
}