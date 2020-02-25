package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.common.CachingSupplier;
import com.sandboni.core.engine.result.FilterIndicator;
import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public class Builder {

    private static final Logger log = LoggerFactory.getLogger(Builder.class);

    private Supplier<DirectedGraph<Vertex, Edge>> graphSupplier = new CachingSupplier<>(this::buildGraph);
    private final Context context;
    private FilterIndicator filterIndicator;

    public Builder(Context context) {
        this(context, FilterIndicator.SELECTIVE);
    }

    public Builder(Context context, FilterIndicator filterIndicator) {
        this.context = context;
        this.filterIndicator = filterIndicator;
    }

    public DirectedGraph<Vertex, Edge> getGraph() {
        return graphSupplier.get();
    }

    private DirectedGraph<Vertex, Edge> buildGraph() {
        StopWatch swAll = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), "buildGraph()", "ALL").start();
        Instant start = Instant.now();
        DirectedGraph<Vertex, Edge> graph = new DefaultDirectedGraph<>(Edge.class);
        //reversing direction
        context.getLinks().sequential().forEach(l -> add(graph, l.getCallee(), l.getCaller(), l.getLinkType()));
        Instant finish = Instant.now();
        swAll.stop();

        log.info("Build Graph execution total time: {}", Duration.between(start, finish).toMillis());

        // Cleaning cache of not longer needed Vertex
        LinkFactory.clear(context.getApplicationId());
        return graph;
    }

    private void add(DirectedGraph<Vertex, Edge> graph, Vertex source, Vertex target, LinkType linkType) {
        graph.addVertex(source);
        graph.addVertex(target);
        Edge edge = graph.addEdge(source, target);
        if (edge != null) {
            edge.setLinkType(linkType);
        }
    }

    public FilterIndicator getFilterIndicator() {
        return filterIndicator;
    }

}