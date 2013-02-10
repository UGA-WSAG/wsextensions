'''
@author  Akshay Choche
@see     LICENSE (MIT style license file).
'''
import jpype
import logging
import warnings
with warnings.catch_warnings():
    warnings.simplefilter("ignore")
    import warnings
    import urllib2
    import platform
    import os,sys

'''
This scriot is responsible for calling the java code that removes tools from the
tool menu in Galaxy
'''
logger = logging.getLogger('myapp')
logger_home = str(os.environ.get('GALAXY_HOME')) + '/tools/WebServiceToolWorkflow_REST_SOAP/Logs/Remove_Tools/removetool.log'
hdlr = logging.FileHandler(logger_home)
formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
hdlr.setFormatter(formatter)
logger.addHandler(hdlr) 
logger.setLevel(logging.INFO)

operationToRemove = sys.argv[1]
outputFile = sys.argv[2]
logger.info("Attempting to remove " + str(operationToRemove) + " from Workflow tools")
jar_home = str(os.environ.get('GALAXY_HOME')) + '/tools/WebServiceToolWorkflow_REST_SOAP/engine'
jarpath = os.path.join(os.path.abspath(jar_home), '')
jpype.startJVM(jpype.getDefaultJVMPath(), "-Djava.ext.dirs=%s" % jarpath)
removeClientPackage = jpype.JPackage("edu.uga.WSExtension.AddingWebServiceAsTools")
removeClient = removeClientPackage.RemoveTool(str(os.environ.get('GALAXY_HOME')))
test = removeClient.removeWorkflowTool(str(operationToRemove))
logger.info("The tool has been deleted from tool_conf.xml, personlaized list and the stub")
