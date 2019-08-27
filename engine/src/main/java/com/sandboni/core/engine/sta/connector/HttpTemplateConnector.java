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

public class HttpTemplateConnector implements Connector {

    private static final Pattern HTTP_PARAMETERS_PATTEN = Pattern.compile("\\{.+?}");

    @Override
    public void connect(Context context) {
        Set<Link> httpRequests = context.getLinks().filter(l -> l.getLinkType() == LinkType.HTTP_REQUEST).collect(Collectors.toSet());
        Set<Link> httpHandlers = context.getLinks().filter(l -> l.getLinkType() == LinkType.HTTP_HANLDER).collect(Collectors.toSet());

        httpRequests.forEach(r -> {
            Optional<Link> matchingHandler = httpHandlers.stream()
                    .filter(l ->  isMatch(r.getCallee().getAction(), l.getCaller().getAction())).findAny();
            matchingHandler.ifPresent(link -> context.addLink(LinkFactory.createInstance(r.getCallee(), link.getCaller(), LinkType.HTTP_MAP)));
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
        if (callerTemplate.equals(calleeTemplate)) {
            return false;
        }

        // trivial 'extra parent path' case
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
}