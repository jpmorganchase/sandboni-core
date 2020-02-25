package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.utils.timing.SWConsts;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
import org.jgrapht.Graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.sandboni.core.engine.common.StreamHelper.emptyIfFalse;

public class AllRelatedNodesOperation extends AbstractGraphOperation<SetResult<Vertex>> {

    private final Set<Edge> allReachableEdges;

    AllRelatedNodesOperation(Set<Edge> allReachableEdges) {
        Objects.requireNonNull(allReachableEdges, INVALID_ARGUMENT);
        this.allReachableEdges = new HashSet<>(allReachableEdges);
    }

    @Override
    public SetResult<Vertex> execute(Graph<Vertex, Edge> graph) {
        StopWatch swAll = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_EXECUTE, "ALL").start();
        SetResult<Vertex> vertexSetResult = new SetResult<>(emptyIfFalse(true, () ->
                allReachableEdges.stream()
                        .flatMap(e -> Arrays.stream(new Vertex[]{e.getSource(), e.getTarget()})).filter(v -> !v.isSpecial()).distinct()));
        swAll.stop();
        return vertexSetResult;
    }
}
