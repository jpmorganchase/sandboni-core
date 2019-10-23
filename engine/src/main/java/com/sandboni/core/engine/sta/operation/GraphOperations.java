package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.common.CachingSupplier;
import com.sandboni.core.engine.result.ChangeStats;
import com.sandboni.core.engine.result.FormattedChangeStats;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.executor.OperationExecutor;
import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class GraphOperations {

    private final OperationExecutor operationExecutor;
    private final Context context;
    private final Supplier<Set<TestVertex>> relatedTestsSupplier = new CachingSupplier<>(this::getRelatedTestsImpl);
    private final Supplier<Set<TestVertex>> allTestsSupplier = new CachingSupplier<>(this::getAllTestsImpl);
    private final Supplier<Set<TestVertex>> disconnectedTestsSupplier = new CachingSupplier<>(this::getDisconnectedTestsImpl);
    private final Supplier<Set<TestVertex>> allExternalTestsSupplier = new CachingSupplier<>(this::getAllExternalTestsImpl);
    private final Supplier<Set<Vertex>> changesSupplier = new CachingSupplier<>(this::getChangesImpl);
    private final Supplier<Map<String, Set<String>>> changesMapSupplier = new CachingSupplier<>(this::getChangesMapImpl);
    private final Supplier<Set<Edge>> allReachableEdgesSupplier = new CachingSupplier<>(this::getAllReachableEdgesImpl);
    private final Supplier<Map<String, Set<String>>> unreachableChangesSupplier = new CachingSupplier<>(this::getUnreachableChangesImpl);
    private final Supplier<Set<String>> jiraTrackingSupplier = new CachingSupplier<>(this::getJiraTrackingImpl);
    private final Supplier<Set<Edge>> jiraRelatedTestsSupplier = new CachingSupplier<>(this::getJiraRelatedTestsImpl);
    private final Supplier<Set<Vertex>> allRelatedNodesSupplier = new CachingSupplier<>(this::getAllRelatedNodesImpl);
    private final Supplier<Long> reachableLineNumberCountSupplier = new CachingSupplier<>(this::getReachableLineNumberCountImpl);
    private final Supplier<Map<Vertex, ChangeStats>> changeStatsSupplier = new CachingSupplier<>(this::getChangeStatsImpl);
    private final Supplier<Set<FormattedChangeStats>> formattedChangeStatsSupplier = new CachingSupplier<>(this::getFormattedChangeStatsImpl);

    public GraphOperations(Graph<Vertex, Edge> graph, Context context) {
        operationExecutor = new OperationExecutor(graph);
        this.context = context;
    }

    public Set<TestVertex> getAllTests() {
        return allTestsSupplier.get();
    }
    private Set<TestVertex> getAllTestsImpl() {
        return operationExecutor.execute(new AllTestsOperation());
    }

    public Set<TestVertex> getAllExternalUnitTests() {
        return operationExecutor.execute(new UnitTestsOperation(allExternalTestsSupplier.get()));
    }
    public Set<CucumberVertex> getAllExternalCucumberTests() {
        return operationExecutor.execute(new CucumberTestsOperation(allExternalTestsSupplier.get()));
    }
    private Set<TestVertex> getAllExternalTestsImpl() {
        return allTestsSupplier.get();
    }

    public Set<Vertex> getChanges() {
        return changesSupplier.get();
    }
    private Set<Vertex> getChangesImpl() {
        return operationExecutor.execute(new ChangesOperation());
    }

    public Map<String, Set<String>> getChangesMap() {
        return changesMapSupplier.get();
    }
    private Map<String, Set<String>> getChangesMapImpl() {
        return operationExecutor.execute(new ChangesMapOperation(changesSupplier.get()));
    }

    public Set<TestVertex> getRelatedTests() {
        return relatedTestsSupplier.get();
    }
    private Set<TestVertex> getRelatedTestsImpl() {
        return operationExecutor.execute(new RelatedTestsOperation(allTestsSupplier.get()));
    }

    public Set<TestVertex> getDisconnectedTests() {
        return disconnectedTestsSupplier.get();
    }
    private Set<TestVertex> getDisconnectedTestsImpl() {
        return operationExecutor.execute(new DisconnectedTestsOperation(allTestsSupplier.get(), relatedTestsSupplier.get(), context.getSrcLocations()));
    }

    public Map<String, Set<String>> getUnreachableChanges() {
        return unreachableChangesSupplier.get();
    }
    private Map<String, Set<String>> getUnreachableChangesImpl() {
        return operationExecutor.execute(new UnreachableChangesOperation());
    }

    public Set<String> getJiraTracking() {
        return jiraTrackingSupplier.get();
    }
    private Set<String> getJiraTrackingImpl() {
        return operationExecutor.execute(new JiraTrackingOperation(jiraRelatedTestsSupplier.get()));
    }

    public Set<Edge> getJiraRelatedTests() {
        return jiraRelatedTestsSupplier.get();
    }
    private Set<Edge> getJiraRelatedTestsImpl() {
        return operationExecutor.execute(new JiraRelatedTestsOperation(allReachableEdgesSupplier.get(), disconnectedTestsSupplier.get()));
    }

    public Set<Edge> getAllReachableEdges() {
        return allReachableEdgesSupplier.get();
    }
    private Set<Edge> getAllReachableEdgesImpl() {
        return operationExecutor.execute(new AllReachableEdgesOperation());
    }

    public Set<Vertex> getAllRelatedNodes() {
        return allRelatedNodesSupplier.get();
    }
    private Set<Vertex> getAllRelatedNodesImpl() {
        return operationExecutor.execute(new AllRelatedNodesOperation(allReachableEdgesSupplier.get()));
    }

    public Long getReachableLineNumberCount() {
        return reachableLineNumberCountSupplier.get();
    }
    private Long getReachableLineNumberCountImpl() {
        return operationExecutor.execute(new ReachableLineNumberCountOperation(allReachableEdgesSupplier.get(), allTestsSupplier.get()));
    }

    public Map<Vertex, ChangeStats> getChangeStats() {
        return changeStatsSupplier.get();
    }
    private Map<Vertex, ChangeStats> getChangeStatsImpl() {
        return operationExecutor.execute(new ChangeStatsOperation(changesSupplier.get(), allTestsSupplier.get()));
    }

    public Set<FormattedChangeStats> getFormattedChangeStats() {
        return formattedChangeStatsSupplier.get();
    }
    private Set<FormattedChangeStats> getFormattedChangeStatsImpl() {
        return operationExecutor.execute(new FormattedChangeStatsOperation(changeStatsSupplier.get()));
    }

    public Set<TestVertex> getUnitRelatedTests() {
        return operationExecutor.execute(new UnitTestsOperation(relatedTestsSupplier.get()));
    }

    public Set<TestVertex> getUnitDisconnectedTests() {
        return operationExecutor.execute(new UnitTestsOperation(disconnectedTestsSupplier.get()));
    }

    public Set<CucumberVertex> getCucumberRelatedTests() {
        return operationExecutor.execute(new CucumberTestsOperation(relatedTestsSupplier.get()));
    }
    public Set<CucumberVertex> getCucumberDisconnectedTests() {
        return operationExecutor.execute(new CucumberTestsOperation(disconnectedTestsSupplier.get()));
    }

    public Set<TestVertex> getUnitRelatedExternalTests() {
        return operationExecutor.execute(new UnitTestsOperation(relatedTestsSupplier.get(), true));
    }

    public Set<CucumberVertex> getCucumberRelatedExternalTests() {
        return operationExecutor.execute(new CucumberTestsOperation(relatedTestsSupplier.get(), true));
    }

    public Set<TestVertex> getIncludedByAnnotationTest() {
        return operationExecutor.execute(new IncludedTestOperation(allTestsSupplier.get()));
    }
}
