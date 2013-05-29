package edu.oldSOAP;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * @author Rui Wang
 *
 */
public class SchemaParser {

    private Element rootElement;
    private static String fileName;
    public static Namespace xsdNS = Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema");
    private static Namespace wsdlNS  = Namespace.getNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
//	private static Namespace sawsdlNS  = Namespace.getNamespace("sawsdl", "http://www.w3.org/ns/sawsdl");

	
	/**given element fullname and a schema element, return the element of <element>
	 * @param elementFullname
	 * @param schemaElem
	 * @return
	 */
	public Element getElementElemOfSchema(String elementFullname, Element schemaElem){
		Element elementElem = null;
		String elementPrefix = "";
		String elementName = elementFullname;

		if (elementFullname.contains(":")){
			elementPrefix = elementFullname.split(":")[0];
			elementName = elementFullname.split(":")[1];
		}
		
		Element rootElem = schemaElem.getDocument().getRootElement();
		//in case schema prefix is s, xs or xsd
		if (rootElem.getNamespace("xsd") != null) {
			xsdNS = rootElem.getNamespace("xsd");
		} else if (rootElem.getNamespace("xs") != null) {
			xsdNS = rootElem.getNamespace("xs");
		 
	} else if (rootElem.getNamespace("s") != null) {
		xsdNS = rootElem.getNamespace("s");
	}else
			System.out.println("Warning: the prefix of this schema is not xsd or xs or s, parser may fail!");
		
		String xsdPrefix = xsdNS.getPrefix();
                //System.out.println("The XSD is : "+xsdPrefix);
		if(elementPrefix.equals("") || rootElem.getNamespace(elementPrefix).getURI().equals(schemaElem.getAttributeValue("targetNamespace")))
		{try {
                        Element tempElem = (Element)( XPath
					.selectSingleNode(rootElem, "//"
							+ xsdPrefix
							+ ":schema/descendant::"
							+ xsdPrefix
							+ ":*[@name=\""
							+ elementName
							+ "\"]"));
			elementElem = tempElem;
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		}
		
		return elementElem;
	}



        /**
         * @author Chaitanya
         * given an "element" and a schema element, return the child elements of "element"
	 * @param elementFullname
	 * @param schemaElem
	 * @return
	 */
	public List<Element> getElementOfSchema(Element element, Element schemaElem){

                List<Element> elementList = new ArrayList<Element>();
                Element elementElem = null;
		String elementPrefix = "";
		String elementName = element.getAttributeValue("name");

		if (element.getAttributeValue("name").contains(":")){
			elementPrefix = element.getAttributeValue("name").split(":")[0];
			elementName = element.getAttributeValue("name").split(":")[1];
		}

		Element rootElem = schemaElem.getDocument().getRootElement();
		//in case schema prefix is s, xs or xsd
		if (rootElem.getNamespace("xsd") != null) {
			xsdNS = rootElem.getNamespace("xsd");
		} else if (rootElem.getNamespace("xs") != null) {
			xsdNS = rootElem.getNamespace("xs");

	} else if (rootElem.getNamespace("s") != null) {
		xsdNS = rootElem.getNamespace("s");
	}else
			System.out.println("Warning: the prefix of this schema is not xsd or xs or s, parser may fail!");

		String xsdPrefix = xsdNS.getPrefix();
                //System.out.println("The XSD is : "+xsdPrefix);
		if(elementPrefix.equals("") || rootElem.getNamespace(elementPrefix).getURI().equals(schemaElem.getAttributeValue("targetNamespace")))
		{try {
                        List lst = (XPath.selectNodes(schemaElem, "//"
							+ xsdPrefix
							+ ":*[@name=\""
                                                        +elementName
                                                        +"\"]/descendant::"
							+ xsdPrefix
							+ ":element[@name]"));
                        /*Element tempElem = (Element)( XPath
					.selectSingleNode(schemaElem, "//"
							+ xsdPrefix
							+ ":schema/descendant::"
							+ xsdPrefix
							+ ":*[@name=\""
							+ elementName
							+ "\"]"));*/
                        for(int i =0;i<lst.size();i++){
                            Element e = (Element)lst.get(i);
                            elementList.add(e);
                            //System.out.println("The element is : "+e.getAttributeValue("name"));
                        }
			//elementElem = tempElem;
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		}

		return elementList;
	}




	/**given <element> full name and root element of the wsdl, return the element of <element>
	 * @param elementFullname
	 * @param rootElem
	 * @return
	 */
	public Element getElementElemOfRoot(String elementFullname, Element rootElem){
		List<Element> schemaList = this.getSchemaElemList(rootElem);
		String elementPrefix = "";
		String elementName = elementFullname;
				Element elementElem = null;

		if (elementFullname.contains(":")){
			elementPrefix = elementFullname.split(":")[0];
			elementName = elementFullname.split(":")[1];
		}
		
		for (Element schema : schemaList) {
			if(rootElem.getNamespace(elementPrefix).getURI().equals(schema.getAttributeValue("targetNamespace"))){
				elementElem = this.getElementElemOfSchema(elementFullname, schema);
				break;
			}

		}
		
		
		return elementElem;
	}
	
	/**given the root element of a wsdl and complexType(prefix and name), return the complextype element in the wsdl
	 * @param complextypePrefix
	 * @param complextypeName
	 * @param rootElem
	 * @return
	 */
	public Element getComplextypeOfRoot(String complextypePrefix, String complextypeName, Element rootElem){
		
		List<Element> schemaList = this.getSchemaElemList(rootElem);
		Element complextypeElem = null;
		
		for (Element schema : schemaList) {
			if (complextypePrefix==null || complextypePrefix ==""){
				complextypeElem = this.getComplextypeOfSchema(complextypePrefix, complextypeName, schema);
				
			}else{
//				if(rootElem.getNamespace(complextypePrefix)==null) System.out.println(complextypePrefix+complextypeName);
			if(rootElem.getNamespace(complextypePrefix).getURI().equals(schema.getAttributeValue("targetNamespace"))){
				complextypeElem = this.getComplextypeOfSchema(complextypePrefix, complextypeName, schema);
				break;
			}

		}
		}
		
		return complextypeElem;
	}

	
	/**given schema element and complexType(prefix and name), return the complextype element in the schema
	 * if not found, return null
	 * @param complextypePrefix
	 * @param complextypeName
	 * @param schemaElem
	 * @return
	 */
	public Element getComplextypeOfSchema(String complextypePrefix, String complextypeName, Element schemaElem){
		
		Element rootElem = schemaElem.getDocument().getRootElement();
		//in case schema prefix is s, xs or xsd
		if (rootElem.getNamespace("xsd") != null) {
			xsdNS = rootElem.getNamespace("xsd");
		} else if (rootElem.getNamespace("xs") != null) {
			xsdNS = rootElem.getNamespace("xs");
		 
	} else if (rootElem.getNamespace("s") != null) {
		xsdNS = rootElem.getNamespace("s");
	}else
			System.out.println("Warning: the prefix of this schema is not xsd or xs or s, parser may fail!");
		
		String xsdPrefix = xsdNS.getPrefix();
		
		if (complextypePrefix != null && !complextypePrefix.equals("")){
			//make sure the namespace of complextype prefix is same as schema targetnamespace
			if(rootElem.getNamespace(complextypePrefix).getURI().equals(schemaElem.getAttributeValue("targetNamespace"))){
				try {
					Element tempElem = (Element) (XPath
							.selectSingleNode(schemaElem, "//"
									+ xsdPrefix
									+ ":schema/descendant::"
									+ xsdPrefix
									+ ":complexType[@name=\""
									+ complextypeName
									+ "\"]"));
						return tempElem;
					
				} catch (JDOMException e) {
					e.printStackTrace();
				}
			}
		}else{//no complextype prefix given, just search the complextype name in the given schema element
			try {
				Element tempElem = (Element) (XPath
						.selectSingleNode(schemaElem, "//"
								+ xsdPrefix
								+ ":schema/descendant::"
								+ xsdPrefix
								+ ":complexType[@name=\""
								+ complextypeName
								+ "\"]"));
					return tempElem;
				
			} catch (JDOMException e) {
				e.printStackTrace();
			}
		}
		//nothing found
		return null;
	}
	
	/**
	 * retrive whole schema Element for the WSDL
	 * @return
	 */
	public List<Element> getSchemaElemList(){		
		List<Element> schemaElemList= new ArrayList<Element>();			
			
		//get root element of xml file
		Element root = getRootElement();
		schemaElemList = getSchemaElemList(root);
			
		return schemaElemList;
	}	
	
	/**
	 * given wsdl root element, retrive whole schema Element
	 * @param rootElem
	 * @return
	 */
	public List<Element> getSchemaElemList(Element rootElem){	
		//to do: handle import xsd
		
		List<Element> schemaElemList= new ArrayList<Element>();			
		
		Element child = rootElem.getChild("types", wsdlNS);
		schemaElemList = child.getChildren("schema", xsdNS);
		int size = schemaElemList.size();
		
		String preXsdNs=schemaElemList.get(0).getNamespacePrefix();	
		if (preXsdNs!=""){
			xsdNS = Namespace.getNamespace(preXsdNs,"http://www.w3.org/2001/XMLSchema");
		}
					
		return schemaElemList;
	}

        public Element getRootElement(){
            return rootElement;
        }

	/**
	 * given sawsdl/wsdl file name, return root element
         * @param fileName 
         * @return
	 */
	public static Element getRootElem(String fileName){
		//String fileUrl=ClassLoader.getSystemResource(fileName).toString();
            String fileUrl = fileName;//Thread.currentThread().getContextClassLoader().getResource(fileName).toString();
		SAXBuilder sbuilder = new SAXBuilder();
		Element root=null;
				
		try{
			Document doc = sbuilder.build(fileUrl);
//			Document doc = sbuilder.build(fileName);
			//get root element of xml file
			root = doc.getRootElement(); 	
							
		}catch(JDOMException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
//		wsdlNS = root.getNamespace("wsdl");
//		sawsdlNS = root.getNamespace("sawsdl");
		if (root.getNamespace("xsd")!=null){
			xsdNS = root.getNamespace("xsd");
		}
		else if (root.getNamespace("s")!=null){
			xsdNS = root.getNamespace("s");
		
	} else if (root.getNamespace("xs") != null) {
		xsdNS = root.getNamespace("xs");
	}
		else System.out.println ("Warning: the prefix of this schema is not xsd or s or xs!");
		return root;
	}



	/**
	 * constructor
         *
         * @param file
         */
	public SchemaParser(String file) {
            SchemaParser.fileName = file;
            rootElement = getRootElem(file);
	}

        /**
         * Constructor
         * @param doc 
         */
        public SchemaParser(Document doc){
            rootElement = doc.getRootElement();
        }

	/**
	 * for test
	 * @param args
	 */
	public static void main(String[] args) {

            SchemaParser parser = new SchemaParser("http://cs.uga.edu/~guttula/wublast.wsdl");

            Element root = SchemaParser.getRootElem("http://cs.uga.edu/~guttula/wublast.wsdl");
            String name = root.getName();
            System.out.println("Root ele is : "+name);
            List<Element> lst = parser.getSchemaElemList(root);
            System.out.println("The schema elements are : "+lst);
            Element schema = lst.get(0);

	}

}
