package com.sandboni.core.engine.sta.connector;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CucumberJavaConnector implements Connector {
    @Override
    public void connect(Context context) {
        Set<Link> cucumberSource = context.getLinks().filter(l -> l.getLinkType() == LinkType.CUCUMBER_SOURCE).collect(Collectors.toSet());
        Set<Link> cucumberTest = context.getLinks().filter(l -> l.getLinkType() == LinkType.CUCUMBER_TEST).collect(Collectors.toSet());

        cucumberSource.forEach(s -> {
            Optional<Link> matchingHandler = cucumberTest.stream()
                    .filter(t -> s.getCaller().getActor().equals(t.getCallee().getActor())
                            && isMatch(s.getCaller().getAction(), t.getCallee().getAction())).findAny();
            matchingHandler.ifPresent(link -> context.addLink(LinkFactory.createInstance(context.getApplicationId(), link.getCallee(), s.getCaller(), LinkType.CUCUMBER_MAP)));
        });
    }

    @Override
    public boolean proceed(Context context) {
        return context.isAdoptedLinkType(LinkType.CUCUMBER_SOURCE, LinkType.CUCUMBER_TEST);
    }

    public boolean isMatch(String callerTemplate, String calleeTemplate) {
        Pattern pattern = Pattern.compile(callerTemplate);
        Matcher matcher = pattern.matcher(calleeTemplate);
        return matcher.find();
    }
}
