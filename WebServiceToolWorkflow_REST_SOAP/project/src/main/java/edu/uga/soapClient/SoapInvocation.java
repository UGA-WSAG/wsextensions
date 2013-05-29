package edu.uga.soapClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import static java.lang.System.out;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;

import com.predic8.wsdl.Operation;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wsdl.Definitions;


/**
 * This class invokes the given operation in a given SOAP service taking in the 
 * required user inputs and returns an Axis OMElement
 * @author Akshay Choche, John A. Miller, Singaram
 *
 */
public class SoapInvocation
{
	static private WSDLParser 	parser = new WSDLParser();	//The WSDL parser used
	private URL 				url;						//The url for the WSDL we are trying to execute
    private String 				operationName;    			//The operation name in WSDL we are trying to execute
    private List<WSInvokeInput> inputList; 					//List of input parameters for execution
    private Definitions 		wsdl;						//The DOM tree generated using the parser
    
    /**
     * Constructor for the SoapInvocation to set the required parameters
     * @param url - wsdlUrl
     * @param operationName - operation to be invoked
     * @throws MalformedURLException
     */
    public SoapInvocation(URL url, String operationName, List<WSInvokeInput> inputList) throws MalformedURLException, WSDLException
    {
        this.url 		   = url;
        this.operationName = operationName;
        this.inputList     = inputList;
        this.wsdl          = parser.parse(url.toString());
    } // constructor 
        
    /**
     * Utility method used internally for converting the format from Galaxy to the compatible 
     * format used by membrane
     * @param input
     * @return
     */
    public Map<String, String> convertIPToHashMap(List<WSInvokeInput> input) 
    {
    	Map<String, String> inputMap = new HashMap<String, String>();
    	for (WSInvokeInput in: input) {
    		inputMap.put(in.getName(), in.getValue());
    	} // for
    	return inputMap;
    } // convertIPToHashMap

    /**
     * The method is used for dynamic invocation of the web service method
     * @return
     * @throws WSDLException
     */
    public String dynamicInvoke() throws WSDLException
    {
        ServiceClient serviceClient = null;								//Dynamic service client
        try {
            serviceClient = new ServiceClient(null, url, null, null);
        } catch (AxisFault ex) {
            ex.printStackTrace();
        } // catch

        PortType wsdlPortType = getPortType(wsdl.getPortTypes());		//Get the porttype corresponding to SOAP 1.1/1.2 
        QName qName = getOperationQName(wsdlPortType, operationName);	//Get the QName for the operation you want to execute
        
        OMElement result = null;										//The OMElement which store the output
        String parsedResultForGalaxy = null;
        try {
            if(serviceClient == null) {
                return "Error: Service Client not initialized";
            } else {
                //store the xml response in the OMElement object result 
            	OMElement element = SOAPMessageBuilder.createSOAPMessage(this.url, this.operationName, convertIPToHashMap(this.inputList));
            	out.println("The SOAP message generated: " + element);
            	result = serviceClient.sendReceive(qName, element);
            	parsedResultForGalaxy = convertOMElementToString(result);
            } // if
        } catch (AxisFault ex) {
        	ex.printStackTrace();
            return "error";
        } // try 
        out.println("Result is: "+ result);
        return parsedResultForGalaxy;
    } // dynamicInvoke
    
    /**
     * Converts the given OMElement into Galaxy format
     * @param element
     */
    public String convertOMElementToString(OMElement element)
    {
    	String output = "";
    	LinkedList<OMElement> listOfElements = new LinkedList<OMElement>();
    	listOfElements.add(element);
    	while (!listOfElements.isEmpty()) {
    		OMElement currentElement = listOfElements.removeFirst();
    		if (currentElement.getText() != null && !currentElement.getText().equals("")) {
    			output += currentElement.getText() + "\n";
    		} // if
    		Iterator<OMElement> childs = currentElement.getChildElements();
    		while (childs.hasNext()) {
    			listOfElements.addLast(childs.next());
    		} //while
    	} // while
    	return output;
    } // convertOMElementToString
    
    /**
	 * The method returns a SOAP port type from the WSDL provided by the
	 * user.
	 * @param portTypes List of port types present in a WSDL
	 * @param operationName The name of operation to execute
	 * @return
	 */
	public PortType getPortType(List<PortType> portTypes)
	{
		if (portTypes.size() == 1) {
			return portTypes.get(0);
		} else {
			for (PortType portType: portTypes) {
				if(portType.getName().toLowerCase().contains("soap")){
					return portType;
				}
			} // for
			return null;
		} // if
	} // getPortType
	
	/**
	 * Gets the QName for the given operation 
	 * @param portType
	 * @param operationName
	 * @return
	 */
	public QName getOperationQName(PortType portType, String operationName)
	{
		for (Operation operation: portType.getOperations()) {
			if(operation.getName().equalsIgnoreCase(operationName)) {
				QName qname = new QName(wsdl.getTargetNamespace(), operation.getName());
				return qname;
			} // if
		} // for
		return null;
	} // getOperationQName
    
