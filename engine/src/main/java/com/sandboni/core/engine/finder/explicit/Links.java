package com.sandboni.core.engine.finder.explicit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(namespace = Links.NAMESPACE)
public class Links {

    static final String NAMESPACE = "urn:sandboni-links";

    @XmlAttribute()
    public String location;

    @XmlElement(namespace = NAMESPACE)
    public List<Link> link = new ArrayList<>();

    public Links() {
        // needed for serialization
    }
}
