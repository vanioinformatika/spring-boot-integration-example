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
 * A SOAP üzenetbe beilleszt a megadott autentikációs adatokat tartalmazó WS-Security SOAP header-t
 * 
 * @author Gyula Szalai <gyula.szalai@vanio.hu>
 */
public class WsSecurityHandler implements SOAPHandler<SOAPMessageContext> {

    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(WsSecurityHandler.class);
    
    /** A felhasználónév */
    private final String userName;
    /** A jelszó hash */
    private final String password;

    /**
     * Konstruktor
     * @param userName A felhasználónév
     * @param password A jelszó hash
     */
    public WsSecurityHandler(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    
    public boolean handleMessage(SOAPMessageContext context) {
        System.out.println("***************************************- WS-Security handler running");
        String prefixUri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-";
        String uri = prefixUri + "wssecurity-secext-1.0.xsd";
        String uta = prefixUri + "wssecurity-utility-1.0.xsd";
        String ta = prefixUri + "username-token-profile-1.0#PasswordText";
        Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outboundProperty.booleanValue()) {
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
                logger.error("Hiba a WsSecurityHandler-ben: ", e);
            }
        }
        return true;
    }

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    public void close(MessageContext context) {
    }
    
}