	/**
	 * For testing the code
	 */
    public void test(){
	try {	
    		
			WSInvokeInput firstOperand = new WSInvokeInput();
			firstOperand.setName("xpath:/run/email");
			firstOperand.setRequired(true);
			firstOperand.setValue("akshaychoche@gmail.com");
			WSInvokeInput secondOperand = new WSInvokeInput();
			secondOperand.setName("xpath:/run/parameters/program");
			secondOperand.setRequired(true);
			secondOperand.setValue("blastp");
			WSInvokeInput thirdOperand = new WSInvokeInput();
			thirdOperand.setName("xpath:/run/parameters/stype");
			thirdOperand.setRequired(true);
			thirdOperand.setValue("protein");
			WSInvokeInput fourthOperand = new WSInvokeInput();
			fourthOperand.setName("xpath:/run/parameters/sequence");
			fourthOperand.setRequired(true);
			fourthOperand.setValue("MYLPSKIGQKGDEVDKMKSANEEASPSGSSSGRSTKSPGVFPTFSVAAIPPRAAVSSLATRGIFDLSERPKEISCRNDDAFVAHGAYLLFNAIKRKLTESSYAKVVIGLSGGSTPLPIYSALRHLALASADHEPGRAATHLATAVPEFSEELQRDAKGSDSALDWTRVFFFLVDERYVHPTHADSNQRSIRKHLLGQPNGVACDQPGAECGGDVLPVPEKNLIFPDTSLPLEDCIVKYRSALLELLAATQQIDLVTLGLGPDGHIASIFPPLSEEDLKEQMNPNPIVLHTTTSRFAGFDRITVSLQLLCGAHQKVFFLKGDEKIRLWRDMQDDARSKSVAEFPALAVLQSGNVKVVAVPPLDVHEEHLQQQLRADRTFLSVVVLGASGDLAHKKTYPALFSLFCEGLLPPHFHIVGYARSKMTFDQFWEKISQKLKSLSSFFCRRASAIDLLASFKSHCSYLQGLYDRPADFANLGNHLKEVEGDAEQVGRVLYLALPPDVFLPSVKSYRQSCWNTKGWNRVVVEKPFGRDLKSSDKLSASLMALLREREIFRIDHYLGKEMSLSLTALRFANVAFMPLFHRDYVHSVRITFKEQSGTWRRGGYFDNYGIIRDVMQNHMIQLLTLVAMERPASLKDDDIRDEKVKVLKQMPPVKISETVLGQFTKSVDGQLPGYTDDDTVPKDSKTPTFCTCVLWINNERWSGVPFIFKAGKALESKTTEVRVQLREAPAGASFFHEPNLTPNELVILVQPHEAVYLKIHTKKPGLLSQGLQPTELDLSVMDRFDVERLPDAYERLLLDVIRGDKQNFVRTDELREAWRIFTPLLHEIEEKNIDPLPYPAGSSGPSASYDLIQKYYSYKQSNYKWTPPKRETSSDTVQ");
			WSInvokeInput fifthOperand = new WSInvokeInput();
			fifthOperand.setName("xpath:/run/parameters/database/string");
			fifthOperand.setRequired(true);
			fifthOperand.setValue("uniprotkb");
			
			List<WSInvokeInput> ip = new ArrayList<WSInvokeInput>();
			ip.add(firstOperand);
			ip.add(secondOperand);
			ip.add(thirdOperand);
			ip.add(fourthOperand);
			ip.add(fifthOperand);
			
    		/*
    		WebServiceInvokeInput firstOperand = new WebServiceInvokeInput();
			firstOperand.setName("xpath:/Add/a");
			firstOperand.setRequired(true);
			firstOperand.setValue("20");
			WebServiceInvokeInput secondOperand = new WebServiceInvokeInput();
			secondOperand.setName("xpath:/Add/b");
			secondOperand.setRequired(true);
			secondOperand.setValue("20");
			List<WebServiceInvokeInput> ip = new ArrayList<WebServiceInvokeInput>();
			ip.add(firstOperand);
			ip.add(secondOperand);
			*/
			SoapInvocation soapWebClient = new SoapInvocation(new URL("http://www.ebi.ac.uk/Tools/services/soap/wublast?wsdl"), "run", ip);
			//SoapInvocation soapWebClient = new SoapInvocation(new URL("http://www.html2xml.nl/Services/Calculator/Version1/Calculator.asmx?WSDL"), "Add", ip);
			out.println(soapWebClient.dynamicInvoke());
    	} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WSDLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args){
    	try {	
    		/*
			WebServiceInvokeInput firstOperand = new WebServiceInvokeInput();
			firstOperand.setName("xpath:/run/email");
			firstOperand.setRequired(true);
			firstOperand.setValue("akshaychoche@gmail.com");
			WebServiceInvokeInput secondOperand = new WebServiceInvokeInput();
			secondOperand.setName("xpath:/run/parameters/program");
			secondOperand.setRequired(true);
			secondOperand.setValue("blastp");
			WebServiceInvokeInput thirdOperand = new WebServiceInvokeInput();
			thirdOperand.setName("xpath:/run/parameters/stype");
			thirdOperand.setRequired(true);
			thirdOperand.setValue("protein");
			WebServiceInvokeInput fourthOperand = new WebServiceInvokeInput();
			fourthOperand.setName("xpath:/run/parameters/sequence");
			fourthOperand.setRequired(true);
			fourthOperand.setValue("MYLPSKIGQKGDEVDKMKSANEEASPSGSSSGRSTKSPGVFPTFSVAAIPPRAAVSSLATRGIFDLSERPKEISCRNDDAFVAHGAYLLFNAIKRKLTESSYAKVVIGLSGGSTPLPIYSALRHLALASADHEPGRAATHLATAVPEFSEELQRDAKGSDSALDWTRVFFFLVDERYVHPTHADSNQRSIRKHLLGQPNGVACDQPGAECGGDVLPVPEKNLIFPDTSLPLEDCIVKYRSALLELLAATQQIDLVTLGLGPDGHIASIFPPLSEEDLKEQMNPNPIVLHTTTSRFAGFDRITVSLQLLCGAHQKVFFLKGDEKIRLWRDMQDDARSKSVAEFPALAVLQSGNVKVVAVPPLDVHEEHLQQQLRADRTFLSVVVLGASGDLAHKKTYPALFSLFCEGLLPPHFHIVGYARSKMTFDQFWEKISQKLKSLSSFFCRRASAIDLLASFKSHCSYLQGLYDRPADFANLGNHLKEVEGDAEQVGRVLYLALPPDVFLPSVKSYRQSCWNTKGWNRVVVEKPFGRDLKSSDKLSASLMALLREREIFRIDHYLGKEMSLSLTALRFANVAFMPLFHRDYVHSVRITFKEQSGTWRRGGYFDNYGIIRDVMQNHMIQLLTLVAMERPASLKDDDIRDEKVKVLKQMPPVKISETVLGQFTKSVDGQLPGYTDDDTVPKDSKTPTFCTCVLWINNERWSGVPFIFKAGKALESKTTEVRVQLREAPAGASFFHEPNLTPNELVILVQPHEAVYLKIHTKKPGLLSQGLQPTELDLSVMDRFDVERLPDAYERLLLDVIRGDKQNFVRTDELREAWRIFTPLLHEIEEKNIDPLPYPAGSSGPSASYDLIQKYYSYKQSNYKWTPPKRETSSDTVQ");
			WebServiceInvokeInput fifthOperand = new WebServiceInvokeInput();
			fifthOperand.setName("xpath:/run/parameters/database/string");
			fifthOperand.setRequired(true);
			fifthOperand.setValue("uniprotkb");
			
			List<WebServiceInvokeInput> ip = new ArrayList<WebServiceInvokeInput>();
			ip.add(firstOperand);
			ip.add(secondOperand);
			ip.add(thirdOperand);
			ip.add(fourthOperand);
			ip.add(fifthOperand);
			*/
    		
    		WSInvokeInput firstOperand = new WSInvokeInput();
    		firstOperand.setName("xpath:/decode/base64");
    		firstOperand.setRequired(true);
    		firstOperand.setValue("VFI6QjZLUlczX1RPWEdPClRSOkI5UFJQOF9UT1hHTwpUUjpGMFZSRTVfTkVPQ0wKVFI6UTI3NzQxX1BMQUZBClRSOlE4SUtVMF9QTEFGNwpUUjpCM0w5TjFfUExBS0gKVFI6SzZVV004XzlBUElDClRSOkE1SzNNMV9QTEFWUwpUUjpGNFA2UDZfQkFUREoKVFI6RDhVMlI1X1ZPTENBClRSOkQwTVQwM19QSFlJVApUUjpRNlBDSDRfWEVOTEEKVFI6RjBXSUgzXzlTVFJBClRSOlEyOERJOV9YRU5UUgpUUjpRNUJLTTlfWEVOVFIKVFI6UTlTVEM3X0RVTkJJClRSOkkxSVdHOV9CUkFESQpUUjpDNVBCNjVfQ09DUDcKVFI6RTlESFo0X0NPQ1BTClRSOkozSzRJNl9DT0NJTQpUUjpBMUQ3SzFfTkVPRkkKVFI6STBZWEQ5XzlDSExPClRSOkU3RkRZN19EQU5SRQpUUjpLMlJNQzRfTUFDUEgKVFI6STNLMVUwX09SRU5JClRSOkozTFoxN19PUllCUgpUUjpJMkZUMzVfVVNUSE8KVFI6RjBYUlo3X0dST0NMClRSOlE0UlZZMV9URVRORwpTUDpHNlBEM19BUkFUSApUUjpCMkFZMThfUE9EQU4KVFI6QTFDSlc3X0FTUENMClRSOkIwWFlFNF9BU1BGQwpUUjpRNFdYNzRfQVNQRlUKVFI6SDNHNzUxX1BIWVJNClRSOkI2U1dWMV9NQUlaRQpTUDpHNlBEX0VNRU5JClRSOkcwUkRQN19IWVBKUQpUUjpHMlEyTTBfVEhJSEEKU1A6RzZQRF9DUklHUgpUUjpHNFkzNjlfQ1JJR1IKVFI6RjhOTkEwX1NFUkw5ClRSOkY4UFAyN19TRVJMMwpUUjpDMUZINTNfTUlDU1IKVFI6QjZIRzIxX1BFTkNXClRSOkc5UDZLN19IWVBBSQpUUjpGOE4yTDJfTkVVVDgKVFI6RzRVNVQ0X05FVVQ5ClRSOkkzTjdXNV9TUEVUUgpUUjpJMVBNRTdfT1JZR0wKVFI6STdISU43X09SWVNBClRSOlE3WDdJNl9PUllTSgpUUjpCNVgxSTNfU0FMU0EKVFI6SDNEQ0E2X1RFVE5HClRSOkg2QlNVOF9FWE9ETgpUUjpCMU1UVDlfQ0FMTU8KVFI6RzNUNjM2X0xPWEFGClRSOkc5TjlIOV9IWVBWRwpUUjpIMFc2VzFfQ0FWUE8KVFI6UTlCSFQ5X1BMQUJFClRSOkQ1R0lVOV9UVUJNTQpUUjpFOUVDQzJfTUVUQVEKVFI6STNMNjc3X1BJRwpUUjpGOVhJQzJfTVlDR00KVFI6RDhTUkc5X1NFTE1MClRSOkU1QUJJM19MRVBNSgpUUjpHNE1SODJfTUFHTzcKVFI6SjNQRkY5X0dBR1QzClRSOkIyS0lLNV9SSElGRQpUUjpCN05aUjBfUkFCSVQKVFI6RjdETUc1X0hPUlNFClRSOkszWTYyN19TRVRJVApUUjpCMlZVRjJfUFlSVFIKVFI6RTNRNVA3X0NPTEdNClRSOkUzUzhZM19QWVJUVApUUjpRNFA0TjNfVVNUTUEKVFI6QjBLV1Q5X0NBTEpBClRSOkQ4U05FOF9TRUxNTApUUjpRMDE4RTRfT1NUVEEKVFI6STNLSzQyX09SRU5JClRSOkcxRUhJM19DQU1EUgpUUjpCMkJQMzVfUElNUFIKVFI6SDJNQUk4X09SWUxBClRSOkU2WlEzNV9TUE9SRQpUUjpCNUZXOTlfT1RPR0EKVFI6RjdEWkMzX09STkFOClRSOkcxTDBMOV9BSUxNRQpUUjpHM1ZIRjRfU0FSSEEKVFI6QzVZRDc3X1NPUkJJClRSOk81NzY1NV9UQUtSVQpTUDpHNlBEX01BQ1JPClRSOlEwQ1hHMV9BU1BUTgpUUjpGN0IwTDZfTU9ORE8KVFI6UThSWTUxX09SWVNKClRSOlE3UlJEOF9QTEFZTwpTUDpHNlBEX0hVTUFOClNQOkc2UERfUkFUClNQOlAxMTQxMy0zClRSOkM1SkhIMF9BSkVEUwpUUjpGMlQxSzJfQUpFREEKVFI6RzBTQ0w0X0NIQVREClRSOkcyWDJHNF9WRVJEVgpUUjpJNFk3NDJfV0FMU0MKVFI6UTBVRlA4X1BIQU5PClRSOkczUk1NMl9HT1JHTwpUUjpLN0NDQzRfUEFOVFIKVFI6STFRQTAwX09SWUdMClRSOlE3RVlLOV9PUllTSgpUUjpRN1hBVjdfT1JZU0oKVFI6UTc2QkQ2X0FDSUJFClRSOkUxWkdGMV9DSExWQQpUUjpCOUlBVDFfUE9QVFIKVFI6QjhNU0s0X1RBTFNOClRSOkI4TVNLNV9UQUxTTgpUUjpCMkxYVzFfQk9PTUkKVFI6QTlUQTU0X1BIWVBBClRSOlE3NkJCNV9QT1RNTwpUUjpGMkRDOThfSE9SVkQKVFI6RjJETVQ0X0hPUlZEClRSOks0QllXMV9TT0xMQwpUUjpHNUJGTDVfSEVUR0EKU1A6RzZQRDFfQVJBVEgKU1A6RzZQRF9BU1BORwpUUjpBMlFFVDFfQVNQTkMKVFI6QjhOMDIyX0FTUEZOClRSOkMwTkFTMV9BSkVDRwpUUjpDNkg0OTFfQUpFQ0gKVFI6RTVRWkY5X0FSVEdQClRSOkYwVUpDNl9BSkVDOApUUjpHM0s1MDdfQVNQT1oKVFI6RzNZMjI2X0FTUE5BClRSOkk4VFpQMF9BU1BPMwpUUjpBM1JJNTNfQk9PTUkKVFI6RjFNTUsyX0JPVklOClRSOkg5RVNONV9NQUNNVQpUUjpIOUVTVjdfTUFDTVUKVFI6RTZSOTc0X0NSWUdXClRSOkY5RjFDNF9GVVNPRgpUUjpHN0U4RDJfTUlYT1MKVFI6SjlNQjYxX0ZVU080ClRSOko5VlNWNF9DUllOVgpUUjpLM1Y1TDVfRlVTUEMKVFI6RDdLQVA2X0FSQUxMClRSOkgyTFJYNF9PUllMQQpUUjpIM0Q0WDVfVEVUTkcKVFI6RTFaWTE4X0NBTUZPClRSOkQySTNCNF9BSUxNRQpTUDpHNlBEMV9NT1VTRQpTUDpHNlBENV9BUkFUSApUUjpHN1hDMzhfQVNQS1cKVFI6QTJTVUc3X0JPT01JClRSOlE0NVI0NV9CT09NSQpUUjpBOUNCNjlfUEFQQU4KVFI6QjlHTU44X1BPUFRSClRSOkszWDZENF9QWVRVTApUUjpRNUU5MTlfQVJBVEgKVFI6UTNUTkwxX01PVVNFClRSOlE3OTBZOF9NT1VTRQpUUjpIMlJTVTlfVEFLUlUKVFI6UTc2QkE5XzlDSE9OClRSOkI2SzIzM19TQ0hKWQpUUjpBOEs4RDlfSFVNQU4KVFI6RTJSMEk5X0NBTkZBClRSOkE0Ulk0OF9PU1RMVQpUUjpGMkRJNjRfSE9SVkQKVFI6UTRHMzM5XzlQRVJPClRSOkc3S1MyOF9NRURUUgpUUjpCNlFWUTJfUEVOTVEKVFI6QzdZSzU1X05FQ0g3ClRSOksxWDI3N19NQVJCVQpUUjpHM01OVzhfOUFDQVIKVFI6QTlTRkU5X1BIWVBBClRSOkI5R1pMOF9QT1BUUgpUUjpCNlFWUTNfUEVOTVEKVFI6QzVGVFU2X0FSVE9DClRSOko0SFdNMF85QVBIWQpUUjpHMVBCMDdfTVlPTFUKVFI6QTJZS0cxX09SWVNJClRSOkEzQklVNV9PUllTSgpUUjpPMjI0MDRfUEVUQ1IKVFI6RzNORkE2X0dBU0FDClNQOkc2UERfTUVEU0EKU1A6RzZQRF9UQUtSVQpUUjpBN1dMSjBfSE9SVlUKVFI6UTlMTDg4X1RPQkFDClRSOkgyUlNWMF9UQUtSVQpUUjpCMENZQzNfTEFDQlMKVFI6QjlSTUE4X1JJQ0NPClRSOkI2VFNCM19NQUlaRQpUUjpDMUdBQjRfUEFSQkQKVFI6RDdNNTY5X0FSQUxMClRSOkE3RjVLOF9TQ0xTMQpUUjpEM0JCMDdfUE9MUEEKVFI6STFHRlY0X0FNUFFFClRSOkcxU1RaNF9SQUJJVApUUjpHNFpLTDFfUEhZU1AKVFI6SzdGWjczX1BFTFNJClRSOkszWTZQN19TRVRJVApTUDpHNlBEX0RJQ0RJClRSOkE4TlhJOV9DT1BDNwpUUjpKNFVJMjRfQkVBQjIKVFI6UTlTVDY3X1NPTFRVClRSOkQ4UzI4N19TRUxNTApUUjpGMFpDNjdfRElDUFUKVFI6RjJaOVIzX05JQ0JFClRSOlE2SzVINV9PUllTSgpUUjpHM1AxQTBfR0FTQUMKU1A6RzZQRDJfQVJBVEgKVFI6QjJMWFczX0JPT01JClRSOlE4SDlDOF9TT0xUVQpTUDpHNlBEQ19UT0JBQwpUUjpFOUlQVThfU09MSU4KVFI6QTlURlozX1BIWVBBClRSOkI4QUYwN19PUllTSQpUUjpDMUdRMzhfUEFSQkEKVFI6QThYMFowX0NBRUJSClRSOlE3NkJDMl85Q0hPTgpUUjpRNFlURDRfUExBQkEKVFI6RjVIRVQ1X0NSWU5CClRSOlE1S0RQM19DUllOSgpUUjpIMlVRVjhfVEFLUlUKVFI6RjdIUlUwX01BQ01VClRSOkUzSzVJMl9QVUNHVApUUjpJMVAxWDJfT1JZR0wKVFI6SzRBNzcxX1NFVElUClRSOkczTkZCMl9HQVNBQwpUUjpJMU41MDhfU09ZQk4KVFI6UTI1ODU2X1BMQUZBClRSOkQ3VTVZMF9WSVRWSQpUUjpRNzZCRjFfTEVQT1MKVFI6STFKRFQ5X1NPWUJOClRSOlEyVkY0Ml9IVU1BTgpUUjpRMlZGNDFfUEFOVFIKVFI6QzBQRlgwX01BSVpFClRSOkY2SEhQM19WSVRWSQpUUjpJMUlaMTBfQlJBREkKVFI6RjJaOVIyX05JQ0JFClRSOkQ4UlZJN19TRUxNTApUUjpHN0tVUzVfTUVEVFIKVFI6RzFYQjM0X0FSVE9BClRSOkczSkxJOV9DT1JNTQpUUjpRMlE5SDJfSFVNQU4KVFI6QTBGRjQ0XzlNVVJJClRSOks1VjhEN19QSEFDUwpUUjpINldOQTRfRlJBQU4KVFI6RjRXQjgzX0FDUkVDClRSOkgyUFhBMF9QT05BQgpUUjpHMlJGMzNfVEhJVEUKVFI6SzVYQUYzX0FHQUJVClRSOlEySERVOV9DSEFHQgpUUjpRMlE5QjdfSFVNQU4KVFI6QzBQTVIzX01BSVpFClRSOkQ4UzJYMV9TRUxNTApUUjpRNzZCSDJfTEVQUEEKVFI6SDlJRFM3X0FUVENFClRSOkQ4UzhCN19TRUxNTApUUjpRNzZCSDlfUFJPQU4KVFI6RjdBWDYyX0NJT0lOClRSOko5SktRMl9BQ1lQSQpUUjpEN01IVzFfQVJBTEwKVFI6QTlSWVU4X1BIWVBBClRSOkI5U1c1Ml9SSUNDTwpUUjpKM01LNjNfT1JZQlIKVFI6TzY1ODU2X1RPQkFDClRSOkYxTDA4M19BU0NTVQpUUjpDMFBUNjNfUElDU0kKVFI6SzNZUk42X1NFVElUClRSOkEwRkY0Nl9NVVNDRQpUUjpRNzZCRTRfQU1JQ0EKVFI6RjRSQjU4X01FTExQClRSOlE3UlY4Nl9ORVVDUgpUUjpGMlNQNDdfVFJJUkMKVFI6RjRSNzgwX01FTExQClRSOkgwRUNUNl9HTEFMNwpTUDpHNlBEX0NBRUVMClRSOlE5WlNSMV9NRVNDUgpUUjpEN0xMWjNfQVJBTEwKVFI6RzNJQkw0X0NSSUdSClRSOlE5SUFEMV9QSU1QUgpUUjpFOUVLSjZfTUVUQVIKVFI6RzBQNlI1X0NBRUJFClRSOkozTEVIMl9PUllCUgpUUjpRNE1aODdfVEhFUEEKVFI6RjhTTUMzX1NPWUJOClRSOkYyQ1hFMl9IT1JWRApTUDpHNlBENl9BUkFUSApUUjpRMVdCVTRfVFJZQ1IKVFI6RDdNSjM5X0FSQUxMClRSOlEwV1NMMF9BUkFUSApUUjpBOVRWVTBfUEhZUEEKVFI6QzRKWk0zX1VOQ1JFClRSOkE2WElHMF9QRUEKVFI6QTlTQTM4X1BIWVBBClRSOkgyUlNWMV9UQUtSVQpUUjpDNUdIOTlfQUpFRFIKVFI6STFON0cxX1NPWUJOClRSOkEwRkY0M185TVVSSQpUUjpRNzZCRjhfT1JZTEEKVFI6RzdOU001X01BQ01VClRSOkU5R0NMOV9EQVBQVQpUUjpEMktUVThfQ0hMVlUKVFI6RDRCNUwwX0FSVEJDClRSOkQ0RDJROF9UUklWSApUUjpEOFBOVjZfU0NIQ00KVFI6RzFLTUgyX0FOT0NBClNQOkc2UERfU0NIUE8KVFI6QzFNTFgwX01JQ1BDClRSOlE4TE5aN19DSExWVQpUUjpRMlRMVzNfSVBTVFkKVFI6UTJUTFc0X0lQU1RZClRSOkEwRkY0NV9NVVNDTwpUUjpFMUZWTTlfTE9BTE8KVFI6UTBJRUw4X0FFREFFClRSOlExV0JVNl9UUllDUgpUUjpRNEUwQjJfVFJZQ0MKVFI6QTlTNkQyX1BIWVBBClRSOkEwRkY0Ml9NVVNDUgpUUjpRNzZCRzVfQU1CTUUKVFI6QTZSM1gxX0FKRUNOClRSOkQ2V0tLOV9UUklDQQpUUjpIMllBVTVfQ0lPU0EKU1A6RzZQRENfU1BJT0wKVFI6STFSQ0c1X0dJQlpFClRSOkE5VVlMMl9NT05CRQpUUjpRMVdCVTVfVFJZQ1IKVFI6RzdRMjI4X01BQ0ZBClRSOkYyRDA0NF9IT1JWRApUUjpJMUg0TTZfQlJBREkKU1A6RzZQRENfU09MVFUKVFI6RTJCREM2X0hBUlNBClRSOks0RTFZOF9UUllDUgpUUjpKOVA5RTlfQ0FORkEKVFI6QjlJSks4X1BPUFRSClRSOlE0UzVNM19URVRORwpUUjpBMVhJODVfU0hFRVAKVFI6RjJaOVIxX05JQ0JFClRSOkkwWjlWMV85Q0hMTwpUUjpLNENFVzBfU09MTEMKVFI6RTlCNEg0X0xFSU1VClRSOkkxSUI5N19CUkFESQpUUjpLNEJESDlfU09MTEMKVFI6QzNZVjgxX0JSQUZMClRSOkE3WVZXMl9BQ1RDSApUUjpCN1EzMzFfSVhPU0MKVFI6TzIyNDA2X1BFVENSClRSOkIwV0hHOF9DVUxRVQpUUjpROEk5MTFfTEVJQU0KVFI6SzdMN1I3X1NPWUJOClRSOkI0RFlBN19IVU1BTgpUUjpBN1VGSDhfOVRSWVAKVFI6QjNSVVEzX1RSSUFEClRSOkIzUkZFMl9TT1JBUgpUUjpEN1VCSDJfVklUVkkKVFI6RjJENlozX0hPUlZEClRSOkE0SEFEM19MRUlCUgpUUjpBN1VGSDJfTEVJQlIKVFI6QTdVRkg0X0xFSVBFClRSOko5RVg2N19XVUNCQQpTUDpHNlBEX1NPTFRVClRSOkE4UTFNNV9CUlVNQQpUUjpRNzZCQTJfQlJBQkUKVFI6RDNVNzE4X0hPUlZEClRSOkEyQ0lMM19MRUlETwpUUjpDMFBJVzFfTUFJWkUKVFI6RDdURDcyX1ZJVFZJClRSOlE4NjlCN19MRUlNRQpUUjpRMlhUQzRfU09MVFUKVFI6QTJDSUwxX0xFSUlOClRSOkozTFBSNl9PUllCUgpUUjpRMkw5VjhfUE9QVFIKVFI6UTJMOVY5X1BPUFRSClRSOlE3NkJBNV9MRVRSSQpUUjpBMkNJSzFfTEVJSU4KVFI6QTdVRkg3XzlUUllQClRSOkI5R0laNF9QT1BUUgpUUjpHM1VXRDZfTU9VU0UKVFI6UTBLSEI4X0NSQUdJClNQOkc2UERfQ0VSQ0EKVFI6QTJDSU02X0xFSUlOClRSOlE4STkwOV9MRUlHVQpUUjpROUxSSjFfV0hFQVQKVFI6RjJRMkMzX1RSSUVDClRSOkEyQ0lKOF9MRUlJTgpUUjpBMkNJSzZfTEVJRE8KVFI6QTJDSUs4X0xFSURPClRSOkI0TDdaM19EUk9NTwpUUjpFOUJRRzJfTEVJREIKVFI6UTZTWFAyXzlST1NJClRSOkEwRkYzOF9NT1VTRQpUUjpBMkNJTThfOVRSWVAKU1A6RzZQRDJfTU9VU0UKVFI6QTdVRkg1X0xFSUJSClRSOlE5TFJKMF9XSEVBVApUUjpBMEZGMzlfTVVTU0kKVFI6QTBGRjQxX01VU1NQClRSOlE0VUJNNF9USEVBTgpUUjpIMktNRjNfQU5PR0EKVFI6UTRRM0sxX0xFSU1BClRSOkU5QzVSMV9DQVBPMwpUUjpIOUo4WDRfQk9NTU8KVFI6QTVKTk0wX09OQ01ZClRSOkY3VzFZNV9TT1JNSwpUUjpPNjU4NTRfVE9CQUMKVFI6UTlMUkk5X1dIRUFUClRSOkEyQ0lNOV9MRUlUUgpUUjpCMkxYVzVfQk9PTUkKVFI6RTFaS0w3X0NITFZBClRSOkkxQlkxM19SSElPOQpUUjpDMUUwSDlfTUlDU1IKVFI6SzFWSEs1X1RSSUFDClRSOkEyU1VHOF9CT09NSQpUUjpCMkxYVzJfQk9PTUkKVFI6QTBGRjQwX01VU01BClRSOkUzV1c1M19BTk9EQQpUUjpDMU1SNzJfTUlDUEMKVFI6RTNNN1UzX0NBRVJFClRSOk8yMjQwNV9QRVRDUgpUUjpIMVZKMThfQ09MSEkKVFI6UTVESDgzX1NDSEpBClRSOkc4WUlZNV9QSUNTTwpUUjpKNkYxVzdfVFJJQVMKVFI6STNWSk0wX0xFSURPClRSOkkzVkpNMV9MRUlETwpUUjpJM1ZKTTRfTEVJSU4KVFI6STNWSk03XzlUUllQClRSOkkzU1JWMV9MT1RKQQpUUjpRNzZCQzlfUE9MT1IKVFI6QzBTMVgxX1BBUkJQClRSOkkxQ1VZNF9SSElPOQpUUjpJM1ZKTDhfOVRSWVAKVFI6STNWSk05XzlUUllQClRSOkkzVkpOMF9MRUlETwpUUjpJM1ZKTTZfTEVJRE8KVFI6STNWSk4yXzlUUllQClRSOkQ4TThHM19CTEFITwpUUjpPNjU4NTVfVE9CQUMKVFI6QjhBSlIxX09SWVNJClRSOkkxUEM4OF9PUllHTApUUjpRNzVJWjlfT1JZU0oKVFI6UTdRTEgxX0FOT0dBClRSOkYyVTFOMl9TQUxTNQpUUjpCNEpKRzlfRFJPR1IKVFI6RDJWMzYyX05BRUdSClRSOkIzTVFDNF9EUk9BTgpUUjpCNE1TUThfRFJPV0kKVFI6RzBVNVA1X1RSWVZZClRSOkI3RkxWOV9NRURUUgpUUjpJM1ZKTDlfTEVJRE8KVFI6QjNNTDk3X0RST0FOClRSOkIzTlZTMV9EUk9FUgpUUjpRMVdLVDFfRFJPRVIKVFI6QjRQWkUyX0RST1lBClRSOlExV0tTN19EUk9ZQQpUUjpCN0ZOSzBfRFJPTUUKVFI6QjRINFIzX0RST1BFClRSOlEyOUhZOF9EUk9QUwpUUjpRMzhCWjZfVFJZQjIKVFI6RDdGTEQwX0VDVFNJClRSOlE2QlVKMF9ERUJIQQpUUjpEMEExVDJfVFJZQjkKVFI6UTFXS1M4X0RST1RFClRSOlE5R1JHN185VFJZUApTUDpHNlBEX0RST1lBClRSOkswSzhNMV85QVNDTwpTUDpHNlBEX0RST01FClNQOlAxMjY0Ni0yClRSOlE2QzRZN19ZQVJMSQpUUjpRMVdLVDBfRFJPT1IKVFI6QjlUODI2X1JJQ0NPClRSOkI4QVZUMF9PUllTSQpUUjpCOUZGVDNfT1JZU0oKVFI6UTFXS1M5X0RST1NJClRSOlE5U1RENF9DWUFDQQpUUjpBNUI3TjdfVklUVkkKVFI6QjRJNzk3X0RST1NFClRSOkY0UFc0Ml9ESUNGUwpUUjpHN1k1RjJfQ0xPU0kKVFI6UTJMOVY3X1BPUFRSClRSOkQ4UE5WNF9TQ0hDTQpUUjpHMFVWTjdfVFJZQ0kKVFI6SzFQTTI5X0NSQUdJClRSOks3S1owNF9TT1lCTgpUUjpKN1M2VDJfS0FaTkEKVFI6SDJBUEUxX0tBWkFGClRSOkM0UjA5OV9QSUNQRwpUUjpGMlFURTVfUElDUDcKVFI6UTZGUDA2X0NBTkdBClRSOko0QzNWMl9USEVPUgpUUjpBNlpSSzJfWUVBUzcKVFI6QjNMUDgwX1lFQVMxClRSOkI1VlFHOF9ZRUFTNgpUUjpDN0dQQjlfWUVBUzIKVFI6RTdLSEU1X1lFQVNBClRSOkcyV0xNNV9ZRUFTSwpUUjpIMEdNOTdfOVNBQ0gKVFI6SjdSTUcyX0tBWk5BClRSOkM4WkZaNV9ZRUFTOApUUjpBN1NSSzZfTkVNVkUKVFI6RzhKVVM3X0VSRUNZClNQOkc2UERfWUVBU1QK");
			
    		List<WSInvokeInput> ip = new ArrayList<WSInvokeInput>();
    		ip.add(firstOperand);
    		/*
    		WebServiceInvokeInput firstOperand = new WebServiceInvokeInput();
			firstOperand.setName("xpath:/Add/a");
			firstOperand.setRequired(true);
			firstOperand.setValue("20");
			WebServiceInvokeInput secondOperand = new WebServiceInvokeInput();
			secondOperand.setName("xpath:/Add/b");
			secondOperand.setRequired(true);
			secondOperand.setValue("20");
			List<WebServiceInvokeInput> ip = new ArrayList<WebServiceInvokeInput>();
			ip.add(firstOperand);
			ip.add(secondOperand);
			*/
			SoapInvocation soapWebClient = new SoapInvocation(new URL("http://mango.ctegd.uga.edu/jkissingLab/SWS/webservices/wsConverter.wsdl"), "decode", ip);
			//SoapInvocation soapWebClient = new SoapInvocation(new URL("http://www.html2xml.nl/Services/Calculator/Version1/Calculator.asmx?WSDL"), "Add", ip);
			out.println("Before Invocation!!!!");
			out.println(soapWebClient.dynamicInvoke());
			soapWebClient.test();
    	} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WSDLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}