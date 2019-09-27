package com.sandboni.core.engine.finder.explicit;

import com.sandboni.core.engine.contract.ThrowingBiConsumer;
import com.sandboni.core.engine.finder.ExtensionType;
import com.sandboni.core.engine.finder.FileTreeFinder;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ExplicitFinder extends FileTreeFinder {

    static com.sandboni.core.engine.sta.graph.Link toLink(String applicationId, Link l) {
        return LinkFactory.createInstance(applicationId,
                new Vertex.Builder(l.caller, l.callerAction).build(),
                new Vertex.Builder(l.callee, l.calleeAction).build(),
                LinkType.MANUAL);
    }

    @Override
    protected Map<String, ThrowingBiConsumer<File, Context>> getConsumers() {
        HashMap<String, ThrowingBiConsumer<File, Context>> map = new HashMap<>();
        map.put(ExtensionType.SANDBONI.type(), (file, context) -> {

            JAXBContext jContext = JAXBContext.newInstance(Links.class);
            Unmarshaller unmarshaller = jContext.createUnmarshaller();

            Links links = (Links) unmarshaller.unmarshal(file);
            links.link.forEach(l -> context.addLink(toLink(context.getApplicationId(), l)));
        });

        return map;
    }
}


