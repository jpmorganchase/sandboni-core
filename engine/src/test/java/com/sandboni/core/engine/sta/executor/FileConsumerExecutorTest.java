package com.sandboni.core.engine.sta.executor;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class FileConsumerExecutorTest {

    private FileConsumerExecutor fileConsumerExecutor;
    private Context context;
    private File file1;

    @Before
    public void setUp() {
        HashMap<String, ThrowingBiConsumer<File, Context>> consumers = new HashMap<>();
        consumers.put("txt", (file, context) -> {
            assertEquals(file1, file);
        });

        context = new Context("appId", new String[]{}, new String[]{}, new String[]{}, "", new ChangeScopeImpl(), null, null, true);
        file1 = new File("file1.txt");
        fileConsumerExecutor = new FileConsumerExecutor(consumers, context, "FileConsumerExecutorTest");
    }

    @Test
    public void execute() {
        Set<File> files = new HashSet<>();
        files.add(file1);
        Collection<String> execute = fileConsumerExecutor.execute(files);
        assertEquals(file1.getAbsolutePath(), execute.iterator().next());

    }
}
