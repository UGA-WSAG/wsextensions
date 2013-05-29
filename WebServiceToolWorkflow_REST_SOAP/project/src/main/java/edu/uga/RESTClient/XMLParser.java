package edu.uga.RESTClient;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This class has methods to parse the output received from the SOAP and REST clients
 * to display user readable output or xml files
 * @author Singaram
 *
 */
public class XMLParser
{
    private String result = null;
    
    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    /**
     * parse the given xml and return it as a string
     * @param xml file
     * @return string equivalent of the given xml
     * @throws JDOMException
     * @throws IOException
     */
    public String parseXml(String file) throws JDOMException, IOException
    {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(file);
        Element root = doc.getRootElement();
        printXmlContent(root, 0);
        return result;
    }
    
    /**
     * print the content of the xml document by parsing through line by line
     * @param root element of the xml
     * @param level of the element
     */
    public void printXmlContent(Element element, int i)
    {
        //Spaces to differentiate next level of nodes
        for(int n=0;n<i;n++)
        {
            this.setResult(this.getResult().concat("&nbsp;&nbsp;"));
            //System.out.print("  ");
        }
        
        //Print the current tag and content
        if(i == 0)
        {
            this.setResult("Root Element : " + element.getName() + " " + element.getTextTrim() + "<br/>");
            //System.out.println("Root Element : " + element.getName() + " " + element.getTextTrim() + "\n");
            List<Attribute> attributeList = element.getAttributes();
            printAllChildren(attributeList);
        }
        else
        {
            String concat = element.getName() + ":" + element.getTextTrim() + "<br/>";
            this.setResult(this.getResult().concat(concat));
            //System.out.println(element.getName() + ":" + element.getTextTrim());
            List<Attribute> attributeList = element.getAttributes();
            printAllChildren(attributeList);
        }
        
        //Get the list of children under the current element
        List allChildren = element.getChildren();
        Iterator iterator = allChildren.iterator();
        
        /*Iterates through the list of children under the current tag and if child exists, retrieves the child and calls
          printXmlContent recursively with i incremented to next level and prints that element's details, 
          Otherwise exits and i is set back to previous level
         */
        while (iterator.hasNext()) 
        {
            Element child = (Element) iterator.next();
            printXmlContent(child, i+1);
        }
    }

    /**
     * print all the children from a given list 
     * @param allChildren
     */
    private void printAllChildren(List allChildren)
    {
        Iterator iterator = allChildren.iterator();
        while(iterator.hasNext())
        {
            //System.out.println(iterator.next());
            String concat = iterator.next().toString() + "<br/>";
            this.setResult(this.getResult().concat(concat));
        }
        this.setResult(this.getResult().concat("<br/>"));
    }

}
