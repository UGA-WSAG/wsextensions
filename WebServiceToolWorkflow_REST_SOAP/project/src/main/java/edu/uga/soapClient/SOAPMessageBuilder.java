package edu.uga.soapClient;

import groovy.xml.MarkupBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import com.predic8.wsdl.*;
import com.predic8.wstool.creator.*;

/**
 * This class is used for building SOAP Messages using the membrane 
 * library. You need WSDL URL as well as the operation name.
 * 
 * @author Akshay Choche
 * @version 1.0
 * @see "http://www.membrane-soa.org/soa-model-doc/1.2/create-soap-request.htm"
 */
public class SOAPMessageBuilder {
	
	/**
	 * The method returns a SOAP binding name from the WSDL provided by the
	 * user.
	 * @param bindings List of bindings present in a WSDL
	 * @return
	 */
	public static String getBindingName(List<Binding> bindings){
		if(bindings.size() == 1){
			return bindings.get(0).getName();
		} else {
			for(Binding binding: bindings){
				if(binding.getName().toLowerCase().contains("soap")){
					return binding.getName();
				}
			}
			return "";
		} // if
	} // getBindingName
	
	/**
	 * The method returns a SOAP port type name from the WSDL provided by the
	 * user.
	 * @param portTypes List of port types present in a WSDL
	 * @param operationName The name of operation to execute
	 * @return
	 */
	public static String getPortTypeName(List<PortType> portTypes, String operationName){
		if(portTypes.size() == 1){
			return portTypes.get(0).getName();
		} else {
			for(PortType portType: portTypes){
				if(portType.getName().toLowerCase().contains("soap")){
					return portType.getName();
				}
			}
			return "";
		} // if
	} // getPortTypeName

	/**
	 * The method is used for creating SOAP Messages
	 * @param wsdlLocation
	 * @param operationName
	 * @param inputs
	 * @return
	 */
	public static OMElement createSOAPMessage(URL wsdlLocation, String operationName, Map<String, String> inputs) {
		//Getting the WSDL parsers
		com.predic8.wsdl.WSDLParser parser = new com.predic8.wsdl.WSDLParser();
		com.predic8.wsdl.Definitions wsdl = parser.parse(wsdlLocation.toString());
		StringWriter writer = new StringWriter();
		
		//Obtain Binding Name and PortType Name for the SOAP Message
		String bindingName = getBindingName(wsdl.getBindings());
		String portTypeName = getPortTypeName(wsdl.getPortTypes(), operationName);
		if (bindingName == null || bindingName.equals("")||portTypeName == null || portTypeName.equals("")) {
			return null;
		} // if
		
		//SOARequestCreator constructor: SOARequestCreator(Definitions, Creator, MarkupBuilder)
		SOARequestCreator creator = new SOARequestCreator(wsdl, new RequestCreator(), new MarkupBuilder(writer));
		creator.setFormParams(inputs);
		
		//creator.createRequest(PortType name, Operation name, Binding name);
	    creator.createRequest(portTypeName, operationName, bindingName);
	    FileWriter fwriter;
		try {
			//Temporary file which stores the soap message.
			fwriter = new FileWriter(new File("soapMessagetemp.xml"));
			fwriter.write(writer.toString());
		    fwriter.close();
		    OMElement finalMessage = new StAXOMBuilder(new FileInputStream(new File("soapMessagetemp.xml"))).getDocumentElement();
		    return finalMessage.getFirstElement().getFirstElement();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null;
		} // try
	} // createSOAPMessage
	
	/**
	 * Main method to test the soap message generator
	 * @param args
	 */
	public static void main(String[] args){
		try {
			HashMap<String, String> formParams = new HashMap<String, String>();
		    formParams.put("xpath:/Add/a", "10");
		    formParams.put("xpath:/Add/b", "20");
			System.out.println(SOAPMessageBuilder.createSOAPMessage(new URL("http://www.html2xml.nl/Services/Calculator/Version1/Calculator.asmx?WSDL"), "Add", formParams));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
