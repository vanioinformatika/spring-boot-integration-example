package hu.vanio.spring.boot.integration;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.springframework.stereotype.Component;
import org.springframework.xml.validation.ValidationErrorHandler;

/**
 *
 * @author Gyula Szalai <gyula.szalai@vanio.hu>
 */
@Component
public class MtomAwareValidationErrorHandler implements ValidationErrorHandler {
    
    private final List errors = new ArrayList();
    
    private String [] skipElements;
    
    @Override
    public SAXParseException[] getErrors() {
        return (SAXParseException[]) errors.toArray(new SAXParseException[errors.size()]);
    }
    
    public void setSkipElements(String[] skipElements){
        this.skipElements = skipElements; 
    }
    
    @Override
    public void warning(SAXParseException ex) throws SAXException {
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {      
        String text = ex.getMessage();
        for (int i=0; skipElements != null && i < skipElements.length; i++){
            if (text.contains("'"+skipElements[i]+"' is a simple type")){
                System.out.println("Skipping validation for XSD element <" + skipElements[i] + ">.");
                return;
            }
        }
        errors.add(ex);
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        errors.add(ex);
    }
    
}
