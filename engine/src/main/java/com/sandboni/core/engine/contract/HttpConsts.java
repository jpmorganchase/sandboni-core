package com.sandboni.core.engine.contract;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class HttpConsts {
    public static final Pattern RELATIVE_URL = Pattern.compile("^((?<proto>\\w+)://[^/]+?(?<port>:\\d+)?)?(/.+)*(\\?.+)?");

    public static final String TEMPLATE_PATTERN = "\\{.+\\}";

    // should really be curly-braced-param
    public static final String TEMPLATE_REPLACE = "";

    public static final String GET_METHOD = "GET";

    private static final Set<String> HTTP_VERB = new HashSet<>(Arrays.asList(GET_METHOD, "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"));

    public static final String HTTP_LOCALHOST = "http://localhost/*";

    private HttpConsts() {
    }

    public static Set<String> getHttpVerb() {
        return Collections.unmodifiableSet(HTTP_VERB);
    }

}
