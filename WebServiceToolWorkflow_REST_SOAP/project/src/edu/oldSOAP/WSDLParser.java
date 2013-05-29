package edu.oldSOAP;


import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;

import org.jdom.Element;

import com.ibm.wsdl.BindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.xml.WSDLReaderImpl;



/**
 * This class has methods to parse various WSDL and XML elements
 * @author Singaram Akshay Choche
 *
 */
public class WSDLParser
{
    private static Definition wsdl;
    private URL url;
    SchemaParser schemaParser;
    List<Element> newChildrenList = null;
    ParseWSDL parseWsdl;

    /**
     * Constructor for the WSDL parser that takes in the url of the wsdl to be parsed
     * @param url
     * @throws WSDLException
     */
    public WSDLParser(URL url) throws WSDLException
    {
        WSDLReaderImpl reader = new WSDLReaderImpl();
        schemaParser = new SchemaParser(url.toString());
        wsdl = reader.readWSDL(url.toString());
        parseWsdl = new ParseWSDL(url.toString());
    }

    /**
     * gets the qName for any given entity from the wsdl
     * @param args
     * @return 
     * @throws WSDLException 
     * @throws WSDLException 
     */
    public static void getQname(String operationName) throws WSDLException
    {
        for(Object message : wsdl.getMessages().keySet())
        {
            Message msg = wsdl.getMessage((QName) message);
            //System.out.println("#" + msg);
            for(Object msgs : msg.getParts().keySet())
            {
                Part part = msg.getPart((String)msgs);
                //System.out.println("*" + part.getName());   
            }
        }

        for( Object portType : wsdl.getPortTypes().keySet())
        {
            PortType port = wsdl.getPortType((QName) portType);
            //System.out.println(port);
            System.out.println("port qName" + port.getQName());
            for(Object operation : port.getOperations())
            {
                System.out.println(((Operation) operation).getName());
                if(((Operation) operation).getName().equals(operationName))
                {
                    QName qName = new QName(((Operation) operation).getName());
                }
                //System.out.println(((Operation) operation).getInput());
            }
        }
        String nameSpace = wsdl.getTargetNamespace();
        //System.out.println(nameSpace);
    }

    /**
     * gets the qName for the given operation from the wsdl
     * @param operationName
     * @return
     */
    public static QName getOperationQname(String operationName)
    {
        for( Object portType : wsdl.getPortTypes().keySet())
        {
            PortType port = wsdl.getPortType((QName) portType);
            //System.out.println(port);
            for(Object operation : port.getOperations())
            {
                System.out.println(((Operation) operation).getName());
                if(((Operation) operation).getName().equals(operationName))
                {
                    QName qName = new QName(wsdl.getTargetNamespace(),((Operation) operation).getName());
                    System.out.println("operation qname" + qName);
                    return qName;
                }
                //System.out.println(((Operation) operation).getInput());
            }
        }
        return null;    
    }

    public static String getTargetNameSpace()
    {
        return wsdl.getTargetNamespace();
    }

    /**
     * gets the input message element name for a given operation from the wsdl
     * @param operationName
     * @return
     */
    public static String getInputMessageElementName(String operationName){
        for( Object portType : wsdl.getPortTypes().keySet()){
            PortType port = wsdl.getPortType((QName) portType);
            for(Object operation : port.getOperations()){
                if(((Operation) operation).getName().equals(operationName)){
                    Operation opr = (Operation) operation;
                    Input input = opr.getInput();
                    Message message = input.getMessage();
                    //System.out.println("input message-" + message.getQName());
                    Map inputElements = message.getParts();
                    Iterator iterator = inputElements.keySet().iterator();
                    while(iterator.hasNext()){
                        Part part = (Part) inputElements.get(iterator.next());
                        //System.out.println("element name - " + part.getElementName().getLocalPart());
                        return part.getElementName().getLocalPart();
                    }

                }   
            }
        }
        return null;
    }

    /**
     * gets the input message element name for a given operation from a rpc-encoded wsdl
     * @param operationName
     * @return
     */
    public static String getInputMessageElementName_RPC(String operationName)
    {
        for( Object portType : wsdl.getPortTypes().keySet())
        {
            PortType port = wsdl.getPortType((QName) portType);
            for(Object operation : port.getOperations())
            {
                System.out.println(((Operation) operation).getName());
                if(((Operation) operation).getName().equals(operationName))
                {
                    Operation opr = (Operation) operation;
                    Input input = opr.getInput();
                    Message message = input.getMessage();

                    System.out.println("input message-" + message.getQName());

                    return message.getQName().getLocalPart();
                }   
            }
        }
        return null;
    }

