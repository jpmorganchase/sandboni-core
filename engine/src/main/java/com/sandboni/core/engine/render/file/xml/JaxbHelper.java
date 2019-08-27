package com.sandboni.core.engine.render.file.xml;

import com.sandboni.core.engine.exception.RendererException;
import com.sandboni.core.engine.render.file.FormatHelper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class JaxbHelper implements FormatHelper {

    @Override
    public <T> String marshal(T modelClassObject) throws RendererException {
        try {
            Marshaller jaxbMarshaller = JAXBContext.newInstance(modelClassObject.getClass()).createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StringWriter stringWriter = new StringWriter();
            jaxbMarshaller.marshal(modelClassObject, stringWriter);

            return stringWriter.toString();

        } catch (JAXBException e) {
           throw new RendererException("Error marshalling object to XML", e);
        }
    }
}
