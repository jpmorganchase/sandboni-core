package com.sandboni.core.engine.sta.connector;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.JiraVertex;
import com.sandboni.core.scm.CachedRepository;
import com.sandboni.core.scm.revision.RevInfo;

import java.util.Set;
import java.util.stream.Collectors;

import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.TRACKING_VERTEX;

public class JiraConnector implements Connector {
    private CachedRepository gitCommitDiff;

    public JiraConnector(String repository) {
        this.gitCommitDiff = new CachedRepository(repository);
    }

    @Override
    public void connect(Context context) {
        Set<Link> set = context.getLinks().filter(e -> !e.getCaller().isLineNumbersEmpty()).collect(Collectors.toSet());
        set.parallelStream().forEach(e -> {
            String filepath =  e.getCaller().getFilePath();
            Set<RevInfo> revInfo = gitCommitDiff.getJiraSet(filepath, e.getCaller().getLineNumbers());
            revInfo.forEach(entry -> {
                JiraVertex jira = new JiraVertex.Builder(TRACKING_VERTEX.getActor(), entry.getJiraId())
                        .withDate(entry.getDate())
                        .withRevisionId(entry.getRevisionId())
                        .build();
                context.addLink(LinkFactory.createInstance(context.getApplicationId(), jira, e.getCaller(), LinkType.METADATA));
            });
        });
    }

    @Override
    public boolean proceed(Context context) {
        return true;
    }
}
