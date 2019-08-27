package com.sandboni.core.engine.finder.explicit;

import javax.xml.bind.annotation.XmlAttribute;

class Link {

    @XmlAttribute
    public String caller;

    @XmlAttribute
    public String callerAction;

    @XmlAttribute
    public String callee;

    @XmlAttribute
    public String calleeAction;

    public Link() {
        // needed for serialization
    }
}
