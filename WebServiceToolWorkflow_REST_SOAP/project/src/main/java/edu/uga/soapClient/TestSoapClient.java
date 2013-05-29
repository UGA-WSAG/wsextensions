package edu.uga.soapClient;

import java.net.MalformedURLException;
import java.net.URL;

import javax.wsdl.WSDLException;

public class TestSoapClient {
	
	public TestSoapClient() {
		
	}
	
//	public void testMethod(int operand1, int operand2) {
//    	try {
//			SoapInvocation soapWebClient = new SoapInvocation(new URL("http://www.html2xml.nl/Services/Calculator/Version1/Calculator.asmx?WSDL"), "Add");
//			WebServiceInvokeInput firstOperand = new WebServiceInvokeInput();
//			firstOperand.setName("a");
//			firstOperand.setType("Add");
//			firstOperand.setRequired(true);
//			firstOperand.setValue(operand1+"");
//			WebServiceInvokeInput secondOperand = new WebServiceInvokeInput();
//			secondOperand.setName("b");
//			secondOperand.setType("Add");
//			secondOperand.setRequired(true);
//			secondOperand.setValue(operand2+"");
//			soapWebClient.getInputList().add(firstOperand);
//			soapWebClient.getInputList().add(secondOperand);
//			soapWebClient.dynamicInvoke();
//    	} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (WSDLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
	
	public static void main(String[] args){
		TestSoapClient soap = new TestSoapClient();
		//soap.testMethod();
	}
}
