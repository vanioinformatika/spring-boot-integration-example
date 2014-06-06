package hu.vanio.spring.boot.integration.client;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inserts a WS-Security header into the SOAP message to be sent
  * 
 * @author Gyula Szalai <gyula.szalai@vanio.hu>
 */
public class WsSecurityHandler implements SOAPHandler<SOAPMessageContext> {

    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(WsSecurityHandler.class);
    
    /** Username */
    private final String userName;
    /** Password */
    private final String password;

    /**
     * Constructs a new instance
     * @param userName Username
     * @param password Password
     */
    public WsSecurityHandler(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    
    /**
     * Entry point
     * @param context SOAP message context
     * @return true, if processing may continue
     */
    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        String prefixUri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-";
        String uri = prefixUri + "wssecurity-secext-1.0.xsd";
        String uta = prefixUri + "wssecurity-utility-1.0.xsd";
        String ta = prefixUri + "username-token-profile-1.0#PasswordText";
        Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outboundProperty) {
            try {
                SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                SOAPFactory factory = SOAPFactory.newInstance();
                String prefix = "wsse";
                SOAPElement securityElem = factory.createElement("Security", prefix, uri);
                SOAPElement tokenElem = factory.createElement("UsernameToken", prefix, uri);
                tokenElem.addAttribute(QName.valueOf("wsu:Id"), "UsernameToken-2");
                tokenElem.addAttribute(QName.valueOf("xmlns:wsu"), uta);
                SOAPElement userElem = factory.createElement("Username", prefix, uri);
                userElem.addTextNode(this.userName);
                SOAPElement pwdElem = factory.createElement("Password", prefix, uri);
                pwdElem.addTextNode(this.password);
                pwdElem.addAttribute(QName.valueOf("Type"), ta);
                tokenElem.addChildElement(userElem);
                tokenElem.addChildElement(pwdElem);
                securityElem.addChildElement(tokenElem);
                SOAPHeader header = envelope.getHeader();
                if (header == null) {
                    header = envelope.addHeader();
                }
                header.addChildElement(securityElem);
            } catch (SOAPException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {
    }
    
}
