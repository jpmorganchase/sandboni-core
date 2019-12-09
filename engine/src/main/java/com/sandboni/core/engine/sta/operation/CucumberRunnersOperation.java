package com.sandboni.core.engine.sta.operation;


import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

import static com.sandboni.core.engine.common.StreamHelper.emptyIfFalse;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.CUCUMBER_RUNNER_VERTEX;

public class CucumberRunnersOperation extends AbstractGraphOperation<SetResult<TestVertex>> {
    @Override
    public SetResult<TestVertex> execute(Graph<Vertex, Edge> graph) {
        return new SetResult<>(emptyIfFalse(graph.containsVertex(CUCUMBER_RUNNER_VERTEX),
                () -> graph.edgesOf(CUCUMBER_RUNNER_VERTEX).stream().map(e -> (TestVertex) e.getTarget())));
    }
}
