package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

import java.util.HashSet;
import java.util.Set;

import static com.sandboni.core.engine.common.StreamHelper.emptyIfFalse;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.REFLECTION_CALL_VERTEX;

public class ReflectionCallTestsOperation extends AbstractGraphOperation<SetResult<TestVertex>> {

    private final Set<TestVertex> allTests;

    public ReflectionCallTestsOperation(Set<TestVertex> allTests) {
        this.allTests = new HashSet<>(allTests);
    }

    @Override
    public SetResult<TestVertex> execute(Graph<Vertex, Edge> graph) {
        Set<String> testClassNames = emptyIfFalse(graph.containsVertex(REFLECTION_CALL_VERTEX),
            () -> graph.edgesOf(REFLECTION_CALL_VERTEX).stream().filter(e -> e.getLinkType().equals(LinkType.REFLECTION_CALL_TEST)).map(e -> e.getTarget().getActor()));

        return new SetResult<>(emptyIfFalse(!testClassNames.isEmpty(), () -> allTests.parallelStream().filter(tv -> testClassNames.contains(tv.getActor()))));
    }

}
