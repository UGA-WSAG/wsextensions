package edu.oldSOAP;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;
import javax.wsdl.*;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Namespace;



/**
 * This class has methods to parse various WSDL and XML elements
 * @author Singaram
 *
 */
public class ParseWSDL {

    private static Definition def;

    private static String fileName;

    private static SchemaParser schemaParser;

    private static List<Element> schemaElementList;

    public SchemaParser getSchemaParser(){
        return schemaParser;
    }

    /**
     * return definition part of the wsdl file
     * @return Definition
     * @throws Exception
     */
    public Definition getWsdlDef(){
        return def;
    }

    /**
     * given operation name, and definition part of wsdl file
     * get Operation
     * @param def
     * @param opName
     * @return Operation
     */
    public Operation getDefOp(Definition def, String opName){
        Map portTypes=def.getPortTypes();
        //one portType in one wsdl
        PortType pt=(PortType)portTypes.get(portTypes.keySet().iterator().next());
        Operation op=pt.getOperation(opName,null,null);
        return op;
    }

    /**
     * get all operation names of given wsdl file
     * @return List<String>
     */
    public List<String> getAllOpName(){   //Works
        List<String> opNameList=new ArrayList<String>();

        Definition df=null;
        try {
            df = this.getWsdlDef();//fileName);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Map portTypes=df.getPortTypes();
        //one portType in one wsdl
        PortType pt=(PortType)portTypes.get(portTypes.keySet().iterator().next());
        List<Operation> opList=pt.getOperations();
        //System.out.println(opList);
        for(int i=0;i<opList.size();i++){
            //System.out.println(opList.get(i).getName());
            opNameList.add(opList.get(i).getName());			
        }		
        return opNameList;
    }


    /**
     * get all operations of given wsdl file
     * @return List<Operation>
     */
    public List<Operation> getAllOp(){   
    	//Works
        //List<String> opNameList=new ArrayList<String>();
        List<Operation> opList = new ArrayList<Operation>();
        Map portTypes=def.getPortTypes();
        //one portType in one wsdl

        Iterator it = portTypes.keySet().iterator();
        while(it.hasNext()){
            PortType pt=(PortType)portTypes.get(it.next());
            opList.addAll(pt.getOperations());
        }
        //		PortType pt=(PortType)portTypes.get(portTypes.keySet().iterator().next());
        //		List<Operation> opList=pt.getOperations();
        //System.out.println(opList);
        return opList;
    }

    /**
     * Given message , get the list of element name of all parts of the message
     * @param Message
     * @return List<String>
     */
    private List<String> getMsElemName(Message ms){
        Map partsMap=ms.getParts();
        if(partsMap==null){
            return null;
        }
        Iterator it=partsMap.keySet().iterator();
        List<String> msEleNameList=new ArrayList<String>();
        while(it.hasNext()){
            Part pa=(Part)partsMap.get(it.next());
            String temp=pa.getElementName().getLocalPart();
            msEleNameList.add(temp);
        }
        //System.out.println(temp);
        return msEleNameList;
    }

    /**
     * filename-->message-->part--->element namespace uri
     * @return
     */
    public String getMsEleNSUri(){

        Definition df=null;
        try {
            df = this.getWsdlDef();
        } catch (Exception e) {

            e.printStackTrace();
        }
        Map messageMap=df.getMessages();
        Message tempMs=(Message)messageMap.get(messageMap.keySet().iterator().next());
        Map partsMap=tempMs.getParts();
        if(partsMap==null){
            return null;
        }
        Iterator it=partsMap.keySet().iterator();
        Part pa=(Part)partsMap.get(it.next());
        String NSuri=pa.getElementName().getNamespaceURI();
        return NSuri;
    }


    /**
     * get name list of all parts of given message
     * @param ms
     * @return
     */
    private List<String> getMsPartNameList(Message ms){
        Map partsMap=ms.getParts();
        Iterator it=partsMap.keySet().iterator();
        List<String> msPartNameList=new ArrayList<String>();
        int i=0;
        while(it.hasNext()){
            Part pa=(Part)partsMap.get(it.next());
            String temp=pa.getName();
            //QName temp1=pa.getElementName();
            //System.out.println("The part is  : "+temp1);
            msPartNameList.add(temp);//temp1.getLocalPart());//(temp);
        }
        return msPartNameList;
    }


    /**
     * gets the output message for an operation
     * @param Operation
     * @return Message
     */
    public Message getOpOutMessage(Operation op){

        Message ms=op.getOutput().getMessage();
        //String temp=ms.toString();//many portTypes??
        //System.out.println(temp);
        return ms;

    }	

    public Message getOpInMessage(Operation op){

        Message ms=op.getInput().getMessage();
        //String temp=ms.toString();//many portTypes??
        //System.out.println(temp);
        return ms;
    }

    /**
     * given operation name, get a list of the element name of all parts of 
     * message of input of the operation
     * 
     * @param opName
     * @return List<String>
     * @throws Exception
     */
    public List<String> getOpInMsElemName(String opName)throws Exception{

        Definition df=this.getWsdlDef();
        return getMsElemName(this.getDefOp(df,opName).getInput().getMessage());
    }

    /**
     * get the name list of all parts of input of given operation
     * @param fileName
     * @param opName
     * @return List<String>
     * @throws Exception
     */
    public List<String> getOpInMsPartNameList(String opName)throws Exception{

        Definition df=this.getWsdlDef();
        return getMsPartNameList(this.getDefOp(df,opName).getInput().getMessage());
    }

    /**
     * get the name list of all parts of output of given operation
     * @param opName
     * @return List<String>
     * @throws Exception
     */
    public List<String> getOpOutMsPartNameList(String opName)throws Exception{

        Definition df=this.getWsdlDef();
        return getMsPartNameList(this.getDefOp(df,opName).getOutput().getMessage());
    }

    /**
     * given operation name, get a list of the element name of all parts of 
     * message of output of the operation
     * @param opName
     * @return List<String>
     * @throws Exception
     */
    public List<String> getOpOutMsElemName(String opName)throws Exception{
        //String fileUrl=ClassLoader.getSystemResource(fileName).toString();
        Definition df=this.getWsdlDef();
        return getMsElemName(this.getDefOp(df,opName).getOutput().getMessage());
    }

    /**
     * create a new wsdl file with given file name & definition
     * @param outputFileName
     * @param Definition
     * @throws WSDLException
     * @throws IOException
     * @throws URISyntaxException 
     */
    private void createWsdl(String outputFileName, Definition def) throws WSDLException, IOException, URISyntaxException{
        String fileUrl=ClassLoader.getSystemResource(outputFileName).toString();
        WSDLFactory factory= WSDLFactory.newInstance();
        final WSDLWriter writer = factory.newWSDLWriter();
        final Writer sink = new FileWriter(new File(new URI(fileUrl)));
        writer.writeWSDL(def, sink);
    }


    /**
     * update the target wsdl input part element name as fileA input part element name
     * and ouput part element as fileB output part element name
     * @param fileNameA
     * @param opA
     * @param fileNameB
     * @param opB
     * @param outFileName
     * @param opOutFile
     */
    public void updatePartElement(String fileNameA, String opA, String fileNameB, String opB, String outFileName, String opOutFile){

        //String fileAUrl=ClassLoader.getSystemResource(fileNameA).toString();
        //String fileBUrl=ClassLoader.getSystemResource(fileNameB).toString();
        //String outfileUrl=ClassLoader.getSystemResource(outFileName).toString();

        //get input part of wsdlA, output part of wsdlB
        List<String> inPartsEleNameListA=null;
        List<String> outPartsEleNameListB=null;
        try{
            inPartsEleNameListA=getOpInMsElemName(opA);	
            outPartsEleNameListB=getOpOutMsElemName(opB);	
        }catch(Exception e){
            e.printStackTrace();
        }	
        //assuming only one part for each message
        String inPartEleNameA=inPartsEleNameListA.get(0);
        String outPartEleNameB=outPartsEleNameListB.get(0);

        //update part element of target wsdl file
        Definition df=null;
        try {
            df = this.getWsdlDef();
        } catch (Exception e) {

            e.printStackTrace();
        }
        //get output and input part of target wsdl
        Map tempInMap=this.getDefOp(df,opOutFile).getInput().getMessage().getParts();
        Map tempOutMap=this.getDefOp(df,opOutFile).getOutput().getMessage().getParts();
        Part inPart=(Part)tempInMap.get(tempInMap.keySet().iterator().next());
        Part outPart=(Part)tempOutMap.get(tempOutMap.keySet().iterator().next());
        QName inEleQname=inPart.getElementName();
        QName outEleQname=outPart.getElementName();
        //update element name of parts
        inPart.setElementName(new QName(inEleQname.getNamespaceURI(),inPartEleNameA,inEleQname.getPrefix()));
        outPart.setElementName(new QName(outEleQname.getNamespaceURI(),outPartEleNameB,outEleQname.getPrefix()));
        //write back to target wsdl file
        try {
            this.createWsdl(outFileName, df);
        } catch (WSDLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (URISyntaxException e) {

            e.printStackTrace();
        }
    }

    /**
     * get input part of given wsdl & operation
     * @param opName
     * @return Map
     */
    private Map getInPart(String opName){
        //String fileUrl=ClassLoader.getSystemResource(fileName).toString();	
        Definition df=null;
        try {
            df = this.getWsdlDef();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return this.getDefOp(df,opName).getInput().getMessage().getParts();
    }

    /**
     * get output part of given wsdl & operation
     * @param opName
     * @return Map
     */
    private Map getOutPart(String opName){
        //String fileUrl=ClassLoader.getSystemResource(fileName).toString();	
        Definition df=null;
        try {
            df = this.getWsdlDef();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return this.getDefOp(df,opName).getOutput().getMessage().getParts();
    }


    /*
     * @author Chaitanya
     *
     *
     *
     * have to handle multiple schemas
     * */
    public List<String> getChildFromMsg(Message msg){

        Element rootElement = schemaParser.getRootElement();
        List<Element> schemaList = schemaParser.getSchemaElemList(rootElement);
        Element schemaElement = null;
        List<String> eleNames = new ArrayList<String>();
        if(schemaList.size()==1)
            schemaElement = schemaList.get(0);
        //System.out.println("The Schema element is : "+schemaElement);

        List<Element> leafNode = new ArrayList<Element>();

        Element partElement;
        Map parts = msg.getParts();
        Iterator it = parts.keySet().iterator();
        List<String> leafNodes = new ArrayList<String>();
        //System.out.println("Are there any elements in the Map : "+it.hasNext());
        while(it.hasNext()){

            Part p = (Part)parts.get(it.next());
            partElement = getPartTypeEle(p,schemaElement);

            if(partElement == null){
                leafNodes.add(p.getName());
            }
            else
            {
                List<String> lst = getChildEle(partElement,schemaElement);

                for(String s : lst){
                    leafNodes.add(s);
                }
            }
        }
        return leafNodes;
    }


    /*
     * @author Chaitanya
     *
     *
     *
     *
     * */

    public List<Element> getMsgChildElements(Message msg){

        Element rootElement = schemaParser.getRootElement();
        List<Element> schemaList = schemaParser.getSchemaElemList(rootElement);
        Element schemaElement = null;
        //            List<String> eleNames = new ArrayList<String>();
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


    /*
     * @author Chaitanya
     *
     *
     *
     *
     * */

    public List<Element> getPartChildElements(Part part){

        Element rootElement = schemaParser.getRootElement();
        List<Element> schemaList = schemaParser.getSchemaElemList(rootElement);
        Element schemaElement = null;
        //            List<String> eleNames = new ArrayList<String>();
        if(schemaList.size()==1)
            schemaElement = schemaList.get(0);
        //System.out.println("The Schema element is : "+schemaElement);

        //List<Element> leafNode = new ArrayList<Element>();

        Element partElement;
        //Map parts = msg.getParts();
        //Iterator it = parts.keySet().iterator();
        List<Element> leafNodes = new ArrayList<Element>();
        //System.out.println("Are there any elements in the Map : "+it.hasNext());
        //while(it.hasNext()){

        //Part p = (Part)parts.get(it.next());
        partElement = getPartTypeEle(part,schemaElement);

        //if(partElement == null){
        //    leafNodes.add(p);
        //}
        //else
        //{
        List<Element> lst = getChildElements(partElement,schemaElement);

        for(Element e : lst){
            leafNodes.add(e);
        }
        //}
    //}
        return leafNodes;
    }



    /* @author Chaitanya
     * Given an element and schema element gives the list of names of the leaf nodes for the element
     * @param element
     * @param schemaElement
     * @return
     *
     * */
    public List<String> getChildEle(Element element,Element schemaElement){

        List<String> leafNodes = new ArrayList();

        List<Element> lst = schemaParser.getElementOfSchema(element, schemaElement);
        for(Element ele : lst) {
            String type = ele.getAttributeValue("type");
            if(type.contains(":")){
                String prefix = type.split(":")[0];
                if(prefix.equalsIgnoreCase("xsd") || prefix.equalsIgnoreCase("xs"))
                {
                    leafNodes.add(ele.getAttributeValue("name"));
                    //System.out.println("Ele : "+ele.getAttributeValue("name"));
                }else
                {
                    //System.out.println("Inisde");
                    List<String> eleList = getChildEle(ele,schemaElement);
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
                    List<String> eleList;
                    //{
                    eleList = getChildEle(elem,schemaElement);
                    //System.out.println("Ele list : "+eleList);
                    leafNodes.addAll(eleList);
                }
                else{
                    leafNodes.add(ele.getAttributeValue("name"));
                }

            }
        }

        return leafNodes;
    }


    /* @author Chaitanya
     * Given an element in the schema element gives the list of leaf node elements for the element
     * @param element
     * @param schemaElement
     * @return
     *
     * */
    public List<Element> getChildElements(Element element,Element schemaElement){

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
            else
            {
                getChildElements(ele,schemaElement);
            }
        }
        return leafNodes;
    }



    /*
     * @author Chaitanya
     * Gien the part returns element if it is of type element esle if part is of xsd type else returns null
     * @param part
     * @return
     * */

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
     * get the service info for given wsdl
     * @return service info
     */
    public Map getService()
    {
        Definition def = null;
        try {
            def = getWsdlDef();
        } catch (Exception ex) {
            Logger.getLogger(ParseWSDL.class.getName()).log(Level.SEVERE, null, ex);
        }
        Map services = def.getAllServices();
        return services;
    }

    /**
     * Constructor for the setting the required parameters to parse the given wsdl file
     * @param fileName
     */
    public ParseWSDL(String fileName) 
    {
        ParseWSDL.fileName = fileName;
        schemaParser = new SchemaParser(fileName);
        schemaElementList = schemaParser.getSchemaElemList();
        //            jdomWSDLManager = new JDOMWSDLManager(schemaParser.getRootElement().getDocument());
        WSDLFactory factory;
        try 
        {
            factory = WSDLFactory.newInstance();

            WSDLReader reader=factory.newWSDLReader();
            def=reader.readWSDL(fileName); 
        } 
        catch (WSDLException ex)
        {
            Logger.getLogger(ParseWSDL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Constructor to parse the wsdl file with given definition and document
     * @param def
     * @param doc
     */
    public ParseWSDL(Definition def,Document doc){
        schemaParser = new SchemaParser(doc);
        //            jdomWSDLManager = new JDOMWSDLManager(doc);
        this.def = def;
    }


    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{

        ParseWSDL test = new ParseWSDL("http://cs.uga.edu/~guttula/wublast.wsdl");
        Operation op = test.getDefOp(def, "run");
        List s = op.getExtensibilityElements();
        for(int i=0;i<s.size();i++)
        {
            UnknownExtensibilityElement e = (UnknownExtensibilityElement)s.get(i);
            //Element ele = 
            System.out.println("The op annot type is : "+e.getElement().getAttribute("modelReference"));
        }
        Message inMsg = test.getOpInMessage(op);
        Map extAttrs = inMsg.getExtensionAttributes();
        //Map ext = 
        Iterator it = extAttrs.keySet().iterator();
        System.out.println("The attr is : "+extAttrs);
        while(it.hasNext()){
            System.out.println("attr : "+extAttrs.get(it.next()));
        }
        Message outMsg = test.getOpOutMessage(op);
        Map extAttr = outMsg.getExtensionAttributes();
        List t = outMsg.getNativeAttributeNames();
        System.out.println("The out msg attr are : "+t);
        QName msgname = inMsg.getQName();
        //String partEleName =
        List<String> lst = test.getMsPartNameList(inMsg);
        List<Element> inChild = test.getMsgChildElements(inMsg);

        List<String> children = test.getChildFromMsg(outMsg);

        System.out.println("The leaf nodes are : "+children);
        System.out.println("The input parts are : "+inChild);
        for(Element e : inChild){
            List lt = e.getAttributes();
            System.out.println("Child : "+e.getAttributeValue("name"));
            Attribute at = e.getAttribute("modelReference", Namespace.getNamespace("http://www.w3.org/ns/sawsdl"));//e.getAttribute("sawsdl:modelReference");
            if(at != null){
                //System.out.println("The model ref is : "+at);
            }
        }
        //System.out.println("The schema element is : "+test.schemaParser.getSchemaElemList(test.schemaParser.getRootElem()));
        System.out.println(test.getAllOpName());
    }

}
