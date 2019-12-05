package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.FinderTestBase;
import com.sandboni.core.engine.contract.ChangeDetector;
import com.sandboni.core.engine.finder.bcel.visitors.AffectedClassVisitor;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.scm.scope.*;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class BcelFinderConcurrentTest extends FinderTestBase {

    private static final String TEST_LOCATION = "./target/test-classes/com/sandboni/core/engine/scenario/concurrent";
    private static final String TEST_PACKAGE = "com.sandboni.core.engine.scenario.concurrent";
    private static final int THREADS = 200;

    private ClassVisitor affectedClassVisitor;
    private JavaClass interfaceClass;
    private JavaClass implementationClass;
    private AtomicInteger overlaps;

    public BcelFinderConcurrentTest() {
        super(TEST_LOCATION, TEST_PACKAGE);
    }

    @Before
    public void setUp() throws IOException {
        super.initializeContext(new MockForConcurrentChangeDetector().getChanges("1", "2"));
        affectedClassVisitor = new AffectedClassVisitor();

        File interfaceFile = new File(TEST_LOCATION, "InterfaceToTest.class");
        ClassParser interfaceParse = new ClassParser(interfaceFile.getAbsolutePath());
        interfaceClass = interfaceParse.parse();

        File implementationFile = new File(TEST_LOCATION, "InterfaceToTestImpl.class");
        ClassParser implementationParse = new ClassParser(implementationFile.getAbsolutePath());
        implementationClass = implementationParse.parse();
    }

    private boolean execution() throws IOException {
        Stream<Link> streamInterface = affectedClassVisitor.start(interfaceClass, context);
        List<Link> collectInterface = streamInterface.collect(Collectors.toList());
        assertTrue(collectInterface.isEmpty());

        Stream<Link> streamImplementation = affectedClassVisitor.start(implementationClass, context);
        List<Link> collectImplementation = streamImplementation.collect(Collectors.toList());
        assertFalse(collectImplementation.isEmpty());
        return true;
    }

    @Ignore("Proof that AffectedClassVisitor is not thread safe, do not run or will fail")
    @Test
    public void testThreadSafety() throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(THREADS);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean running = new AtomicBoolean();
        overlaps = new AtomicInteger();

        Collection<Future<Boolean>> futures = new ArrayList<>(THREADS);

        for (int i = 0; i < THREADS; i++) {
            futures.add(
                    service.submit(
                            () -> {
                                latch.await();
                                if (running.get()) {
                                    overlaps.incrementAndGet();
                                }
                                running.set(true);
                                boolean execution = execution();
                                running.set(false);
                                return execution;
                            }
                    )
            );
        }
        latch.countDown();
        List<Boolean> result = new ArrayList<>();
        for (Future<Boolean> f : futures) {
            result.add(f.get());
        }
        assertTrue(overlaps.get() > 0);
    }

    private static class MockForConcurrentChangeDetector implements ChangeDetector {

        @Override
        public ChangeScope<Change> getChanges(String fromChangeId, String toChangeId) {
            ChangeScope<Change> changeScope = new ChangeScopeImpl();
            changeScope.addChange(new SCMChange(TEST_PACKAGE.replace('.', '/') + "/InterfaceToTestImpl.java",
                    IntStream.range(8, 10).boxed().collect(Collectors.toSet()), ChangeType.MODIFY));
            return changeScope;
        }
    }

}
