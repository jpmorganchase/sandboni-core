package com.sandboni.core.engine.sta.executor;

import com.sandboni.core.engine.sta.Finder;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FinderExecutorTest {

    private FinderExecutor finderExecutor;
    private Context context;

    @Before
    public void setUp() {
        context = new Context("appId", new String[]{}, new String[]{}, new String[]{}, "", new ChangeScopeImpl(), null, null, true);
        finderExecutor = new FinderExecutor(context);
    }

    @Test
    public void execute() {
        Set<Finder> finders = new HashSet<>();
        finders.add(localContext -> localContext.addLink(LinkFactory.createInstance(context.getApplicationId(),
                new Vertex.Builder("caller", "action1", context.getCurrentLocation()).build(),
                new Vertex.Builder("callee", "action2", context.getCurrentLocation()).build(),
                LinkType.METHOD_CALL)));

        finderExecutor.execute(finders);
        assertEquals(1, context.getLinks().count());
        assertTrue(context.getLinks().anyMatch(link -> link.getCaller().getActor().equals("caller")));

    }

}
