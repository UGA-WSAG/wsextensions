'''
@author  Akshay Choche, Chaitanya Guttula
@see     LICENSE (MIT style license file).
'''
import jpype
import logging
import warnings
import os
import sys
import platform
with warnings.catch_warnings():
    warnings.simplefilter("ignore")
    import ZSI
    import commands
    import urllib2
    
    '''
    Takes the description document as input and parses the document and 
    gets all the operations available in the web service and provides it 
    Galaxy
    
    '''
    '''
    This code is added to extend the logging functionality to the tool

    '''
    logger = logging.getLogger('myapp')
    logger_home = str(os.environ.get('GALAXY_HOME')) + '/tools/WebServiceToolWorkflow_REST_SOAP/Logs/Adding_Web_Service_As_Tool/step_a.log'
    hdlr = logging.FileHandler(logger_home)
    formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    hdlr.setFormatter(formatter)
    logger.addHandler(hdlr) 
    logger.setLevel(logging.INFO)

    logger.info("Executing the script WebServiceTool_input_url_m.py")
    logger.info("Adding a new Web Service to Galaxy")
    url = sys.argv[1]
    logger.info("URL of Web Service Description is " + url)
   
    #Housekeeping tasks 
    while(url.find('__tilda__')>-1):
        ulist = url.split('__tilda__')
        url = '~'.join(ulist)            
    urllist = url.split('/')
    wsdlname = urllist[len(urllist)-1].split(".")
    if len(wsdlname)==1:
        wsdlname =urllist[len(urllist)-1].split('?')
    
    servicetype = ''
    logger.info("The Web Service Description name is " + str(wsdlname))
  
    try:
	logger.info("Attempting to open the following url: " + url)
        u = urllib2.urlopen(url)
    except urllib2.HTTPError, e:
	logger.error("Error executing the script WebServiceTool_input_url_m.py")
        print e.code
        print e.msg
        print e.headers
        print e.fp.read()

    descfile = open('temp','w')
    urlDataRead = u.read()
    descfile.write(urlDataRead)
    logger.info("Creating a local copy of the web service description file: ")
    descfile.close()
    readwsdl = open('temp','r')
    tempstring = 'start'
    
    #checks the description document (WSDL/WADL) is for SOAP 1.1 or SOAP 2.0 or REST and gets the operations in the web service accordingly   
    while (tempstring != ''):
        logger.info("Checking to see is the supplied file is WSDL or WADL")
        tempstring = readwsdl.readline()
	logger.info("The first line read: " + tempstring)
         #If its WSDL 1.0/1.1
        if tempstring.find('<definitions') != -1 or tempstring.find('<wsdl:definitions') != -1:
            logger.info("Found a SOAP service with WSDL 1.0/1.1")
	    jar_home = str(os.environ.get('GALAXY_HOME')) + '/tools/WebServiceToolWorkflow_REST_SOAP/engine'
	    jarpath = os.path.join(os.path.abspath(jar_home), '')
	    jpype.startJVM(jpype.getDefaultJVMPath(), "-Djava.ext.dirs=%s" % jarpath)
	    parserWSDLPackage = jpype.JPackage("edu.uga.WSExtension.AddingWebServiceAsTools")
            parser = parserWSDLPackage.ParsingWSDLForMethods()
	    resultFile = open(sys.argv[2],"w")
	    resultFile.write(parser.getMethods(url))
	    resultFile.close()
            break
         #if it is WSDL 2.0
        elif tempstring.find('<description') != -1 or tempstring.find('<wsdl:description') != -1:
            #servicetype = 'WSDL2.0'
            #break
            while tempstring != '':
                if tempstring.find('<bindings ') != -1 or tempstring.find('<wsdl:bindings') != -1:
                    bind = tempstring.split('type')
                    bindLength = len(bind)
                    bindingtype = bind[bindLength-1]
                    break
                tempstring = readwsdl.readline()
               #IF the WSDL describes SOAP web services
                if bindingtype.find('http://www.w3.org/ns/wsdl/soap') != -1:    
		    logger.info("Format not supported by the system work in progress")
                    print 'WSDL 1.1'
               #If the WSDL describes REST web services
                else:
		    logger.info("Found a REST service described using WSDL:\n Calling the getWSDLRESTMethods from the getMethods_m.py")
		    print "REST using WSDL"
                break
        elif tempstring.find('<application') != -1 or tempstring.find('<APPLICATION') != -1:
        	#servicetype = 'REST'
		logger.info("Found a REST service:\n Calling the getWADLMethods from the getMethods_m.py")
        	print "WADL"
        	break              
    readwsdl.close()
