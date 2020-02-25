package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.utils.timing.SWConsts;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
import org.jgrapht.Graph;

import static com.sandboni.core.engine.common.StreamHelper.emptyIfFalse;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.END_VERTEX;

public class ChangesOperation extends AbstractGraphOperation<SetResult<Vertex>> {

    ChangesOperation() {
    }

    @Override
    public SetResult<Vertex> execute(Graph<Vertex, Edge> graph) {
        StopWatch swAll = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_EXECUTE, "ALL").start();
        SetResult<Vertex> vertexSetResult = new SetResult<>(emptyIfFalse(graph.containsVertex(END_VERTEX),
                () -> graph.edgesOf(END_VERTEX).stream().map(Edge::getTarget)));
        swAll.stop();
        return vertexSetResult;
    }
}
