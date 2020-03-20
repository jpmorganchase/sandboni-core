package com.sandboni.core.engine.finder.scanner;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.executor.ParallelExecutor;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileConsumerTest {

    private FileConsumer fileConsumer;
    private Context context;
    private Map<String, Set<File>> locationFiles;

    @Before
    public void setUp() {
        HashMap<String, ThrowingBiConsumer<File, Context>> consumers = new HashMap<>();
        consumers.put("txt", (file, context) -> {
            // not called because we are using mock executor
        });
        context = new Context("appId", new String[]{}, new String[]{}, new String[]{}, "", new ChangeScopeImpl(), null, null, true);

        locationFiles = new HashMap<>();
        Set<File> files1 = new HashSet<>();
        files1.add(new File("test1.txt"));
        locationFiles.put("location1", files1);

        Set<File> files2 = new HashSet<>();
        files2.add(new File("test2.txt"));
        locationFiles.put("location2", files2);

        fileConsumer = new FileConsumer(consumers, context, "FileConsumerTest") {
            @Override
            ParallelExecutor<Collection<File>, Collection<String>> getFileConsumerExecutor(Context localContext) {
                return new FileConsumerExecutorMock(localContext, locationFiles);
            }
        };
    }

    @Test
    public void consume() {
        fileConsumer.consume(locationFiles);
        assertEquals(2, context.getLinks().count());
        assertTrue(context.getLinks().anyMatch(link -> link.getCaller().getActor().equals("caller_location1")));
        assertTrue(context.getLinks().anyMatch(link -> link.getCaller().getActor().equals("caller_location2")));
    }

    private static class FileConsumerExecutorMock implements ParallelExecutor<Collection<File>, Collection<String>> {

        private Context localContext;
        private Map<String, Set<File>> locationFiles;

        FileConsumerExecutorMock(Context localContext, Map<String, Set<File>> locationFiles) {
            this.localContext = localContext;
            this.locationFiles = locationFiles;
        }

        @Override
        public Collection<String> execute(Collection<File> input) {
            assertTrue(locationFiles.containsKey(localContext.getCurrentLocation()));
            localContext.addLink(LinkFactory.createInstance(localContext.getApplicationId(),
                    new Vertex.Builder("caller_" + localContext.getCurrentLocation(), "callerAction", localContext.getCurrentLocation()).build(),
                    new Vertex.Builder("callee_" + localContext.getCurrentLocation(), "calleeAction", localContext.getCurrentLocation()).build(),
                    LinkType.METHOD_CALL));
            return new ArrayList<>();
        }
    }

}
