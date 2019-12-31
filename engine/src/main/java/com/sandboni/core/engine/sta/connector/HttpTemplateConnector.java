package com.sandboni.core.engine.sta.connector;

import com.google.gson.Gson;
import com.sandboni.core.engine.contract.JsonEntry;
import com.sandboni.core.engine.exception.ParseRuntimeException;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;

public class HttpTemplateConnector implements Connector {

    private static final Pattern HTTP_PARAMETERS_PATTEN = Pattern.compile("\\{.+?}");

    private static final String CUCUMBER_ENTRY = "CUCUMBER";

    private static final Logger log = LoggerFactory.getLogger(HttpTemplateConnector.class);

    @Override
    public void connect(Context context) {
        Set<Link> httpRequests = context.getLinks().filter(l -> l.getLinkType() == LinkType.HTTP_REQUEST).collect(Collectors.toSet());

        Set<Link> httpHandlers = context.getLinks().filter(l -> l.getLinkType() == LinkType.HTTP_HANLDER).collect(Collectors.toSet());

        httpRequests.forEach(r ->
                httpHandlers.stream()
                        .filter(l -> isMatch(r.getCallee().getAction(), l.getCaller().getAction()))
                        .forEach(link -> context.addLink(LinkFactory.createInstance(context.getApplicationId(), r.getCallee(), link.getCaller(), LinkType.HTTP_MAP)))
        );

        //handling Seloni json entries
        JsonEntry[] seloniEntries = getEntriesFromSeloniFile(context);

        Arrays.stream(seloniEntries).forEach(entry -> {
            Vertex vertex = createVertex(entry);
            context.addLink(LinkFactory.createInstance(context.getApplicationId(), START_VERTEX, vertex, LinkType.ENTRY_POINT));
            entry.getUrls().forEach(url ->
                httpHandlers.stream()
                        .filter(l -> isMatch(url, l.getCaller().getAction())).forEach(link -> {
                    context.addLink(LinkFactory.createInstance(context.getApplicationId(), vertex, link.getCaller(), LinkType.HTTP_MAP_SELONI));
                    log.debug("external link: `{}`: {} -> {}  :: {} -> {}", url, link.getCaller().getActor(), link.getCaller().getAction(), link.getCallee().getActor(), link.getCallee().getAction());
                })
            );
        });
    }

    @Override
    public boolean proceed(Context context) {
        return context.isAdoptedLinkType(LinkType.HTTP_REQUEST, LinkType.HTTP_HANLDER);
    }

    // UrlTemplate match is a tricky generic problem.
    // in our scenario standard logic can't apply - tests are not guaranteed to use the same full template
    // so we use some heuristics here
    public boolean isMatch(String callerTemplate, String calleeTemplate) {

        // this trivial case which doesn't need any extra dependency created
        if (callerTemplate.endsWith(calleeTemplate)) {
            return true;
        }

        Matcher calleeMatcher = HTTP_PARAMETERS_PATTEN.matcher(calleeTemplate);
        if (calleeMatcher.find()) {
            Matcher callerMatcher = HTTP_PARAMETERS_PATTEN.matcher(callerTemplate);
            if (callerMatcher.find()) {

                // trivial 'different param names' case
                String callerWithNoPlaceholders = callerMatcher.replaceAll("");
                String calleeWithNoPlaceholders = calleeMatcher.replaceAll("");

                return callerWithNoPlaceholders.endsWith(calleeWithNoPlaceholders);
            } else {
                //poor man Url template matching
                String calleePattern = calleeMatcher.replaceAll("\\\\w*");

                return Pattern.matches(calleePattern, callerTemplate);
            }
        }

        return false;
    }

    private Vertex createVertex(JsonEntry entry) {
        if (CUCUMBER_ENTRY.equals(entry.getType())) {
            return new CucumberVertex.Builder(entry.getClassName(), entry.getTestName())
                    .withFeaturePath(entry.getFilepath())
                    .withScenarioLine(Integer.parseInt(entry.getLineNumber()))
                    .markAsExternalLocation()
                    .build();
        }else {
            return new TestVertex.Builder(entry.getClassName(), entry.getTestName())
                    .withFilePath(entry.getFilepath())
                    .markAsExternalLocation()
                    .build();
        }
    }

    public static JsonEntry[] getEntriesFromSeloniFile(Context context) {
        String filePath = context.getSeloniFilepath();
        if (!Objects.isNull(filePath)){
            log.info("'seloni.filepath' set to '{}'", filePath);
            return parseFile(new File(filePath));
        }
        return new JsonEntry[0];
    }

    private static JsonEntry[] parseFile(File file) {
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()))) {
            return gson.fromJson(reader, JsonEntry[].class);
        } catch (IOException e) {
            throw new ParseRuntimeException(e);
        }
    }
}