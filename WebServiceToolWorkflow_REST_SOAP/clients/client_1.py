'''
@author  Akshay Choche, Chaitanya Guttula, Sumedha Ganjoo
@see     LICENSE (MIT style license file).
'''
import jpype
import logging
import warnings
with warnings.catch_warnings():
    warnings.simplefilter("ignore")
    import sys
    import os
    import urllib
    
    """
    Execute WebService Operation
    This script takes the values of the input parameters of the
    web service from the user and invokes the web service.
    The URL to the WSDL is the command line argument

    """
    def convertPythonToJavaParameters(inputs):
        for each in inputs:
	    print(each + " : " + str(inputs[each]))
	    inputObject = soapClient.WebServiceInvokeInput()
	    inputObject.setName(each)
	    inputObject.setValue(str(inputs[each]))
	    inputObject.setType("Add")
	    inputArrayList.add(inputObject)

    """
    This code is added to extend the logging functionality to the tool

    """
    logger = logging.getLogger('myapp')
    logger_home = str(os.environ.get('GALAXY_HOME')) + '/tools/WebServiceToolWorkflow_REST_SOAP/Logs/WS_Extension_Logs/web_service_ext_single_operation.log'
    hdlr = logging.FileHandler(logger_home)
    formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    hdlr.setFormatter(formatter)
    logger.addHandler(hdlr) 
    logger.setLevel(logging.INFO)
    
    servicetype = sys.argv[2]
    if servicetype=='SOAP':
        with warnings.catch_warnings():
            warnings.simplefilter("ignore")
            galaxyhome=os.environ.get('GALAXY_HOME')
	    logger.info('Request to execute SOAP Web Service operation')
            webservice = str(sys.argv[3])
            operation = str(sys.argv[4])
            resultfile=open(sys.argv[1],'w')
            length=len(sys.argv)
            inputdict={}
            i=5
	    counter1 = length-1
	    while(counter1 >= 5):
	        logger.info("Input parameter " + sys.argv[counter1])
		counter1 = counter1 - 1
            while(i>=5 and i<(length-1)):
                key=sys.argv[i]
                print 'key is : ',key
                val=sys.argv[i+1]
                while(val.find('__at__')>-1):
                    vlist = val.split('__at__')
                    val = '@'.join(vlist)
                while(val.find('__sq__')>-1):
                    vlist = val.split('__sq__')
                    val = ''.join(vlist)
                while(val.find('__gt__')>-1):
                    vlist = val.split('__gt__')
                    val = '>'.join(vlist)
                while(val.find('***')>-1):
                    vlist = val.split('***')
                    val = '\n'.join(vlist)
                while(val.find('**')>-1):
                    vlist = val.split('**')
                    val = ' '.join(vlist)
                if(val != "0"): 
                    inputdict[key]= val
            
                i=i+2
            if len(inputdict) == 0:
                inputdict = {}
            print 'The operation is : ',operation    #added 

	    result = None
	    try:
		logger.info('Attempting to execute: "' + operation + '" in the webservice "' + webservice + '" with input parameters: ' + str(inputdict) )
		jar_home = str(os.environ.get('GALAXY_HOME')) + '/tools/WebServiceToolWorkflow_REST_SOAP/engine'
		jarpath = os.path.join(os.path.abspath(jar_home), '')
		jpype.startJVM(jpype.getDefaultJVMPath(), "-Djava.ext.dirs=%s" % jarpath)
		utils = jpype.JPackage("java.util")
		inputArrayList = utils.ArrayList()
		soapClient = jpype.JPackage("edu.uga.soapClient")
		jnets = jpype.JPackage("java.net")
		webserviceURL = jnets.URL(webservice)
		convertPythonToJavaParameters(inputdict)
		soapInvocation = soapClient.SoapInvocation(webserviceURL, operation, inputArrayList)
		result = soapInvocation.dynamicInvoke()
		logger.info('Execution of operation : "' + operation + '" in the webservice "' + webservice + '" was successful ')
	    except:
		 logger.error("Exception Raised: " + str(sys.exc_info()[0]))
		 logger.error("Execute WebService Operation: Script failed to execute")
		 raise

	    logger.info("Result from Execution: " + str(result))
	    resultfile.write(str(result))
    
    elif servicetype == 'REST':
            print "WADL"
