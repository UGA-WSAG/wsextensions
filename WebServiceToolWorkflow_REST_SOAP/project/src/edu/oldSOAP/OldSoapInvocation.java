package edu.oldSOAP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.wsdl.Message;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.jdom.Attribute;
import org.jdom.Element;



public class OldSoapInvocation 
{
    private URL url;
    private String operationName;
    SchemaParser parseSchema;
    //OMElement omComplexElement = null;

    /**
     * 
     * @param url
     * @param operationName
     * @throws MalformedURLException
     */
    OldSoapInvocation(URL url, String operationName) throws MalformedURLException
    {
        this.url = url;
        this.operationName = operationName;
        //this.parseSchema = new SchemaParser("http://www.ebi.ac.uk/Tools/services/soap/ncbiblast?wsdl");
        //this.parseSchema = new SchemaParser("file://localhost/Users/Singaram/Downloads/ncbiblast.xml");
        this.parseSchema = new SchemaParser(url.toString());
    }

    /**
     * SOAP message creation
     * @return
     * @throws WSDLException
     */
    public OMElement createPayLoad() throws WSDLException
    {
        WSDLParser parse = new WSDLParser(url);
        ParseWSDL parseWsdl = new ParseWSDL(url.toString());
        OMFactory fac = OMAbstractFactory.getOMFactory();

        //for static payload creation
        //OMNamespace nameSpace = fac.createOMNamespace("http://ws.apache.org/axis2", "ns1");
        //OMElement message = fac.createOMElement("echo", nameSpace);

        //for dynamic payload creation
        OMNamespace nameSpace = fac.createOMNamespace(parse.getTargetNameSpace(), "ns");
        //creates a message object for the element under the input message
        OMElement message = fac.createOMElement(parse.getInputMessageElementName(operationName), nameSpace);

        //create input message object and get the complete list of child elements under the message
        Message messageObject = parse.getInputMessage(operationName);
        List<Element> messageElementList = parseWsdl.getMsgChildElements(messageObject);
        System.out.println("size -" + messageElementList.size());

        //create a hashmap out of the input parameter elements and its values
        HashMap<Element, String> map = new HashMap<Element, String>();

        //traverse through the complete list of elements under the message
        for(int i=0;i<messageElementList.size();i++)
        {
            Element element = messageElementList.get(i); 

            //look up parent element to construct the input structure for SOAP message
            Element parentElement = element.getParentElement();
            while(parentElement.getAttributeValue("name") == null)
            {
                parentElement = parentElement.getParentElement();
            }
            System.out.println("parent element -" + parentElement.getAttributeValue("name"));

            //get the input parameter names
            OMElement value = fac.createOMElement(element.getAttribute("name").getValue(), null);
            //set the input parameter values
            System.out.println("Enter " + element.getAttribute("name").getValue());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String inputValue = null;
            if(!element.getAttributeValue("type").equals("ArrayOfString"))
            {
                try
                {
                    inputValue = br.readLine();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                List<String> inputList = new ArrayList<String>();
                Scanner inputScanner = new Scanner(System.in);
                System.out.println("Enter list elements:");
                do
                {
                    System.out.println("Enter element: ");
                    try
                    {
                        String input = br.readLine();
                        if(!input.equals("exit"))
                        {
                            inputList.add(input);
                            //OMElement listOmelement = fac.createOMElement("string", null);
                            Element listInputElement = new Element("string");
                            listInputElement.setText(input);
                            Attribute attribute = new Attribute("type", "xsd:string");
                            listInputElement.setAttribute(attribute);
                            element.addContent(listInputElement);
                            System.out.println(listInputElement.getParentElement().getAttributeValue("name"));
                            map.put(listInputElement, input);
                        }
                        else
                        {
                            break;
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }while(true);
                System.out.println("Element list :" + element);
                /*
                for(String listElement : inputList)
                {
                    if(inputValue != null)
                    {
                        inputValue.concat(listElement);
                    }
                    else
                    {
                        inputValue = listElement;
                    }
                }
                 */
            }
            //System.out.println(inputValue);
            //value.setText("Hello , my first service utilization");
            //            value.setText(inputValue);
            value.setText(inputValue);
            //            message.addChild(value);
            //fill up the map with input parameters values
            map.put(element, inputValue);
        }

        //print out the elements map
        /*Iterator<Element> elementMapInterator = map.keySet().iterator();
        System.out.println("elementMap Iterator");
        while(elementMapInterator.hasNext())
        {
            Element elementKey = elementMapInterator.next();
            System.out.println(elementKey.getAttributeValue("name") + map.get(elementKey));
        }
         */
        //        OMElement value = fac.createOMElement("parameterId", null);
        //        value.setText("program");
        //        message.addChild(value);

        /**construction of the SOAP message structure from hashmap by adding each element in the map as a child 
        to its parent element in the map and forming the input parameters structure as in wsdl**/
        //        Iterator iterateMap = map.entrySet().iterator();
        //        while(iterateMap.hasNext())
        //        {
        //            Element mapElement = (Element) iterateMap.next();
        //            mapElement.get
        //        }

        //create a hashmap for storing the OMElement for each element
        HashMap<Element, OMElement> omElementMap = new HashMap<Element, OMElement>();

        for(Element key : map.keySet())
        {
            //create OMElement objects for the current element
            OMElement omElement = null;
            if(key.getAttributeValue("name") != null)
            {
                //if an element direclty from wsdl
                omElement  = fac.createOMElement(key.getAttribute("name").getValue(), null);
            }
            else
            {
                //if an element is a part of a list generated from input values
                omElement = fac.createOMElement(key.getName(), null);
            }
            omElement.setText(map.get(key));
            //create a hashmap for storing the OMElement for each element
            omElementMap.put(key, omElement);
            Element parentElement = null;
            //get the parent element of the current element
            if(!key.getParentElement().getName().equals("schema"))
            {
                parentElement = key.getParentElement();
                while(parentElement.getAttributeValue("name") == null)
                {
                    parentElement = parentElement.getParentElement();
                }
                //create OMElement object for the parent element
                OMElement parentOmelement  = fac.createOMElement(parentElement.getAttribute("name").getValue(), null);
                //locate the parent element in the hashmap 

                //                for(Element parentKey : map.keySet())
                //                {
                //                    //add the current element as a child to the parent element if it matches in the hashmap
                //                    if(parentKey == parentElement)
                //                    {
                //                        parentOmelement.addChild(omElement);
                //                    }
                //                }

                for(Element parentKey : omElementMap.keySet())
                {
                    if(parentKey.getAttributeValue("type").equals(parentElement.getAttributeValue("name")))
                    {
                        omElementMap.get(parentKey).addChild(omElement);
                        omElementMap.remove(omElement);
                    }
                }
            }
            else
            {
                //                message.addChild(omElement);
            }
        }

        HashMap<Element, OMElement> finalMap = new HashMap<Element, OMElement>();
        //for(Element key : omElementMap.keySet())
        System.out.println(omElementMap.keySet().size());
        Iterator<Element> iterator = omElementMap.keySet().iterator();
        while(iterator.hasNext())
        {
            Element key = iterator.next();
            Element parentElement = key.getParentElement();
            while(parentElement.getAttributeValue("name") == null)
            {
                parentElement = parentElement.getParentElement();
            }
            if(parentElement.getAttributeValue("name").equals(operationName))
            {
                finalMap.put(key, omElementMap.get(key));
                //message.addChild(omElementMap.get(key));
            }
            //a parent element found
            Iterator<Element> iteratorParent = omElementMap.keySet().iterator();
            while(iteratorParent.hasNext())
            {
                Element parentKey = iteratorParent.next();
                if(parentKey.getAttributeValue("type").equals(parentElement.getAttributeValue("name")))
                {
                    OMElement parentKeyElement = omElementMap.get(parentKey);
                    parentKeyElement.addChild(omElementMap.get(key));

                    finalMap.put(parentKey, parentKeyElement);
                    //omElementMap.remove(key);
                    //iterator.remove();
                    //                        if(finalMap.get(parentKey) == null)
                    //                        {
                    //                            finalMap.put(parentKey, omElementMap.get(parentKey));
                    //                        }
                    //                        else
                    //                        {
                    //                            OMElement parentKeyFinal = finalMap.get(parentKey);
                    //                            parentKeyFinal.addChild(omElementMap.get(key));
                    //                            finalMap.put(parentKey, omElementMap.)
                    //                        }
                }
            }
        }

        System.out.println("final map");
        for(Element key : finalMap.keySet())
        {
            System.out.println(key.getAttributeValue("name") + " " + finalMap.get(key));
        }

        int i=1;
        for(Element key : finalMap.keySet())
        {
            i++;
            message.setLineNumber(i);
            message.addChild(finalMap.get(key));
        }

        //        for(Element key : omElementMap.keySet())
        //        {
        //          message.addChild(omElementMap.get(key));
        //        }

        //print the xml SOAP message to a file
        File file=new File("SOAPMessage.xml");
        FileWriter fileWriter = null;
        try
        {
            fileWriter = new FileWriter(file);
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);   
        printWriter.print(message);
        printWriter.close();
        try
        {
            fileWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }        


        System.out.println("message :" + message);

        return message;
    }


    public OMElement omElementHandler (Element element, String complexElementName, Element schemaElement, Boolean complexType, OMElement message) throws WSDLException
    {
        WSDLParser parse = new WSDLParser(url);
        //ParseWSDL parseWsdl = new ParseWSDL("http://www.ebi.ac.uk/Tools/services/soap/ncbiblast?wsdl");
        //ParseWSDL parseWsdl = new ParseWSDL("file://localhost/Users/Singaram/Downloads/ncbiblast.xml");
        ParseWSDL parseWsdl = new ParseWSDL("http://www.ebi.ac.uk/Tools/services/soap/ncbiblast?wsdl");
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement omElement = fac.createOMElement(element.getAttributeValue("name"), null);
        //if element is an operation, replace omElement object with message for final wrap up
        if(element.getAttributeValue("name").equals(operationName))
        {
            omElement = message;
        }
        //if complex type, create a new complex omElement
        if(complexElementName != null)
        {
            omElement = fac.createOMElement(complexElementName, null);
        }

        List<Element> childrenList = null;
        childrenList = parse.getChildElement(childrenList, element, complexType, schemaElement);
        //        for(Element child : childrenList)
        //        {
        //            System.out.println(child.getName());
        //        }

        //        List<Element> childrenList = parse.getChildElements(element, schemaElement);
        System.out.println(childrenList);


        for(Element child : childrenList)
        {
            //for simple types
            if(child.getAttributeValue("type").contains(":"))
            {
                //for list
                if(child.getAttributeValue("maxOccurs") != null)
                {
                    if(child.getAttributeValue("maxOccurs").equals("unbounded"))
                    {
                        List<String> inputList = new ArrayList<String>();
                        System.out.println("Enter list elements:");
                        do
                        {
                            System.out.println("Enter element: ");
                            try
                            {
                                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                                String input = br.readLine();
                                if(!input.equals("exit"))
                                {
                                    inputList.add(input);
                                    //OMElement listOmelement = fac.createOMElement("string", null);
                                    Element listInputElement = new Element("string");
                                    listInputElement.setText(input);
                                    Attribute attribute = new Attribute("type", "xsd:string");
                                    listInputElement.setAttribute(attribute);
                                    element.addContent(listInputElement);
                                    System.out.println(listInputElement.getParentElement().getAttributeValue("name"));
                                    //OMElement omListElement = fac.createOMElement(listInputElement.getAttributeValue("name"), null);
                                    //omChildElement.addChild(omListElement);
                                    //map.put(listInputElement, input);
                                    OMElement omChildElement = fac.createOMElement(child.getAttributeValue("name"), null);
                                    omChildElement.setText(input);
                                    //                                if(omComplexElement == null)
                                    //                                {
                                    omElement.addChild(omChildElement);
                                    //                                }
                                    //                                else
                                    //                                {
                                    //                                    omComplexElement.addChild(omChildElement);
                                    //                                }
                                }
                                else
                                {
                                    break;
                                }
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }while(true);
                    }

                    else
                    {
                        System.out.println("Enter " + child.getAttribute("name").getValue());
                        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                        String inputValue = null;
                        try
                        {
                            inputValue = br.readLine();
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                        OMElement omChildElement = fac.createOMElement(child.getAttributeValue("name"), null);
                        omChildElement.setText(inputValue);
                        //                    if(omComplexElement == null)
                        //                    {
                        omElement.addChild(omChildElement);
                        //                    }
                        //                    else
                        //                    {
                        //                        omComplexElement.addChild(omChildElement);
                        //                    }

                        //                        if(element.getAttributeValue("name").equals(operationName))
                        //                        {
                        //                            omElement.addChild(omElement);
                        //                        }

                    }
                }
            }
            /*
            else if(child.getAttributeValue("type").equals("ArrayOfString"))
            {
                OMElement omChildElement = fac.createOMElement(child.getAttributeValue("name"), null);
                List<String> inputList = new ArrayList<String>();
                Scanner inputScanner = new Scanner(System.in);
                System.out.println("Enter list elements:");
                do
                {
                    System.out.println("Enter element: ");
                    try
                    {
                        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                        String input = br.readLine();
                        if(!input.equals("exit"))
                        {
                            inputList.add(input);
                            //OMElement listOmelement = fac.createOMElement("string", null);
                            Element listInputElement = new Element("string");
                            listInputElement.setText(input);
                            Attribute attribute = new Attribute("type", "xsd:string");
                            listInputElement.setAttribute(attribute);
                            element.addContent(listInputElement);
                            System.out.println(listInputElement.getParentElement().getAttributeValue("name"));
                            OMElement omListElement = fac.createOMElement(listInputElement.getAttributeValue("name"), null);
                            omChildElement.addChild(omListElement);
                            //map.put(listInputElement, input);
                        }
                        else
                        {
                            break;
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }while(true);

                if(omComplexElement == null)
                {
                    omElement.addChild(omChildElement);
                }
                else
                {
                    omComplexElement.addChild(omChildElement);
                }


//                System.out.println("Enter " + child.getAttribute("name").getValue());
//                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//                String inputValue = null;
//                try
//                {
//                    inputValue = br.readLine();
//                }
//                catch(IOException e)
//                {
//                    e.printStackTrace();
//                }
//                OMElement omChildElement = fac.createOMElement(child.getAttributeValue("name"), null);
//                omChildElement.setText(inputValue);
//                if(omComplexElement == null)
//                {
//                    omElement.addChild(omChildElement);
//                }
//                else
//                {
//                    omComplexElement.addChild(omChildElement);
//                }  

            }
             */
            else
            {
                //for complex types
                //                if(omComplexElement != null)
                //                {
                //                    omElement.addChild(omComplexElement);
                //                }

                complexType = true;
                Element complexElement = parseSchema.getElementElemOfSchema(child.getAttributeValue("type"), schemaElement);
                //                omComplexElement = fac.createOMElement(child.getAttributeValue("name"), null);
                String complexElementNameAttribute = child.getAttributeValue("name");

                OMElement omComplexElement = omElementHandler(complexElement, complexElementNameAttribute, schemaElement, complexType, null);
                omElement.addChild(omComplexElement);
            }

            //            if(omComplexElement != null)
            //            {
            //                omElement.addChild(omComplexElement);
            //            }
            //            omElement.addChild(omElement);
        }
        //        if(omComplexElement != null)
        //        {
        //            omElement.addChild(omComplexElement);
        ////            return omComplexElement;
        //        }
        return omElement;

    }

    public OMElement elementHandler() throws WSDLException
    {
        Boolean complexType = false;
        WSDLParser parse = new WSDLParser(url);
        //ParseWSDL parseWsdl = new ParseWSDL("http://www.ebi.ac.uk/Tools/services/soap/ncbiblast?wsdl");
        //SchemaParser parseSchema = new SchemaParser("http://www.ebi.ac.uk/Tools/services/soap/ncbiblast?wsdl");
        //ParseWSDL parseWsdl = new ParseWSDL("file://localhost/Users/Singaram/Downloads/ncbiblast.xml");
        //SchemaParser parseSchema = new SchemaParser("file://localhost/Users/Singaram/Downloads/ncbiblast.xml");
        ParseWSDL parseWsdl = new ParseWSDL(url.toString());
        SchemaParser parseSchema = new SchemaParser(url.toString());

        OMFactory fac = OMAbstractFactory.getOMFactory();
        //for dynamic payload creation
        OMNamespace nameSpace = fac.createOMNamespace(parse.getTargetNameSpace(), "ns");
        
        //for rpc encoded
        String bindingType = parse.getWsdlType();
        if(bindingType.equals("rpc"))
        {
            
        }
        //creates a message object for the element under the input message
        OMElement message = fac.createOMElement(parse.getInputMessageElementName(operationName), nameSpace);
        Message messageObject = parse.getInputMessage(operationName);

        //retrieve the schema element
        Element root = parseSchema.getRootElement();
        List<Element> schemaList = parseSchema.getSchemaElemList(root);
        Element schemaElement = null;
        if(schemaList.size()==1)
        {
            schemaElement = schemaList.get(0);
        }

        Element rootElement = parse.getPartElementFromInputMessage(messageObject);
        OMElement finalMessage = omElementHandler(rootElement, null, schemaElement, complexType, message);

        //print the xml SOAP message to a file
        File file=new File("SOAPMessage.xml");
        FileWriter fileWriter = null;
        try
        {
            fileWriter = new FileWriter(file);
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);   
        printWriter.print(message);
        printWriter.close();
        try
        {
            fileWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        } 

        //message.addChild(finalMessage);
        System.out.println(finalMessage);
        return finalMessage;
        //return message;

    }

    /**
     * Dynamic invocation of SOAP service
     * @throws WSDLException
     */
    public void dynamicInvoke() throws WSDLException
    {
        //dynamic service client
        ServiceClient serviceClient = null;
        try
        {
            serviceClient = new ServiceClient(null,url,null,null);
        } 
        catch (AxisFault e)
        {
            e.printStackTrace();
        }

        /**for dynamic service client parse wsdl file to get the operation qName and then call sendReceive()**/
        WSDLParser parse = new WSDLParser(url);
        QName qName = parse.getOperationQname(operationName);

        OMElement res = null;
        try
        {
            //res = serviceClient.sendReceive(qName, createPayLoad());
            res = serviceClient.sendReceive(qName, elementHandler());
        } 
        catch (AxisFault e)
        {
            e.printStackTrace();
        }

        System.out.println(res);

    }

    /**
     * static invocation of SOAP service
     * @throws AxisFault
     * @throws WSDLException
     */
    public void staticInvoke() throws AxisFault, WSDLException
    {
        //default service client
        ServiceClient serviceClient = new ServiceClient ();

        /**for static client - fill up options object and call sendReceive()**/
        // create option object
        Options opts = new Options();
        //setting target EPR
        opts.setTo(new EndpointReference("http://127.0.0.1:8080/axis2/services/MyService.MyServiceHttpSoap11Endpoint/"));
        //Setting action ,and which can be found from the wsdl of the service
        opts.setAction("urn:echo");
        //setting created option into service client
        serviceClient.setOptions(opts);

        OMElement res = serviceClient.sendReceive(createPayLoad());
    }

    public static void main(String [] args) throws Exception 
    {
        OldSoapInvocation invoke = new OldSoapInvocation(new URL("http://www.ebi.ac.uk/Tools/services/soap/ncbiblast?wsdl"), "run");
        //SoapInvocation invoke = new SoapInvocation(new URL("http://www.ebi.ac.uk/ws/services/WSDbfetch?wsdl"), "fetchBatch");
        //SoapInvocation invoke = new SoapInvocation(new URL("file://localhost/Users/Singaram/Downloads/ncbiblast.xml"), "run");
        invoke.dynamicInvoke();
        //invoke.staticInvoke();

        //url = new URL("http://www.ebi.ac.uk/Tools/services/soap/fastm?wsdl");
        // url = new URL("http://localhost:8080/axis2/services/MyService?wsdl");

        //operationName = "getParameterDetails";
        //operationName = "echo";

    }

}