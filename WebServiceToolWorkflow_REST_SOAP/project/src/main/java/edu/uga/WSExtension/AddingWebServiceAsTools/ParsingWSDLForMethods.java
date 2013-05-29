package edu.uga.WSExtension.AddingWebServiceAsTools;

import java.util.Iterator;
import java.util.List;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;

/**
 * This class is responsible for getting all the methods present in a 
 * WSDL file
 * @author Akshay Choche
 * @version 1.0
 *
 */
public class ParsingWSDLForMethods {
	
	public ParsingWSDLForMethods() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This method returns the corresponding SOAP port type from the list
	 * of Port Types
	 * @param portTypes
	 * @return
	 */
	public PortType getSOAPPortType(List<PortType> portTypes){
		if(portTypes.size() == 1){
			return portTypes.get(0);
		} else {
			for(PortType portType: portTypes){
				if(portType.getName().toLowerCase().contains("soap")){
					return portType;
				}
			}
			return null;
		}
	}
	
	/**
	 * This method return the SOAP methods from a given WADL.
	 * @param url
	 */
	public String getMethods(String url){
		StringBuilder output = new StringBuilder();
		WSDLParser parser = new WSDLParser();
		Definitions defination = parser.parse(url);
		PortType portType = getSOAPPortType(defination.getPortTypes());
		Iterator<Operation> operationItterator = portType.getOperations().iterator();
		if(operationItterator.hasNext()){
			output.append(operationItterator.next().getName() + "\t" +
		url + "\t" + url + "\n");
		}
		while(operationItterator.hasNext()) {
	        output.append(operationItterator.next().getName() + "\n");
		}
		return output.toString();
	}

	public static void main(String[] args){
		ParsingWSDLForMethods obj = new ParsingWSDLForMethods();
		System.out.println(obj.getMethods("http://www.ebi.ac.uk/Tools/services/soap/wublast?wsdl"));
	}
}
