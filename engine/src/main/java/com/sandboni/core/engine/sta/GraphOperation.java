package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

public interface GraphOperation<R extends OperationResult<?>> {

    R execute(Graph<Vertex, Edge> graph);

}
