package com.sandboni.core.engine.result;

public class ChangeStats {

    private final long relatedTests; // Number of test for this change
    private final long impactedCodeElements; // Sum of paths from this change to adjacent nodes
    private final double relatedTestsPercent; // Percentage of the number of tests for this change with the overall project
    private final double impactedCodeElementsPercent; // Percentage of the affected elements for this change with the overall project

    public ChangeStats(long relatedTests, long impactedCodeElements, double relatedTestsPercent, double impactedCodeElementsPercent) {
        this.relatedTests = relatedTests;
        this.impactedCodeElements = impactedCodeElements;
        this.relatedTestsPercent = relatedTestsPercent;
        this.impactedCodeElementsPercent = impactedCodeElementsPercent;
    }

    public long getRelatedTests() {
        return relatedTests;
    }

    public long getImpactedCodeElements() {
        return impactedCodeElements;
    }

    public double getRelatedTestsPercent() {
        return relatedTestsPercent;
    }

    public double getImpactedCodeElementsPercent() {
        return impactedCodeElementsPercent;
    }

    @Override
    public String toString() {
        return String.format("Related tests: %d, Related tests percent: %f, Impacted code elements: %d",
                relatedTests, relatedTestsPercent, impactedCodeElements);
    }
}
