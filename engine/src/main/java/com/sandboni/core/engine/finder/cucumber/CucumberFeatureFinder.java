package com.sandboni.core.engine.finder.cucumber;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.common.ExtensionType;
import com.sandboni.core.engine.finder.FileTreeFinder;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import cucumber.runtime.FeatureBuilder;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberTagStatement;
import gherkin.formatter.model.Range;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;
import gherkin.formatter.model.TagStatement;
import gherkin.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.CUCUMBER_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;

public class CucumberFeatureFinder extends FileTreeFinder {

    @Override
    protected Map<String, ThrowingBiConsumer<File, Context>> getConsumers() {
        HashMap<String, ThrowingBiConsumer<File, Context>> map = new HashMap<>();
        map.put(ExtensionType.FEATURE.type(), (file, context) -> toLinks(context, file).forEach(context::addLink));
        return map;
    }

    public static List<Link> toLinks(Context context, File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
        return toLinks(context, file, content);
    }

    public static List<Link> toLinks(Context context, File file, String content) {
        Set<Integer> affectedLines = getAffectedLines(context, file);

        List<Link> links = new LinkedList<>();

        List<CucumberFeature> features = getCucumberFeatures(content, file.getAbsolutePath());
        for (CucumberFeature feature : features) {
            String featureName = feature.getGherkinFeature().getName();

            List<CucumberTagStatement> statements = feature.getFeatureElements();
            for (CucumberTagStatement statement : statements) {
                TagStatement scenario = statement.getGherkinModel();
                String scenarioName = scenario.getName();

                CucumberVertex scenarioVertex = new CucumberVertex.Builder(featureName, scenarioName)
                        .withFeaturePath(feature.getPath())
                        .markAffected(isAffected(scenario, statement, affectedLines))
                        .withScenarioLine(scenario.getLine())
                        .build();
                links.add(LinkFactory.createInstance(context.getApplicationId(), START_VERTEX, scenarioVertex, LinkType.ENTRY_POINT));

                for (Step step : statement.getSteps()) {
                    CucumberVertex stepVertex = new CucumberVertex.Builder(CUCUMBER_VERTEX.getActor(), step.getName())
                            .build();
                    links.add(LinkFactory.createInstance(context.getApplicationId(), scenarioVertex, stepVertex, LinkType.CUCUMBER_TEST));
                }

                for (Tag tag : scenario.getTags()) {
                    String tagName = tag.getName().substring(1);
                    CucumberVertex tagVertex = new CucumberVertex.Builder(CUCUMBER_VERTEX.getActor(), tagName)
                            .build();
                    if (tagName.startsWith("/")) {
                        links.add(LinkFactory.createInstance(context.getApplicationId(), scenarioVertex, tagVertex, LinkType.HTTP_REQUEST));
                    } else {
                        links.add(LinkFactory.createInstance(context.getApplicationId(), scenarioVertex, tagVertex, LinkType.CUCUMBER_TEST_TAG));
                    }
                }
            }
        }
        return links;
    }

    private static boolean isAffected(TagStatement scenario, CucumberTagStatement statement, Set<Integer> affectedLines) {
        return !affectedLines.isEmpty() && affectedLines.stream().anyMatch(getScenarioRange(scenario, statement)::isInclude);
    }

    private static Set<Integer> getAffectedLines(Context context, File file) {
        String affectedFeature = context.getChangeScope().getAllAffectedClasses().stream().filter(c -> c.endsWith(file.getName())).findFirst().orElse(null);
        return affectedFeature == null ? Collections.emptySet() : context.getChangeScope().getAllLinesChanged(affectedFeature);
    }

    private static Range getScenarioRange(TagStatement scenario, CucumberTagStatement statement) {
        int start = scenario.getTags().isEmpty() ? scenario.getLine() : scenario.getTags().get(0).getLine();
        int end = statement.getSteps().isEmpty() ? scenario.getLine() : statement.getSteps().get(statement.getSteps().size() - 1).getLineRange().getLast();
        return new Range(start, end);
    }

    private static List<CucumberFeature> getCucumberFeatures(String featureContent, String featurePath) {
        final List<CucumberFeature> features = new ArrayList<>();
        final FeatureBuilder builder = new FeatureBuilder(features);
        Parser parser = new Parser(builder);
        parser.parse(featureContent, featurePath, 0);
        features.forEach(feature -> feature.setI18n(parser.getI18nLanguage()));
        return features;
    }

    public static List<String> getCucumberSteps() {
        return Arrays.asList("When", "And", "Then", "Given", "But");
    }
}