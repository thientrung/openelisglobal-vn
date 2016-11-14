package com.mirth.connect.connectors.ws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 3.1.6
 * 2016-09-16T15:23:45.912+07:00
 * Generated source version: 3.1.6
 * 
 */
@WebServiceClient(name = "DefaultAcceptMessageService", 
                  wsdlLocation = "http://120.72.83.85:8843/services/Mirth?wsdl",
                  targetNamespace = "http://ws.connectors.connect.mirth.com/") 
public class DefaultAcceptMessageService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://ws.connectors.connect.mirth.com/", "DefaultAcceptMessageService");
    public final static QName DefaultAcceptMessagePort = new QName("http://ws.connectors.connect.mirth.com/", "DefaultAcceptMessagePort");
    static {
        URL url = null;
        try {
            url = new URL("http://120.72.83.85:8843/services/Mirth?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(DefaultAcceptMessageService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://120.72.83.85:8843/services/Mirth?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public DefaultAcceptMessageService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public DefaultAcceptMessageService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DefaultAcceptMessageService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    public DefaultAcceptMessageService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public DefaultAcceptMessageService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public DefaultAcceptMessageService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }    




    /**
     *
     * @return
     *     returns DefaultAcceptMessage
     */
    @WebEndpoint(name = "DefaultAcceptMessagePort")
    public DefaultAcceptMessage getDefaultAcceptMessagePort() {
        return super.getPort(DefaultAcceptMessagePort, DefaultAcceptMessage.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns DefaultAcceptMessage
     */
    @WebEndpoint(name = "DefaultAcceptMessagePort")
    public DefaultAcceptMessage getDefaultAcceptMessagePort(WebServiceFeature... features) {
        return super.getPort(DefaultAcceptMessagePort, DefaultAcceptMessage.class, features);
    }

}
