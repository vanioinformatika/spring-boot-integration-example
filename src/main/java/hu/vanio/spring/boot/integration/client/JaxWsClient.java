package hu.vanio.spring.boot.integration.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.soap.SOAPBinding;

import hu.vanio.springwsmtom.wstypes.ContentStoreHttpPort;
import hu.vanio.springwsmtom.wstypes.ContentStoreHttpPortService;
import hu.vanio.springwsmtom.wstypes.LoadContentRequest;
import hu.vanio.springwsmtom.wstypes.LoadContentResponse;
import hu.vanio.springwsmtom.wstypes.ObjectFactory;
import hu.vanio.springwsmtom.wstypes.StoreContentRequest;
import hu.vanio.springwsmtom.wstypes.StoreContentResponse;

/**
 * Simple JAX-WS MTOM enabled client
 *
 * @author Gyula Szalai <gyula.szalai@vanio.hu>
 */
public class JaxWsClient {

    /** JAXB object factory */
    private final ObjectFactory objectFactory = new ObjectFactory();
    
    /** Inserts a WS-Security header to the SOAP message */
    private final WsSecurityHandler wsSecurityHandler = new WsSecurityHandler("Bert", "Ernie");
    
    /**
     * Sends the specified content file to the WebService
     * 
     * @param name The name of the content to be stored
     * @param content The content to be stored
     * @return The message that tthe server sent back
     */
    public String storeContent(String name, DataHandler content) {
        ContentStoreHttpPortService service = new ContentStoreHttpPortService();
        
        service.setHandlerResolver(new HandlerResolver() {
            @Override
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                List<Handler> handlerList = new ArrayList<>();
                handlerList.add(wsSecurityHandler);
                return handlerList;
            }
        });
        
        ContentStoreHttpPort contentStorePort = service.getContentStoreHttpPortSoap11();
        SOAPBinding binding = (SOAPBinding) ((BindingProvider) contentStorePort).getBinding();
        binding.setMTOMEnabled(true);

        StoreContentRequest request = objectFactory.createStoreContentRequest();
        request.setName(name);
        request.setContent(content);
        
        StoreContentResponse response = contentStorePort.storeContent(request);
        return response.getMessage();
    }
    
    /**
     * Loads the content with the specified name from the WebService
     * 
     * @param name The name of the content
     * @return The loaded content
     * @throws IOException If an IO error occurs
     */
    public DataHandler loadContent(String name) throws IOException {
        ContentStoreHttpPortService service = new ContentStoreHttpPortService();
        
        service.setHandlerResolver(new HandlerResolver() {
            @Override
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                List<Handler> handlerList = new ArrayList<>();
                handlerList.add(wsSecurityHandler);
                return handlerList;
            }
        });
        
        ContentStoreHttpPort loadContentPort = service.getContentStoreHttpPortSoap11();
        SOAPBinding binding = (SOAPBinding) ((BindingProvider) loadContentPort).getBinding();
        binding.setMTOMEnabled(true);

        LoadContentRequest request = objectFactory.createLoadContentRequest();
        request.setName(name);
        LoadContentResponse response = loadContentPort.loadContent(request);
        DataHandler content = response.getContent();
        return content;
    }

}
