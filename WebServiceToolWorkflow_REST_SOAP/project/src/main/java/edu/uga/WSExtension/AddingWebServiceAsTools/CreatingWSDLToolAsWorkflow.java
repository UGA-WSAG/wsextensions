package edu.uga.WSExtension.AddingWebServiceAsTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.soamodel.Consts;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Part;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;

/**
 * This class is used to create workflow tools to be executed in Galaxy for each operation
 * @author Akshay Choche
 * @version 1.0
 *
 */
public class CreatingWSDLToolAsWorkflow {
	WSDLParser parser;
	Definitions wsdlDefinition;
	
	String user;
	String wsdlURL;
	String galaxyHomePath;
	String clientLoc;
	ArrayList<InputElement> rawList;
	ArrayList<InputElement> requiredElements;
	ArrayList<InputElement> optionalElements;
	
	/**
	 * Constructor for creating tools
	 * @param wsdlURL
	 * @param galaxyHomePath
	 */
	public CreatingWSDLToolAsWorkflow(String wsdlURL, String galaxyHomePath) {
		this.wsdlURL = wsdlURL;
		this.galaxyHomePath = galaxyHomePath;
		this.clientLoc = getWorkflowClientLocation();
		this.parser = new WSDLParser();	
		this.wsdlDefinition = parser.parse(this.wsdlURL);
		this.user = "\"user\"";
	}
	
	/**
	 * The Method returns the folder in which standalone client for Web Service operation
	 * would be stored
	 * @param galaxyHomePath Location where galaxy is stored
	 * @return
	 */
	public String getWorkflowClientLocation(){
		return galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/workflowclients";
	}
	
