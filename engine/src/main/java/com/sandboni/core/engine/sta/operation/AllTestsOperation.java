package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.utils.timing.SWConsts;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
import org.jgrapht.Graph;

import static com.sandboni.core.engine.common.StreamHelper.emptyIfFalse;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;

public class AllTestsOperation extends AbstractGraphOperation<SetResult<TestVertex>> {

    AllTestsOperation() {
    }

    @Override
    public SetResult<TestVertex> execute(Graph<Vertex, Edge> graph) {
        StopWatch swAll = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_EXECUTE, "ALL").start();
        SetResult<TestVertex> testVertexSetResult = new SetResult<>(emptyIfFalse(graph.containsVertex(START_VERTEX),
                () -> graph.edgesOf(START_VERTEX).stream().map(e -> (TestVertex) e.getSource())));
        swAll.stop();
        return testVertexSetResult;
    }
}
