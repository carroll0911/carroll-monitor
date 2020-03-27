//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.11 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2017.12.07 时间 04:16:48 PM CST 
//


package com.carroll.monitor.analyzer.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.carroll.monitor.analyzer.ws package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TemplateQueryInterface_QNAME = new QName("http://webservice.usp.svw.com/", "TemplateQueryInterface");
    private final static QName _TemplateQueryInterfaceResponse_QNAME = new QName("http://webservice.usp.svw.com/", "TemplateQueryInterfaceResponse");
    private final static QName _SendTimingMessageInterface_QNAME = new QName("http://webservice.usp.svw.com/", "sendTimingMessageInterface");
    private final static QName _SendTimingMessageInterfaceResponse_QNAME = new QName("http://webservice.usp.svw.com/", "sendTimingMessageInterfaceResponse");
    private final static QName _SendRealtimeMessageInterface_QNAME = new QName("http://webservice.usp.svw.com/", "sendRealtimeMessageInterface");
    private final static QName _SendRealtimeMessageInterfaceResponse_QNAME = new QName("http://webservice.usp.svw.com/", "sendRealtimeMessageInterfaceResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.carroll.monitor.analyzer.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TemplateQueryInterface }
     * 
     */
    public TemplateQueryInterface createTemplateQueryInterface() {
        return new TemplateQueryInterface();
    }

    /**
     * Create an instance of {@link TemplateQueryInterfaceResponse }
     * 
     */
    public TemplateQueryInterfaceResponse createTemplateQueryInterfaceResponse() {
        return new TemplateQueryInterfaceResponse();
    }

    /**
     * Create an instance of {@link SendTimingMessageInterface }
     * 
     */
    public SendTimingMessageInterface createSendTimingMessageInterface() {
        return new SendTimingMessageInterface();
    }

    /**
     * Create an instance of {@link SendTimingMessageInterfaceResponse }
     * 
     */
    public SendTimingMessageInterfaceResponse createSendTimingMessageInterfaceResponse() {
        return new SendTimingMessageInterfaceResponse();
    }

    /**
     * Create an instance of {@link SendRealtimeMessageInterface }
     * 
     */
    public SendRealtimeMessageInterface createSendRealtimeMessageInterface() {
        return new SendRealtimeMessageInterface();
    }

    /**
     * Create an instance of {@link SendRealtimeMessageInterfaceResponse }
     * 
     */
    public SendRealtimeMessageInterfaceResponse createSendRealtimeMessageInterfaceResponse() {
        return new SendRealtimeMessageInterfaceResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemplateQueryInterface }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.usp.svw.com/", name = "TemplateQueryInterface")
    public JAXBElement<TemplateQueryInterface> createTemplateQueryInterface(TemplateQueryInterface value) {
        return new JAXBElement<TemplateQueryInterface>(_TemplateQueryInterface_QNAME, TemplateQueryInterface.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemplateQueryInterfaceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.usp.svw.com/", name = "TemplateQueryInterfaceResponse")
    public JAXBElement<TemplateQueryInterfaceResponse> createTemplateQueryInterfaceResponse(TemplateQueryInterfaceResponse value) {
        return new JAXBElement<TemplateQueryInterfaceResponse>(_TemplateQueryInterfaceResponse_QNAME, TemplateQueryInterfaceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendTimingMessageInterface }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.usp.svw.com/", name = "sendTimingMessageInterface")
    public JAXBElement<SendTimingMessageInterface> createSendTimingMessageInterface(SendTimingMessageInterface value) {
        return new JAXBElement<SendTimingMessageInterface>(_SendTimingMessageInterface_QNAME, SendTimingMessageInterface.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendTimingMessageInterfaceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.usp.svw.com/", name = "sendTimingMessageInterfaceResponse")
    public JAXBElement<SendTimingMessageInterfaceResponse> createSendTimingMessageInterfaceResponse(SendTimingMessageInterfaceResponse value) {
        return new JAXBElement<SendTimingMessageInterfaceResponse>(_SendTimingMessageInterfaceResponse_QNAME, SendTimingMessageInterfaceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendRealtimeMessageInterface }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.usp.svw.com/", name = "sendRealtimeMessageInterface")
    public JAXBElement<SendRealtimeMessageInterface> createSendRealtimeMessageInterface(SendRealtimeMessageInterface value) {
        return new JAXBElement<SendRealtimeMessageInterface>(_SendRealtimeMessageInterface_QNAME, SendRealtimeMessageInterface.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendRealtimeMessageInterfaceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.usp.svw.com/", name = "sendRealtimeMessageInterfaceResponse")
    public JAXBElement<SendRealtimeMessageInterfaceResponse> createSendRealtimeMessageInterfaceResponse(SendRealtimeMessageInterfaceResponse value) {
        return new JAXBElement<SendRealtimeMessageInterfaceResponse>(_SendRealtimeMessageInterfaceResponse_QNAME, SendRealtimeMessageInterfaceResponse.class, null, value);
    }

}