	/**
	 * A method that checks is the new tool to be added is already present in the
	 * repository. We keep track of each tool added to the system using a file called
	 * "ListOfTool.dat"
	 * @param operationName
	 * @return
	 */
	public boolean isToolAlreadyPresent(String wsdlURL, String operationName, String displayName){
		String filePath = galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/ListOfWorkflowTool.dat";
		try {
			Scanner scanPresence = new Scanner(new File(filePath));
			while(scanPresence.hasNextLine()){
				String toolName = scanPresence.nextLine();
				String[] partsOfToolName = toolName.split("\t");
				if(partsOfToolName.length == 4){
					if(partsOfToolName[0].equalsIgnoreCase(wsdlURL) && partsOfToolName[1].equalsIgnoreCase(operationName) && partsOfToolName[2].equalsIgnoreCase(displayName)){
						return true;
					}
				} else {
					return false;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	/**
	 * This method adds the entry for the tool into ListOfTool.dat
	 * @param wsdlURL
	 * @param operationName
	 * @param displayName
	 * @return
	 */
	public boolean addToolToTheList(String wsdlURL, String operationName, String displayName, String toolName){
		String filePath = galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/ListOfWorkflowTool.dat";
		try {
			Scanner scanPresence = new Scanner(new File(filePath));
			FileWriter fileWriter = new FileWriter(new File(galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/ListOfWorkflowTool_backup.dat"));
			while(scanPresence.hasNextLine()){
				String line = scanPresence.nextLine();
				if(!line.equals("")){
					fileWriter.write(line);
					fileWriter.write("\n");
					fileWriter.flush();
				}
			}
			fileWriter.write("\n");
			fileWriter.write(wsdlURL + "\t" + operationName + "\t" + displayName + "\t" + toolName +"\n");
			fileWriter.flush();
			fileWriter.close();
			scanPresence.close();
			
			//Copy backup into original
			scanPresence = new Scanner(new File(galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/ListOfWorkflowTool_backup.dat"));
			fileWriter = new FileWriter(new File(filePath));
			while(scanPresence.hasNextLine()){
				String line = scanPresence.nextLine();
				if(!line.equals("")){
					fileWriter.write(line);
					fileWriter.write("\n");
					fileWriter.flush();
				}
			}
			fileWriter.close();
			scanPresence.close();
			return true;
		} catch (FileNotFoundException e) {
			//Implies that the file is written into for the first time
			FileWriter fileWriter;
			try {
				fileWriter = new FileWriter(new File(filePath));
				fileWriter.write(wsdlURL + "\t" + operationName + "\t" + displayName + "\t" + toolName +"\n");
				fileWriter.flush();
				fileWriter.close();
				return true;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Gets the unique name for the tool used internally
	 * @return
	 */
	public String getTheNewToolName(){
		try {
			String countPath = galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/workflowclients/ClientCount.xml";
			Scanner scanCount = new Scanner(new File(countPath));
			scanCount.nextLine();
			Integer count = scanCount.nextInt();
			count++;
			scanCount.close();
			return "WorkflowClient_" + count;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Method which modifies the count in ClientCount.xml
	 * @return
	 */
	public boolean writeCount(){
		try {
			String countPath = galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/workflowclients/ClientCount.xml";
			Scanner scanCount = new Scanner(new File(countPath));
			scanCount.nextLine();
			Integer count = scanCount.nextInt();
			count++;
			scanCount.close();
			FileWriter writer = new FileWriter(new File(countPath));
			writer.write("<count> \n");
			writer.write(count + "\n");
			writer.write("</count>\n");
			writer.flush();
			writer.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Returns the name to be displayed in Galaxy
	 * @return
	 */
	public String getDisplayNameForTool(String operationName){
		String name = wsdlDefinition.getName();
		if(name == null || name.equals("")){
			name = this.wsdlURL;
		}
		name = name + " ";
		name += operationName;
		return name;
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
	 * This method returns the name of the root input element for the given operation
	 * @param operationName: Name of the operation
	 * @return Name of the root input element
	 */
	public String getInputElementName(String operationName){
		PortType portType = getSOAPPortType(this.wsdlDefinition.getPortTypes());
		Operation operation = portType.getOperation(operationName);
		if(operation == null){
			return null;
		}
		List<Part> inputParts = operation.getInput().getMessage().getParts();
		if(inputParts != null && inputParts.size() == 1){
			String[] name =inputParts.get(0).getElement().split(":");
			if(name.length == 1){
				return name[0];
			} else if(name.length == 2) {
				return name[1];
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * This method is responsible for returning the Input Element for the given operation
	 * @param operationName: Name of the operation
	 * @return The root element object for the input
	 */
	public Element getInputElement(String operationName){
		String elementName = getInputElementName(operationName);
		if(elementName == null){
			return null;
		}
		List<Schema> schemas = this.wsdlDefinition.getSchemas();
		for(Schema schema: schemas){
			Element element = schema.getElement(elementName);
			if(element != null)
				return element;
			//System.out.print(element);
		}
		return null;
	}
	
	/**
	 * Give the root element of the input this method populates rawList with the
	 * necessary inputs
	 * @param element: rootElement of the tree structure
	 * @param xpath: The xpath till element
	 */
	public void constructInputElementTree(Element element, String xpath) {
		ComplexType complexType = (ComplexType) element.getEmbeddedType();

		// check if the type is referenced using type=".."
		if (complexType == null) 
			complexType = element.getSchema().getComplexType(element.getType().getLocalPart());
		for (Element e : complexType.getSequence().getElements()) {
			// Fix for invalid schema
			if (e.getType() == null) {
				rawList.add(new InputElement(xpath+"/" +element.getName(), element.getName(), element));
				return;
			}
			if (e.getType().getNamespaceURI() == Consts.SCHEMA_NS) {
				rawList.add(new InputElement(xpath+"/"+e.getName(), e.getName(), e));
			} else {
				constructInputElementTree(e,xpath+"/"+e.getName());
			}
		}
	}
	
	/**
	 * Sets the raw list with the required parameters
	 * @param operationName
	 * @return
	 */
	public boolean parseInputsForTheOperation(String operationName){
		Element rootInputElement = getInputElement(operationName);
		if(rootInputElement == null){
			return false;
		}
		rawList = new ArrayList<InputElement>();
		requiredElements = new ArrayList<InputElement>();
		optionalElements = new ArrayList<InputElement>();
		
		//This method will populate the rawList
		constructInputElementTree(rootInputElement, "xpath:/" + rootInputElement.getName());
		if(rawList.isEmpty()){
			return false;
		}
		
		/** Determining the required and optional parameters
		 *  the required elements go in requireELements ArrayList
		 *  while the optional elements go in optionalElements
		 *  ArrayList
		 */
		for(InputElement element: rawList){
			if(!element.getElement().isNillable())
				requiredElements.add(element);
			else
				optionalElements.add(element);
		}
		return true;
	}
	
	/**
	 * Auxillary method used for getting parameter names based on counter
	 * @param counter
	 * @return
	 */
	public String getVariableNameToCheckIfSourceIsUser(int counter){
		return "$source" + counter + ".source" + counter + "_source";
	}
	
	/**
	 * Auxillary method used for getting parameter names based on counter
	 * @param counter
	 * @return
	 */
	public String getVariableNameToCheckIfSourceIsUserOptional(int counter){
		return "$cond_source.source" + counter + ".source" + counter + "_source";
	}
	
	/**
	 * Auxillary method used for getting parameter names based on counter
	 * @param counter
	 * @return
	 */
	public String getSourceUserParam(int counter){
		return "$source" + counter + ".user_param" + counter;
	}
	
	/**
	 * Auxillary method used for getting parameter names based on counter
	 * @param counter
	 * @return
	 */
	public String getSourceUserParamOptional(int counter){
		return "$cond_source.source" + counter + ".user_param" + counter;
	}
	
	/**
	 * Auxillary method used for getting parameter names based on counter
	 * @param counter
	 * @return
	 */
	public String getSourceCachedParam(int counter){
		return "$source" + counter + ".cached_param" + counter;
	}
	
	/**
	 * Auxillary method used for getting parameter names based on counter
	 * @param counter
	 * @return
	 */
	public String getSourceCachedParamOptional(int counter){
		return "$cond_source.source" + counter + ".cached_param" + counter;
	}
	
	/**
	 * This method is used for creating a tool for an operation with in galaxy
	 * @param wsdlURL
	 * @param operationName
	 * @param galaxyHomePath
	 * @return
	 */
	public int createTool(String operationName){
		if(wsdlDefinition == null){
			return -1;
		}
		//Check if the tool is already present
		if(isToolAlreadyPresent(this.wsdlURL, operationName, getDisplayNameForTool(operationName)) == true){
			System.out.println("The tool is already present");
			return 0;
		}
		if(!parseInputsForTheOperation(operationName)){
			return 1;
		}
		String toolName = getTheNewToolName();
		String toolXMLPath = this.clientLoc + "/" + toolName + ".xml";
		try {
			//Create a new xml file,
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(new File(toolXMLPath)));
			fileWriter.write("<tool id=\"" + toolName+"\" name=\"" + getDisplayNameForTool(operationName) + "\">");
			fileWriter.newLine();
			fileWriter.write("\t<description>URL$" + this.wsdlURL+"$Operation$" + operationName +"</description>");
			fileWriter.newLine();
			fileWriter.write("\t<command interpreter=\"python\">");
			fileWriter.newLine();
			fileWriter.write("\t\tclient_1.py $output $servicetype $url $method " + this.wsdlURL + " " + operationName);
			fileWriter.newLine();
			int counter = 0;
			fileWriter.write("\t\t#if $cond_source.optional_param_source==\"no\" ");
			fileWriter.newLine();
			//Implies that the optional parameters have not been selected
			for(InputElement element:requiredElements){
				fileWriter.write("\t\t\t#if " + getVariableNameToCheckIfSourceIsUser(counter) + " == " + this.user + ":");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t#if "+getSourceUserParam(counter)+":");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t\""+ element.getXpath() + "\"" + " " + getSourceUserParam(counter));
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t#end if");
				fileWriter.newLine();
				fileWriter.write("\t\t\t#else:");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\""+element.getXpath() + "\"");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\tfileInput");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t"+getSourceCachedParam(counter));
				fileWriter.newLine();
				fileWriter.write("\t\t\t#end if");
				fileWriter.newLine();
				fileWriter.flush();
				counter++;
			}
			fileWriter.write("\t\t#else");
			fileWriter.newLine();
			counter = 0;
			//Implies optional parameters need to be specified
			for(InputElement element:requiredElements){
				fileWriter.write("\t\t\t#if " + getVariableNameToCheckIfSourceIsUser(counter) + " == " + this.user + ":");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t#if "+getSourceUserParam(counter)+":");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t\""+ element.getXpath() + "\"" + " " + getSourceUserParam(counter));
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t#end if");
				fileWriter.newLine();
				fileWriter.write("\t\t\t#else:");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\""+element.getXpath() + "\"");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\tfileInput");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t"+getSourceCachedParam(counter));
				fileWriter.newLine();
				fileWriter.write("\t\t\t#end if");
				fileWriter.newLine();
				fileWriter.flush();
				counter++;
			}
			
			for(InputElement element:optionalElements){
				fileWriter.write("\t\t\t#if " + getVariableNameToCheckIfSourceIsUserOptional(counter) + " == " + this.user + ":");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t#if " + getSourceUserParamOptional(counter)+":");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t\""+ element.getXpath() + "\"" + " " + getSourceUserParamOptional(counter));
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t#end if");
				fileWriter.newLine();
				fileWriter.write("\t\t\t#else:");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\""+element.getXpath() + "\"");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\tfileInput");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t"+ getSourceCachedParamOptional(counter));
				fileWriter.newLine();
				fileWriter.write("\t\t\t#end if");
				fileWriter.newLine();
				fileWriter.flush();
				counter++;
			}
			fileWriter.write("\t\t#end if");
			//End of command tag
			fileWriter.newLine();
			fileWriter.write("\t</command>");
			fileWriter.newLine();
			
			fileWriter.write("\t<inputs>");
			fileWriter.newLine();
			fileWriter.write("\t\t<param name=\"servicetype\" type=\"hidden\" value=\"SOAP\" />");
			fileWriter.newLine();
			fileWriter.write("\t\t<param name=\"url\" type=\"hidden\" value=\""+this.wsdlURL+"\" />");
			fileWriter.newLine();
			fileWriter.write("\t\t<param name=\"method\" type=\"hidden\" value=\""+operationName+"\" />");
			fileWriter.newLine();
			fileWriter.flush();
			
			//Filling dynamic contents in <input> </input> tags
			counter = 0;
			//Filling the required parameters
			for(InputElement element: requiredElements){
				fileWriter.write("\t\t<conditional name=\"source"+ counter +"\">");
				fileWriter.newLine();
				fileWriter.write("\t\t\t<param name=\"source"+counter+"_source\" type=\"select\" label=\""+element.getName()+" Source\">");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t<option value=\"cached\" selected=\"true\">Param value will be taken from previous step</option>");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t<option value=\"user\">User will enter the param value</option>");
				fileWriter.newLine();
				fileWriter.write("\t\t\t</param>");
				fileWriter.newLine();
				fileWriter.write("\t\t\t<when value=\"user\">");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t<param format=\"text\" size=\"150\" name=\"user_param"+counter+"\" type=\"text\" label=\"Enter "+element.getName()+"\" help=\"see tip below\" />");
				fileWriter.newLine();
				fileWriter.write("\t\t\t</when>");
				fileWriter.newLine();
				fileWriter.write("\t\t\t<when value=\"cached\">");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t<param name=\"cached_param"+counter+"\" type=\"data\" label=\""+element.getName()+"\" />");
				fileWriter.newLine();
				fileWriter.write("\t\t\t</when>");
				fileWriter.newLine();
				fileWriter.write("\t\t</conditional>");
				fileWriter.newLine();
				fileWriter.flush();
				counter++;
			}
			
			//Filling in the optional parameters
			fileWriter.write("\t\t<conditional name=\"cond_source\">");
			fileWriter.newLine();
			fileWriter.write("\t\t\t<param name=\"optional_param_source\" type=\"select\" label=\"Show Optional Parameters\">");
			fileWriter.newLine();
			fileWriter.write("\t\t\t\t<option value=\"no\" selected=\"true\">no</option>");
			fileWriter.newLine();
			fileWriter.write("\t\t\t\t<option value=\"yes\">yes</option>");
			fileWriter.newLine();
			fileWriter.write("\t\t\t</param>");
			fileWriter.newLine();
			fileWriter.write("\t\t\t<when value=\"no\"></when>");
			fileWriter.newLine();
			fileWriter.write("\t\t\t<when value=\"yes\">");
			fileWriter.newLine();
			fileWriter.flush();			
			for(InputElement element: optionalElements){
				fileWriter.write("\t\t\t\t<conditional name=\"source"+ counter +"\">");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t<param name=\"source"+counter+"_source\" type=\"select\" label=\""+element.getName()+" Source\">");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t\t<option value=\"cached\" selected=\"true\">Param value will be taken from previous step</option>");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t\t<option value=\"user\">User will enter the param value</option>");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t</param>");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t<when value=\"user\">");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t\t<param format=\"text\" size=\"150\" name=\"user_param"+counter+"\" type=\"text\" label=\"Enter "+element.getName()+"\" help=\"see tip below\" />");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t</when>");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t<when value=\"cached\">");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t\t<param name=\"cached_param"+counter+"\" type=\"data\" label=\""+element.getName()+"\" />");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t\t</when>");
				fileWriter.newLine();
				fileWriter.write("\t\t\t\t</conditional>");
				fileWriter.newLine();
				fileWriter.flush();
				counter++;
			}
			fileWriter.write("\t\t\t</when>");
			fileWriter.newLine();
			fileWriter.write("\t\t</conditional>");
			fileWriter.newLine();
			fileWriter.write("\t</inputs>");
			fileWriter.newLine();
			fileWriter.flush();			
			fileWriter.write("\t<outputs>");
			fileWriter.newLine();
			fileWriter.write("\t\t<data format=\"tabular\" name=\"output\" />");
			fileWriter.newLine();
			fileWriter.write("\t</outputs>");
			fileWriter.newLine();
			fileWriter.write("\t<help>");
			fileWriter.newLine();
			counter = 0;
			for(InputElement element: rawList){
				fileWriter.newLine();
				fileWriter.write(".. class:: infomark");
				fileWriter.newLine();
				fileWriter.newLine();
				fileWriter.write("**TIP:** About " + element.getName()+": type is "+element.getElement().getBuildInTypeName());
				fileWriter.newLine();
			}
			fileWriter.newLine();
			fileWriter.write("\t</help>");
			fileWriter.newLine();
			fileWriter.write("</tool>");
			fileWriter.close();
			
			//Add it to our list of tools
			if(!addToolToTheList(wsdlURL, operationName, getDisplayNameForTool(operationName), toolName)){
				return 2;
			}
			
			//Add an entry to tool conf
			if(!EditToolConf.editToolConfForWorkflowTool(galaxyHomePath, toolName)){
				return 3;
			}
			
			//Modify Client Count
			if(!writeCount()){
				return 4;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -2;
		} 
		return 5;
	}
	
	public static void main(String[] args){
		CreatingWSDLToolAsWorkflow wrkflow = new CreatingWSDLToolAsWorkflow("http://www.ebi.ac.uk/Tools/services/soap/wublast?wsdl", "/Users/akshaychoche/galaxy-dist");
		//System.out.println(wrkflow.getVariableNameToCheckIfSourceIsUser(100));
		//System.out.println(wrkflow.user);
		//System.out.println(wrkflow.getSourceUserParam(10));
		//System.out.println(wrkflow.getSourceCachedParam(11));
		wrkflow.createTool("getResult");
	}
}
