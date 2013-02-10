'''
@author  Akshay Choche, Chaitanya Guttula
@see     LICENSE (MIT style license file).
'''

import jpype
import logging
import warnings
import urllib2
import platform
import os,sys
with warnings.catch_warnings():
    warnings.simplefilter("ignore")

'''input : wadl/wsdl/sawadl-url, method name
purpose: 
1. Calls methods from generateClient1.py to generate client description for one-time invocation
of the Web service. This client description is added as a xml file under ./clients/
2. Calls methods from generateClient.py to generate client description for invocation of Web service
in a workflow. This client description is added as a xml file under ./workflowclients/
3. Adds the path to the above xml files to Galaxy tool-conf.xml file using call edit_tool_conf.py
'''

"""
This code is added to extend the logging functionality to the tool
"""
logger = logging.getLogger('myapp')
logger_home = str(os.environ.get('GALAXY_HOME')) + '/tools/WebServiceToolWorkflow_REST_SOAP/Logs/Adding_Web_Service_As_Tool/step_b.log'
hdlr = logging.FileHandler(logger_home)
formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
hdlr.setFormatter(formatter)
logger.addHandler(hdlr) 
logger.setLevel(logging.INFO)

logger.info("Executing the script 'WebServiceTool_input_method_m.py'")
servicetype=''

#read the url passed as an argument
url = sys.argv[2]
while(url.find('__tilda__')>-1):
    ulist = url.split('__tilda__')
    url = '~'.join(ulist)

logger.info("The URL obtained is: " + str(url))

#split url passed on '.' character
urllist = url.split('/')
wsdlname = urllist[len(urllist)-1].split(".")
if len(wsdlname)==1:
    wsdlname =urllist[len(urllist)-1].split('?')

'''#checks the description document (WSDL/WADL) is for SOAP 1.1 or SOAP 2.0 or REST
   If the extesnion is wsdl then servicetype is SOAP else If the extension is wadl then servicetype is REST
   in other conditions(i.e. the file has no extension)
'''
u = urllib2.urlopen(url)
logger.info("Saving the Web Service Description file locally: ")
descfile = open('temp','w')
descfile.write(u.read())
descfile.close()
readwsdl = open('temp','r')
tempstring = 'start'
    
#checks the description document (WSDL/WADL) is for SOAP 1.1 or SOAP 2.0 or REST    
while (tempstring != ''):
    tempstring = readwsdl.readline()
    if tempstring.find('<definitions') != -1 or tempstring.find('<wsdl:definitions') != -1:
       print 'WSDL 1.1 or 1.0'
       logger.info("The Web Service Description file is of type WSDL 1.1 or WSDL 1.0: ")
       servicetype = 'SOAP'
       break
    elif tempstring.find('description') != -1:
         while tempstring != '':
            if tempstring.find('<bindings ') != -1:
                bind = tempstring.split('type')
                bindLength = len(bind)
                bindingtype = bind[bindLength-1]
                break
            tempstring = readwsdl.readline()
            if bindingtype.find('http://www.w3.org/ns/wsdl/soap') != -1:    
                servicetype='WSDL2SOAP'
            else:
                servicetype='WSDL2REST'
            break
    elif tempstring.find('<application') != -1:
	logger.info("The Web Service Description file is of type REST")
        servicetype='REST'
        break
        
readwsdl.close()        
operations = sys.argv[3].split(',')              
    
logger.info("The list of operations to be added are as follows: " + str(operations))
logger.info("Writting to the output file: " + str(sys.argv[4]))
logger.info("Attempting to start creating client for the following Web Service: " + str(url))

# The webservice is a soap webservice
if servicetype == 'SOAP': 

    logger.info("Setting up jpype variables")   
    galaxyHomePath = str(os.environ.get('GALAXY_HOME'))
    jar_home = str(os.environ.get('GALAXY_HOME')) + '/tools/WebServiceToolWorkflow_REST_SOAP/engine'
    jarpath = os.path.join(os.path.abspath(jar_home), '')
    logger.info("Starting the JVM")
    jpype.startJVM(jpype.getDefaultJVMPath(), "-Djava.ext.dirs=%s" % jarpath)
    logger.info("JVM is up and running")
    toolCreatorPackage = jpype.JPackage("edu.uga.WSExtension.AddingWebServiceAsTools")
    logger.info("Creating objects to create stand alone and workflow tool")
    standAloneToolCreator = toolCreatorPackage.CreatingWSDLToolAsStandalone(url, galaxyHomePath)
    logger.info("Object for stand alone tool has been created successfully")
    workflowToolCreator = toolCreatorPackage.CreatingWSDLToolAsWorkflow(url, galaxyHomePath)
    logger.info("Object for workflow tool has been created successfully")
    for operation in operations:
        if operation != '':
	    logger.info("Creating tool stand alone and workflow tool for " + str(operation))
	    statusOfStandAlone = standAloneToolCreator.createTool(operation)
	    logger.info("Stand alone tool created successfully " + str(statusOfStandAlone))
	    statusOfWorkflowTool = workflowToolCreator.createTool(operation)
	    logger.info("Workflow tool created successfully " + str(statusOfWorkflowTool))
			
#Web service is a REST Webservice
if servicetype == 'REST':
    print "REST"
