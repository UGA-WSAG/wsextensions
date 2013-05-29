package edu.uga.WSExtension.AddingWebServiceAsTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class EditToolConf {

	/**
	 * The method is used for editing the tool conf for Stand alone tool
	 * @param galaxyHome
	 * @param toolName
	 * @return
	 */
	public static boolean editToolConfForStandAloneTool(String galaxyHome, String toolName){
		String newToolLine = "    <tool file=\"WebServiceToolWorkflow_REST_SOAP/clients/"+toolName + ".xml\"/>";
		String tooLookFor = "<section name=\"Select Web Service Tool\" id=\"WebServices\">";
		try {
			//Make a copy of tool_conf.xml and add a new line corresponding to the tool
			Scanner scanToolConf = new Scanner(new File(galaxyHome+"/tool_conf.xml"));
			FileWriter backupWriter = new FileWriter(new File(galaxyHome +"/tool_conf_backup.xml"));
			while(scanToolConf.hasNextLine()){
				String lineRead = scanToolConf.nextLine();
				if(lineRead.trim().equalsIgnoreCase(tooLookFor)){
					backupWriter.write(lineRead);
					backupWriter.write("\n");
					backupWriter.flush();
					backupWriter.write(newToolLine);
					backupWriter.write("\n");
					backupWriter.flush();
					continue;
				}
				backupWriter.write(lineRead);
				backupWriter.write("\n");
				backupWriter.flush();
			}
			backupWriter.close();
			scanToolConf.close();
			
			//Copy backup back to tool_conf.xml
			scanToolConf = new Scanner(new File(galaxyHome +"/tool_conf_backup.xml"));
			backupWriter = new FileWriter(new File(galaxyHome+"/tool_conf.xml"));
			while(scanToolConf.hasNextLine()){
				String lineRead = scanToolConf.nextLine();
				backupWriter.write(lineRead);
				backupWriter.write("\n");
				backupWriter.flush();
			}
			backupWriter.close();
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
	 * The method is used for editing the tool conf as workflow tool
	 * @param galaxyHome
	 * @param toolName
	 * @return
	 */
	public static boolean editToolConfForWorkflowTool(String galaxyHome, String toolName){
		String newToolLine = "    <tool file=\"WebServiceToolWorkflow_REST_SOAP/workflowclients/"+toolName + ".xml\"/>";
		String tooLookFor = "<section name=\"Select Web Service Workflow Tool\" id=\"WebServiceWorkflow\">";
		try {
			//Make a copy of tool_conf.xml and add a new line corresponding to the tool
			Scanner scanToolConf = new Scanner(new File(galaxyHome+"/tool_conf.xml"));
			FileWriter backupWriter = new FileWriter(new File(galaxyHome +"/tool_conf_backup.xml"));
			while(scanToolConf.hasNextLine()){
				String lineRead = scanToolConf.nextLine();
				if(lineRead.trim().equalsIgnoreCase(tooLookFor)){
					backupWriter.write(lineRead);
					backupWriter.write("\n");
					backupWriter.flush();
					backupWriter.write(newToolLine);
					backupWriter.write("\n");
					backupWriter.flush();
					continue;
				}
				backupWriter.write(lineRead);
				backupWriter.write("\n");
				backupWriter.flush();
			}
			backupWriter.close();
			scanToolConf.close();
			
			//Copy backup back to tool_conf.xml
			scanToolConf = new Scanner(new File(galaxyHome +"/tool_conf_backup.xml"));
			backupWriter = new FileWriter(new File(galaxyHome+"/tool_conf.xml"));
			while(scanToolConf.hasNextLine()){
				String lineRead = scanToolConf.nextLine();
				backupWriter.write(lineRead);
				backupWriter.write("\n");
				backupWriter.flush();
			}
			backupWriter.close();
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
	
	public static void main(String[] args){
		System.out.println(EditToolConf.editToolConfForStandAloneTool("/Users/akshaychoche/galaxy-dist", "akshay"));
	}
}