    /**
     * gets the wsdl type of a given wsdl url
     * @return - type of the wsdl
     */
    public static String getWsdlType()
    {
        Map bindingMap = wsdl.getAllBindings();
        for(Object binding : bindingMap.keySet())
        {
            Binding bind = wsdl.getBinding((QName) binding);
            //Binding bind = (Binding) binding;
            List<ExtensibilityElement> extElements = bind.getExtensibilityElements();
            for(ExtensibilityElement element : extElements)
            {
                System.out.println(element.getElementType());
                SOAPBindingImpl bindImpl = (SOAPBindingImpl) element;
                return bindImpl.getStyle();
            }
            //SOAPBindingImpl bindImpl = (SOAPBindingImpl) bind;
        }
        return null;
    }

    /**
     * gets the input message information for a given operation
     * @param operationName
     * @return Message
     */
    public Message getInputMessage(String operationName){
        for( Object portType : wsdl.getPortTypes().keySet()){
            PortType port = wsdl.getPortType((QName) portType);
            //System.out.println(port);
            for(Object operation : port.getOperations()){
                //System.out.println(((Operation) operation).getName());
                if(((Operation) operation).getName().equals(operationName)){
                    Operation opr = (Operation) operation;
                    Input input = opr.getInput();
                    Message message = input.getMessage();
                    //System.out.println("input message-" + message.getQName());
                    return message;
                }
            }
        }
        return null;
    }

    /**
     * gets the ouput messafe information for a given operation
     * @param operationName
     * @return Mesage
     */
    public Message getOutputMessage(String operationName)
    {
        for( Object portType : wsdl.getPortTypes().keySet())
        {
            PortType port = wsdl.getPortType((QName) portType);
            //System.out.println(port);
            for(Object operation : port.getOperations())
            {
                System.out.println(((Operation) operation).getName());
                if(((Operation) operation).getName().equals(operationName))
                {
                    Operation opr = (Operation) operation;
                    Output output = opr.getOutput();
                    Message message = output.getMessage();
                    //System.out.println("input message-" + message.getQName());
                    return message;
                }
            }
        }
        return null;
    }

    /**
     * gets the fault messages for a given operation
     * @param operationName
     * @return List<Message>
     */
    public List<Message> getFaultMessages(String operationName)
    {
        List<Message> messageList = new ArrayList<Message>();
        for( Object portType : wsdl.getPortTypes().keySet())
        {
            PortType port = wsdl.getPortType((QName) portType);
            //System.out.println(port);
            for(Object operation : port.getOperations())
            {
                System.out.println(((Operation) operation).getName());
                if(((Operation) operation).getName().equals(operationName))
                {
                    Operation opr = (Operation) operation;
                    Map faultMap = opr.getFaults();
                    for(Object fault : faultMap.entrySet())
                    {
                        Message faultMessage = (Message) fault;
                        messageList.add(faultMessage);
                    }
                    return messageList;
                }
            }
        }
        return null;
    }

    /**
     * gets the part element for a given message 
     * @param Message
     * @return Element
     */
    public Element getPartElementFromInputMessage(Message msg){
        Element rootElement = schemaParser.getRootElement();
        List<Element> schemaList = schemaParser.getSchemaElemList(rootElement);
        Element schemaElement = null;
        if(schemaList.size()==1)
            schemaElement = schemaList.get(0);
        Element partElement = null;
        Map parts = msg.getParts();
        Iterator it = parts.keySet().iterator();
        List<Element> leafNodes = new ArrayList<Element>();
        while(it.hasNext()){
            Part p = (Part)parts.get(it.next());
            partElement = getPartTypeEle(p,schemaElement);
        }
        return partElement;
    }

    /**
     * gets the child elements of the given message
     * @param Message
     * @return List<Element>
     */
    public List<Element> getMsgChildElements(Message msg){

        Element rootElement = schemaParser.getRootElement();
        List<Element> schemaList = schemaParser.getSchemaElemList(rootElement);
        Element schemaElement = null;
        //        List<String> eleNames = new ArrayList<String>();
        if(schemaList.size()==1)
            schemaElement = schemaList.get(0);
        //System.out.println("The Schema element is : "+schemaElement);

        //List<Element> leafNode = new ArrayList<Element>();

        Element partElement;
        Map parts = msg.getParts();
        Iterator it = parts.keySet().iterator();
        List<Element> leafNodes = new ArrayList<Element>();
        //System.out.println("Are there any elements in the Map : "+it.hasNext());
        while(it.hasNext()){

            Part p = (Part)parts.get(it.next());
            partElement = getPartTypeEle(p,schemaElement);

            if(partElement == null){
                leafNodes.add((Element)p);
            }
            else
            {
                List<Element> lst = getChildElements(partElement,schemaElement);

                for(Element e : lst){
                    leafNodes.add(e);
                }
            }
        }
        return leafNodes;
    }

