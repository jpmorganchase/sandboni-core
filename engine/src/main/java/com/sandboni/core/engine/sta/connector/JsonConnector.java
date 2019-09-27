package com.sandboni.core.engine.sta.connector;

import com.google.gson.Gson;
import com.sandboni.core.engine.contract.JsonEntry;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.CUCUMBER_VERTEX;

public class JsonConnector implements Connector {
    private static final Logger log = LoggerFactory.getLogger(JsonConnector.class);
    private static final String DEFAULT_SELONI_FILE_PATH = "./src/test/resources/Seloni.json";
    private final Gson gson;

    public JsonConnector() {
        gson = new Gson();
    }

    @Override
    public void connect(Context context) {
        String seloniFilepath = System.getProperty("Seloni.filepath");
        File file = new File(seloniFilepath == null ? DEFAULT_SELONI_FILE_PATH : seloniFilepath);
        if (!file.exists()) {
            return;
        }
        JsonEntry[] entries = fromFile(file);
        toLinks(entries, context).forEach(context::addLinks);
    }

    @Override
    public boolean proceed(Context context) {
        return true;
    }

    private JsonEntry[] fromFile(File file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()))) {
            return gson.fromJson(reader, JsonEntry[].class);
        } catch (IOException e) {
            log.error("Error during deserialize file", e);
        }
        return new JsonEntry[0];
    }

    private List<Link> toLinks(JsonEntry[] entries, Context context) {
        List<Link> links = new ArrayList<>();
        if (isNotEmpty(entries)) {
            for(JsonEntry entry: entries) {
                if (entry.getUrls() == null) {
                    continue;
                }
                context.getLinks()
                        .filter(l -> l.getCallee().getActor().equals(entry.getClassName()) &&
                                l.getCallee().getAction().equals(entry.getTestName()) &&
                                l.getLinkType().equals(LinkType.ENTRY_POINT))
                        .map(Link::getCallee)
                        .findFirst()
                        .ifPresent(scenarioVertex -> {
                            for(String url: entry.getUrls()) {
                                links.add(LinkFactory.createInstance(
                                        context.getApplicationId(), scenarioVertex,
                                        new CucumberVertex.Builder(CUCUMBER_VERTEX.getActor(), url)
                                                .build(),
                                        LinkType.HTTP_REQUEST));
                            }
                        });
            }
        }
        return links;
    }

    private boolean isNotEmpty(JsonEntry[] entries) {
        return entries != null && entries.length > 0;
    }
}
