package edu.uga.WSExtension.AddingWebServiceAsTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class is used for removing tools from the inventory,
 * 1. Delete from tool_conf.xml
 * 2. Delete from personalized tool list we maintain
 * 3. Delete the actual xml
 * @author Akshay Choche
 */
public class RemoveTool {

	String galaxyHomePath;
	String toolConfPath;
	String listOfToolPath;
	String listOfWorkflowToolPath;
	String webServiceExtensionPath;
	
	/**
	 * Default constructor
	 */
	public RemoveTool(){
		
	}
	
	/**
	 * Constructor used for initializing the path for different folders used in 
	 * removing tool 
	 * @param galaxyHomePath	The home path of galaxy-dist
	 */
	public RemoveTool(String galaxyHomePath){
		this.galaxyHomePath = galaxyHomePath;
		this.toolConfPath = galaxyHomePath + "/tool_conf.xml";
		this.listOfToolPath = galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/ListOfTool.dat";
		this.listOfWorkflowToolPath = galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/ListOfWorkflowTool.dat";
		this.webServiceExtensionPath = galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP";
	}
	
	/**
	 * This method is used for removing tool from our custom tool list for standalone tools
	 * @param operationName Name of the tool to be removed
	 * @return
	 */
	public boolean removeFromStandAloneToolList(String operationName){
		try {
			Scanner scanPresence = new Scanner(new File(this.listOfToolPath));
			FileWriter fileWriter = new FileWriter(new File(this.galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/ListOfTool_backup.dat"));
			while(scanPresence.hasNextLine()){
				String line = scanPresence.nextLine();
				String[] components = line.split("\t");
				if(components.length == 4 && !line.equals("") && !components[3].equalsIgnoreCase(operationName)){
					fileWriter.write(line);
					fileWriter.write("\n");
					fileWriter.flush();
				}
			}
			fileWriter.close();
			scanPresence.close();
			
			//Copy backup into original
			scanPresence = new Scanner(new File(galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/ListOfTool_backup.dat"));
			fileWriter = new FileWriter(new File(this.listOfToolPath));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
		
	/**
	 * This method is used for removing tool from our custom tool list for workflow tools
	 * @param operationName Name of the tool to be removed
	 * @return
	 */
	public boolean removeFromWorkflowToolList(String operationName){
		try {
			Scanner scanPresence = new Scanner(new File(this.listOfWorkflowToolPath));
			FileWriter fileWriter = new FileWriter(new File(this.galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/ListOfWorkflowTool_backup.dat"));
			while(scanPresence.hasNextLine()){
				String line = scanPresence.nextLine();
				String[] components = line.split("\t");
				if(components.length == 4 && !line.equals("") && !components[3].equalsIgnoreCase(operationName)){
					fileWriter.write(line);
					fileWriter.write("\n");
					fileWriter.flush();
				}
			}
			fileWriter.close();
			scanPresence.close();
			
			//Copy backup into original
			scanPresence = new Scanner(new File(galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/ListOfWorkflowTool_backup.dat"));
			fileWriter = new FileWriter(new File(this.listOfWorkflowToolPath));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * This method is used for removing tool from tool config
	 * @param lineToRemove
	 * @return
	 */
	public boolean removeFromToolConf(String lineToRemove){
		try {
			Scanner scanToolConf = new Scanner(new File(this.toolConfPath));
			FileWriter fileWriter = new FileWriter(new File(this.galaxyHomePath + "/tool_conf_backup.xml"));
			while(scanToolConf.hasNextLine()){
				String lineRead = scanToolConf.nextLine();
				String tooLook = lineRead.trim();
				if(!tooLook.equalsIgnoreCase(lineToRemove)){
					fileWriter.write(lineRead);
					fileWriter.write("\n");
					fileWriter.flush();
				}
			}
			scanToolConf.close();
			fileWriter.close();

			//Copy backup back to tool_conf.xml
			scanToolConf = new Scanner(new File(this.galaxyHomePath +"/tool_conf_backup.xml"));
			fileWriter = new FileWriter(new File(this.galaxyHomePath +"/tool_conf.xml"));
			while(scanToolConf.hasNextLine()){
				String lineRead = scanToolConf.nextLine();
				fileWriter.write(lineRead);
				fileWriter.write("\n");
				fileWriter.flush();
			}
			fileWriter.close();
			scanToolConf.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Deletes the client stubs w.r.t. to the tool being removed
	 * @param filePath
	 * @return
	 */
	public boolean deleteFile(String filePath){
		File toDelete = new File(filePath);
		if(toDelete.delete()){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method helps us remove our stand alone tools
	 * @param operationName The official tool name
	 * @return true if tool was removed successfully
	 */
	public boolean removeStandAloneTool(String operationName){
		
		//Step 1: Delete from the tool_conf.xml
		String toolFullPath = "WebServiceToolWorkflow_REST_SOAP/clients/" + operationName+ ".xml";
		String lineToRemove = "<tool file=\""+toolFullPath+"\"/>";
		if(!removeFromToolConf(lineToRemove)){
			return false;
		}
		
		//Step 2: Try to delete the actual file
		String filePath = this.galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/clients/" + operationName + ".xml";
		if(!deleteFile(filePath)){
			return false;
		}

		//Step 3: Delete from personalized tool list we maintain
		if(!removeFromStandAloneToolList(operationName)){
			return false;
		}			
		return true;
	}
	
	/**
	 * This method helps us remove our workflow tools
	 * @param operationName The official tool name
	 * @return true if tool was removed successfully
	 */
	public boolean removeWorkflowTool(String operationName){
		//Step 1: Delete from the tool_conf.xml
		String toolFullPath = "WebServiceToolWorkflow_REST_SOAP/workflowclients/" + operationName + ".xml";
		String lineToRemove = "<tool file=\""+toolFullPath+"\"/>";
		if(!removeFromToolConf(lineToRemove)){
			return false;
		}

		//Step 2: Try to delete the actual file
		String filePath = this.galaxyHomePath + "/tools/WebServiceToolWorkflow_REST_SOAP/workflowclients/" + operationName + ".xml";
		if(!deleteFile(filePath)){
			return false;
		}

		//Step 3: Delete from personalized tool list we maintain
		if(!removeFromWorkflowToolList(operationName)){
			return false;
		}			
		return true;
	}
	
	public String test(){
		return ("Liz Lemon is a Dummy!!!!");
	}
}