    /**
     * gets the partTypeElements for a given part and schema 
     * @param partElement
     * @param schemaElement
     * @return Element
     */
    public Element getPartTypeEle(Part part,Element schemaElement){


        Element partElement = null;
        QName partType = part.getTypeName();
        QName partEleName = part.getElementName();
        if(partType != null)
        {
            //System.out.println("The partType is : "+partType.getNamespaceURI());
            if(!partType.getNamespaceURI().equalsIgnoreCase("http://www.w3.org/2001/XMLSchema"))
                partElement = schemaParser.getElementElemOfSchema(partType.getLocalPart(), schemaElement);
            //return false;
            else
                partElement = null;//return true;
        }
        else if(partEleName != null){

            //System.out.println("The partElement is : "+partEleName);
            if(!partEleName.getNamespaceURI().equalsIgnoreCase("http://www.w3.org/2001/XMLSchema"))
                partElement = schemaParser.getElementElemOfSchema(partEleName.getLocalPart(), schemaElement);
            //return false;
            else
                partElement = null;
        }
        return partElement;
    }

    /**
     * gets the child elements for any given element and its schema element
     * @param element
     * @param schemaElement
     * @return List<Element>
     */
    public List<Element> getChildElements(Element element,Element schemaElement){

        List<Element> leafNodes = new ArrayList();

        List<Element> lst = schemaParser.getElementOfSchema(element, schemaElement);
        for(Element ele : lst) {
            String type = ele.getAttributeValue("type");
            if(type!=null)
            {
                leafNodes.add(ele);
            }
            else
            {
                getChildElements(ele,schemaElement);
            }
        }
        return leafNodes;
    }

    /**
     * gets the list of child elements for a given complex element and its schema definition
     * with a given list of elements to be populated
     * @param childrenList
     * @param element
     * @param complexType
     * @param schemaElement
     * @return List<Element>
     */
    public List<Element> getChildElement(List<Element> childrenList, Element element, Boolean complexType, Element schemaElement){
    	childrenList = element.getChildren();
        for(Element child : childrenList){
            if(!child.getName().equals("element")){
                getChildElement(childrenList, child, complexType, schemaElement);
            }else{
                newChildrenList = childrenList;
            }
            //System.out.println(child.getName());
        }
        return newChildrenList;
    }

    /**
     * This method returns the child elements for a given complex type element
     * @param element
     * @param schemaElement
     * @return
     */
    public List<Element> getChildElementsForComplexType(Element element,Element schemaElement){

        List<Element> leafNodes = new ArrayList();

        List<Element> lst = schemaParser.getElementOfSchema(element, schemaElement);
        for(Element ele : lst) {
            String type = ele.getAttributeValue("type");
            if(type!=null){
                if( type.contains(":")){
                    String prefix = type.split(":")[0];
                    String value = type.split(":", 2)[1];
                    if(prefix.equalsIgnoreCase("xsd") || prefix.equalsIgnoreCase("xs"))
                    {
                        leafNodes.add(ele);
                        //System.out.println("Ele : "+ele.getAttributeValue("name"));
                    }else
                    {
                        //System.out.println("Inisde");
                        Element el = schemaParser.getElementElemOfSchema(value,schemaElement);
                        List<Element> eleList = getChildElements(el,schemaElement);
                        leafNodes.add(ele);
                        leafNodes.addAll(eleList);
                    }
                }
                else
                {
                    if(!ele.getAttributeValue("type").equalsIgnoreCase("ArrayOfString"))
                    {
                        //System.out.println("Inisde");
                        Element elem = schemaParser.getElementElemOfSchema(ele.getAttributeValue("type"), schemaElement);
                        //System.out.println("*******  : "+ele.getAttributeValue("name"));
                        List<Element> eleList;
                        //{
                        eleList = getChildElements(elem,schemaElement);
                        //System.out.println("Ele list : "+eleList);
                        leafNodes.add(ele);
                        leafNodes.addAll(eleList);
                    }
                    else{
                        leafNodes.add(ele);
                    }

                }
            }
        }
        return leafNodes;
    }


}
