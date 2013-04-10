
## Web Service Extensions for the Galaxy Workflow Editor
## @author Michael Cotterell <mepcotterell@gmail.com>
##
## NOTE: This file is included in editor.mako via a mako include.

## The product version
WSEXTENSIONS_VERSIONS = "1.4";

## The JSONP URI endpoint for the Suggestion Engine Web Service
## @author Michael Cotterell <mepcotterell@gmail.com>
WSEXTENSIONS_SE_SERVICE_URI = "http://localhost:8084/sse/services";

## A global array of messages that we can access from the JavaScript console.
## @author Michael Cotterell <mepcotterell@gmail.com>
WSEXTENSIONS_LOG = [];

## This function logs a message to the global log array.
## @author Michael Cotterell <mepcotterell@gmail.com>
function wsextensions_log(message) {
    var msg = "LOG [" + (new Date()) + "] " + arguments.callee.caller.name + " : " + message;
    window.console && console.log(msg);
    WSEXTENSIONS_LOG.push(msg);
}

## This function logs an error to the global log array.
## @author Michael Cotterell <mepcotterell@gmail.com>
function wsextensions_error(message) {
    var caller = "SERVER SIDE";
    if (arguments.callee.caller != null) {
        var caller = arguments.callee.caller.name;
    }
    var msg = "ERROR [" + (new Date()) + "] " + caller + " : " + message;
    show_modal( "Web Service Extensions Error", msg, { "Ignore error" : hide_modal } );
    WSEXTENSIONS_LOG.push(msg);
}

## Show the log
function wsextensions_show_log() {
    var n = WSEXTENSIONS_LOG.length;
    var out = '<textarea rows="10" cols="160">';
    for (var i = 0, len = n; i < len; ++i) {
        out += WSEXTENSIONS_LOG[i] + '\n\n';
    }
    out += '</textarea>';
    show_modal( "Web Service Extensions Log", out, { "Close" : hide_modal } );
}

## Show the input documentation dialog
function wsextensions_show_documentation(node, name) {
    
    ## This is the current node.        
    ## var node = workflow.nodes[node_key];
    var wsurl  = $('input[name="url"]', $(node.form_html)).attr('value');
    var wsname = node.name.split(".")[0];
    var wsop   = node.name.split(".")[1];
    var param  = name;
    
    $.wsxDocNode  = node;
    $.wsxDocWsdl  = wsurl;
    $.wsxDocName  = wsname;
    $.wsxDocOp    = wsop;
    $.wsxDocParam = param;

    wsextensions_log("Rendering the input documentation box");
    wsextensions_log(wsurl);    

    var request = WSEXTENSIONS_SE_SERVICE_URI
    	+ "/documentationSuggestion/get/json"
        + "?wsdl="            + encodeURI(wsurl)
        + "&param="           + param 

    ## make a JSON request
    $.getJSON(request + "&callback=?", wsextensions_render_documentation);

} // wsextensions_show_documentation
$.wsextensions_show_documentation = wsextensions_show_documentation

## Handler to show suggestion type help information
$('#suggestion-type-help').click(function() {
    var out = "The Service Suggestion Engine (SSE) provides three types of service suggestion.";
    show_modal( "Help: Types of Service Suggestion", out, { "Close" : hide_modal } );
});

## Handler to show goal help information
$('#suggestion-goal-help').click(function() {
    var out = "<p>The Service Suggestion Engine (SSE) allows you to specify a goal that you would like service suggestions to fulfill. This goal can be expressed in two ways:</p> <p>test</p><p>testing</p>";
    show_modal( "Help: Service Suggestion Goals", out, { "Close" : hide_modal } );
});

## Expand and Collapse Sections
$('[id^="wsx-section"]').each(function() {
    
    var section_id = $(this).attr('id');
    
    $('[class^="wsx-toggle"]', this).click(function() {

        if ($(this).hasClass('wsx-toggle-shrink')) {
	    $(this).removeClass('wsx-toggle-shrink').addClass('wsx-toggle-expand');
	    $(this).html('[+]');
	    $('#' + section_id + '-body').hide();
	} else {
	    $(this).removeClass('wsx-toggle-expand').addClass('wsx-toggle-shrink');
	    $(this).html('[-]');
	    $('#' + section_id + '-body').show();
	} // if
			
    });
});

## handler for predecessor list
$('#suggestionEnginePredecessorList').change(function() {

    var name = $("#suggestionEnginePredecessorList option:selected").html();
    var itm = $("<li></li>")
    
    itm.attr('id', name);
    itm.html(name)
    itm.click(function() {
        itm.remove();
    });

    itm.css('background', 'url(/static/images/delete_icon.png) right center no-repeat');
    itm.css('padding-right', '20px');
    $("#wsx-pred-list ul").append(itm);

});

## handler for successor list
$('#suggestionEngineSuccessorList').change(function() {

    var name = $("#suggestionEngineSuccessorList option:selected").html();
    var itm = $("<li></li>")
    
    itm.attr('id', name);
    itm.html(name)
    itm.click(function() {
        itm.remove();
    });

    itm.css('background', 'url(/static/images/delete_icon.png) right center no-repeat');
    itm.css('padding-right', '20px');
    $("#wsx-succ-list ul").append(itm);

});

## setup the candidate list
$wsxCandidates = function() {

    var service = function(wsdl) {
    	return { 
	    "descriptionDocument": wsdl,
	    "ontologyURI": null     
	};
    };

    var operation = function (name, ws, note) {
    	return {
	    "operationName": name,
	    "service": service(ws),
	    "note": note
	};
    }; 

    ## This a simple WebServiceTool python class that we use instead of Galaxy's
    ## built-in Tool class. We do this because there seems to be, at the time of
    ## this writing, a bug in the way the tool XML files are parsed which
    ## prevents us from properly accessing a tool's input parameters.
    ##
    ## @see https://bitbucket.org/galaxy/galaxy-central/issue/589/tool-class-not-parsing-input-parameters
    ## @TODO finish documenting the python code in this snippet
    <%
        import xml.dom.minidom

        """Simple class for parsing Web Service Tool XML files."""
        class WebServiceTool:
    
            def __init__(self, config_file):
                self.config_file = config_file
                self.dom = xml.dom.minidom.parse(self.config_file)
                self.inputs = {}
                self.handleToolInputs(self.dom.getElementsByTagName("inputs")[0])

            def handleToolInputs(self, inputs):
                self.handleInputParams(inputs.getElementsByTagName("param"))

            def handleInputParams(self, params):
                for param in params:
                    self.handleInputParam(param)

            def handleInputParam(self, param):
                name = param.getAttribute('name')
                value = param.getAttribute('value')
                self.inputs[name] = value
    %>

    ## STEP 1 - Gather all the information we can about the Web Service Tools
    ##          that are both available to the current user and workflow
    ##          compliant.
    wsextensions_log("Gathering candidate operations.");

    ## The name of the Tool sections where the Web Service Tools are located.
    ## @TODO make this an array, just in case.
    var candidateOpsSections = "Select Web Service Workflow Tool";

    ## This array will hold the candidate operations.
    ## They contents of this array should each be in the form of 
    ##   <operation>@<wsdl>
    ## or
    ##   <operation>@<wsdl>@<toolid>
    ## Including the Tool ID will make it possible add a candidate operation
    ## into the workflow directly from the result list.
    var candidateOps = [];

    ## Iterate over all the Tools in the Tool Panel
    %for section_key, section in app.toolbox.tool_panel.items():

        ## Only consider the sections containing Web Service Tools
        if ("${section.name}" == candidateOpsSections) {
                
            ## Iterate over the Tools in the current section that are both
            ## workflow compatible and not hidden.
            %for tool_key, tool in section.elems.items():
                %if tool_key.startswith( 'tool' ):
                    %if not tool.hidden:
                        %if tool.is_workflow_compatible:

                            ## Parse the Web Service Tool and extract the
                            ## parameters that we need.
                            <%
                                wstool = WebServiceTool(tool.config_file)
                                wsurl = "%s" % wstool.inputs.get("url", "")
                                wsop = "%s" % wstool.inputs.get("method", "")
                                wsst = "%s" % wstool.inputs.get("servicetype", "")
                                wstoolid = tool.id
                            %>

                            ## Push this Tool into our array

			    var op = operation("${wsop}", "${wsurl}", "${wstoolid}");
			    console.log(JSON.stringify(op));
                            candidateOps.push(op);

                        %endif
                    %endif
                %endif
            %endfor

        } // if

    %endfor

    console.log(JSON.stringify(candidateOps));

    return candidateOps;

}; 

## Register handler for switching suggestion types
$('#suggestionEngineSuggestionTypeList').change(function() {
    
    var choice = "";
    
    $("#suggestionEngineSuggestionTypeList option:selected").each(function () {
        choice += $(this).val();
    });
    
    switch (choice) {

        case "forward":

            // hide other images
            $("#suggestion-backward-image").hide();
            $("#suggestion-bidirectional-image").hide();
        
            // show correct image
            $("#suggestion-forward-image").show();
        
            // reveal options
            $("#suggestion-forward").show();
            $("#suggestion-backward").hide();
            break;

        case "backward":

            // hide other images
            $("#suggestion-forward-image").hide();
            $("#suggestion-bidirectional-image").hide();
        
            // show correct image
            $("#suggestion-backward-image").show();
        
            // reveal options
            $("#suggestion-forward").hide();
            $("#suggestion-backward").show();
            break;

        case "bidirectional":

            // hide other images
            $("#suggestion-backward-image").hide();
            $("#suggestion-forward-image").hide();
        
            // show correct image
            $("#suggestion-bidirectional-image").show();
        
            // reveal options
            $("#suggestion-forward").show();
            $("#suggestion-backward").show();
            break;

    } // switch

});

function wsextensions_render_documentation(response) {

    ## The number of lines returned.
    var n = response.length;

    ## If there were no suggestions returned then raise an error.
    if (n == 0) {
       wsextensions_error("Received a response from the Suggestion Engine Web Service, but it did not contain any results");
       ## @TODO handle this more gracefully
    } // if    
    
    ## prepare output list
    var out = "<ul>";

    ## loop over suggestions
    for (var i = 0, len = n; i < len; ++i) {

        ## Prepare the result for rendering
        out += "<li>" + response[i] + "</li>";
        
    } // for

    ## finish the output list
    out += "</ul>";

    var msg = '<p>';
    msg += out;
    msg += '</p>';
    msg += '<hr class="docutils">';
    msg += '<p class="infomark">';
    msg += '<strong>Note:</strong> Putting a note here looks cool.';
    msg += '</p>';
    show_modal( "Documentation for " + $.wsxDocNode.name + " " + $.wsxDocParam, msg, { "Close" : hide_modal } );

} // wsextensions_render_documentation
$.wsextensions_render_documentation = wsextensions_render_documentation

## Show the input documentation dialog
function wsextensions_suggest_values(node, name) {
    
    ## This is the current node.        
    ## var node = workflow.nodes[node_key];
    var wsurl  = $('input[name="url"]', $(node.form_html)).attr('value');
    var wsname = node.name.split(".")[0];
    var wsop   = node.name.split(".")[1];
    var param  = name;
    
    $.wsxDocNode  = node;
    $.wsxDocWsdl  = wsurl;
    $.wsxDocName  = wsname;
    $.wsxDocOp    = wsop;
    $.wsxDocParam = param;

    wsextensions_log("Rendering the input documentation box");
    wsextensions_log(wsurl);    

    var request = WSEXTENSIONS_SE_SERVICE_URI
    	+ "/parameterValueSuggestion/get/json"
        + "?wsdl="            + encodeURI(wsurl)
        + "&param="           + param 

    ## make a JSON request
    $.getJSON(request + "&callback=?", wsextensions_render_suggest_values);

} // wsextensions_suggest_values
$.wsextensions_suggest_values = wsextensions_suggest_values

function wsextensions_render_suggest_values(response) {

    ## The number of lines returned.
    var n = response.length;

    ## If there were no suggestions returned then raise an error.
    if (n == 0) {
        wsextensions_error("Received a response from the Suggestion Engine Web Service, but it did not contain any results");
        ## @TODO handle this more gracefully
    } // if    
    
    ## prepare output list
    var out = "<ul>";

    ## loop over suggestions
    for (var i = 0, len = n; i < len; ++i) {

        ## Prepare the result for rendering
        out += "<li>" + response[i] + "</li>";
        
    } // for

    ## finish the output list
    out += "</ul>";

    var msg = '<p>';
    msg += out;
    msg += '</p>';
    msg += '<hr class="docutils">';
    msg += '';
    msg += '<strong>Note:</strong> Putting a note here looks cool.';
    msg += '</p>';
    show_modal( "Suggest input for " + $.wsxDocNode.name + " " + $.wsxDocParam, msg, { "Close" : hide_modal } );

} // wsextensions_render_suggest_values
$.wsextensions_render_suggest_values = wsextensions_render_suggest_values

## Show the about dialog
function wsextensions_show_about() {
    var msg = "<strong>Product Version:</strong> wsextensions v" + WSEXTENSIONS_VERSIONS + "<br />";
    msg += "<br />";
    msg += 'Web Service Extensions for Galaxy are based on software from the'  + "<br />";
    msg += 'University of Georgia Web Services Annotations Group, which has' + "<br />";
    msg += 'been licensed under an MIT style license.' + "<br />";
    msg += "<br />";
    msg += 'For more information, please visit ' + "<br />";
    msg += '<a href="http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/">http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/</a>.' + "<br />";
    msg += "<br />";
    msg += 'User interface for this tool implemented by ' + "<br />";
    msg += '<a href="http://michaelcotterell.com/">Michael E. Cotterell</a>.' + "<br />";
    show_modal( "About Web Service Extensions", msg, { "Close" : hide_modal } );
}

## The folowing assignments make the logging functions available globally.
## @author Michael Cotterell <mepcotterell@gmail.com>
$.wsextensions_log      = wsextensions_log;
$.wsextensions_error    = wsextensions_error;
$.wsextensions_show_log = wsextensions_show_log;

## Add the dropdown menu for WS Extensions
$("#workflow-options-button").replaceWith('<a id="workflow-suggestions-button" class="panel-header-button popup" href="#">Service Suggestion Extensions</a> <a id="workflow-options-button" class="panel-header-button popup" href="#">Options</a>');

## Add the suggestion engine popup menu to the Galaxy worflow editor.
## @author Michael Cotterell <mepcotterell@gmail.com>
make_popupmenu( $("#workflow-suggestions-button"), {
    "Suggestion Engine": wsextensions_make_se_panel,
    "About": wsextensions_show_about,
    "View Debug Log" : wsextensions_show_log
});

## Sets up the right panel in the workflow editor for use with the Suggestion
## Engine. This gets run when the suggestion engine popup menu button is 
## clicked.
## @author Michael Cotterell <mepcotterell@gmail.com>
function wsextensions_make_se_panel() {

    ## Log it
    wsextensions_log("Preparing the right panel for the Suggestion Engine interface.");    
        
    ## deselect the active node in the workflow, if any.
    workflow.clear_active_node();
            
    ## clear the right panel            
    $('.right-content').hide();

    ## the predecessor and successor selection boxes
    $('#suggestionEnginePredecessorList').empty();
    $('#suggestionEngineSuccessorList').empty();

    ## provide options to choose none
    $('#suggestionEnginePredecessorList').append($('<option></option>').val('all').html('--all--'));
    $('#suggestionEngineSuccessorList').append($('<option></option>').val('all').html('--all--'));

    ## fill the predecessor and successor selection boxes            
    for (var node_key in workflow.nodes) {
                
        ## get the current node in the iteration                
        var node = workflow.nodes[node_key];

        ## if the node is a tool, add it to the lists
        if(node.type == 'tool') {
            $('#suggestionEnginePredecessorList').append($('<option></option>').val(node.name).html("Step " + node.id + " - " + node.name));
            $('#suggestionEngineSuccessorList').append($('<option></option>').val(node.name).html("Step " + node.id + " - " + node.name));
        } // if

    } // for           

    ## Log it
    wsextensions_log("Rendering the Suggestion Engine interface in the right panel.");  

    ## show the suggestion engine div
	$('#suggestion-engine').show();

    ## register the click event for the run button            
    $("#run-se-button").click(wsextensions_se_request);

} // function wsextensions_make_se_panel()     

## Sends the information to the Suggestion Engine Web Service, parses
## the results, and renders them to the page.
## @author Michael Cotterell <mepcotterell@gmail.com>
function wsextensions_se_request() {

    // valid url function from http://stackoverflow.com/questions/5717093/check-if-a-javascript-string-is-an-url
    var validURL = function (str) {
        var pattern = new RegExp('^(https?:\\/\\/)?'+ // protocol
        '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|'+ // domain name
        '((\\d{1,3}\\.){3}\\d{1,3}))'+ // OR ip (v4) address
        '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*'+ // port and path
        '(\\?[;&a-z\\d%_.~+=-]*)?'+ // query string
        '(\\#[-a-z\\d_]*)?$','i'); // fragment locator
        if(!pattern.test(str)) {
            return false;
        } else {
            return true;
        } // if
    };

    var service = function(wsdl) {
    	return { 
	    "descriptionDocument": wsdl,
	    "ontologyURI": null     
	};
    };

    var operation = function (name, ws, note) {
    	return {
	    "operationName": name,
	    "service": service(ws),
	    "note": note
	};
    }; 

    var request = function(direction, wops, wops2, cops, df) {
    	return {
            "direction": direction,
            "desiredFunctionality": df,
            "candidates": cops,
	    "workflow": wops,
	    "workflow2": wops2
	};
    };

    ## register the click event for the run button            
    $("#run-se-button").click(wsextensions_se_request);

    $('[class^=wsx-toggle]', $('#wsx-section-options')).removeClass('wsx-toggle-shrink').addClass('wsx-toggle-expand');
    $('[class^=wsx-toggle]', $('#wsx-section-options')).html('[+]');
    $('#wsx-section-options-body').hide();

    $('[class^=wsx-toggle]', $('#wsx-section-results')).removeClass('wsx-toggle-expand').addClass('wsx-toggle-shrink');
    $('[class^=wsx-toggle]', $('#wsx-section-options')).html('[-]');
    $('#wsx-section-results-body').show();

    ## Unhide the results section
    $('#suggestion-engine-results-frame').show();
    $("#suggestion-engine-results").show();

    ## Display the progress bar.
    $("#suggestion-engine-results-progress").show();

    ## Log it
    wsextensions_log("Preparing to make a request to the Suggestion Engine Web Service.");

    ## Grab candidate operations
    var candidateOps = $wsxCandidates();

    ## Did we find any candidate operations? If not, let us register an error.
    if (candidateOps.length == 0) {
        wsextensions_error("Could not find any candidate operations.");
    } // if

    ## STEP 2 - Gather information about the current state of the workflow. 
    ##
    ##          @FIXME The current implementation does not distinguish between
    ##                 regular Tools and Web Services. For now, we assume that
    ##                 the workflow is only composed of Web Service Tools. This
    ##                 obviously needs to be fixed before release. 
    wsextensions_log("Gathering current workflow operations.");

    ## This array will hold the workflow operations.
    ## They contents of this array should each be in the form of 
    ##   <operation>@<wsdl>
    ## or
    ##   <operation>@<wsdl>@<toolid>
    ## Including the Tool ID here is not really needed. However, I chose to
    ## provide this option so that it is consistent with the candidateOps array.
    var workflowOps = [];
    var workflowOps2 = [];  

    ## Iterate over the nodes in the current workflow.
    for (var node_key in workflow.nodes) {
    
        ## This is the current node.        
        var node = workflow.nodes[node_key];

        ## Consider only nodes that are Tools.
        ## @FIXME need to only consider Web Service Tools
        if(node.type == 'tool') {

            ## Get the operation name.
            ## Web Service Tool nodes have names in the form 
            ## of <service>.<operation>
            var wsop = node.name.split(" ")[1];

            ## Get the operation's WSDL URL
            ## @EPIC Uses sexy jQuery magic.
            var wsurl = $('input[name="url"]', $(node.form_html)).attr('value');

            ## Get the Web Service Tool's tool_id
            var wstoolid = node.tool_id;

	    if (validURL(wsurl)) {

                ## create operation
                var op = operation(wsop, wsurl, wstoolid);

                ## is it in the pred list?
		$("#wsx-pred-list ul").find('li').each(function(){
                    var current = $(this);
                    if (current.text().indexOf(wsop) !== -1) {
                        ## Push it into our array
	    	        workflowOps.push(op)
            	        console.log(op);
                    } // if
                });

                ## is it in the succ list?
                $("#wsx-succ-list ul").find('li').each(function(){
                    var current = $(this);
                    if (current.text().indexOf(wsop) !== -1) {
                        ## Push it into our array
	    	        workflowOps2.push(op)
            	        console.log(op);
                    } // if
                });

	    } // if

        } // if

    } // for

    ## Did we find any operations in the current workflow? If not, let us
    ## register an error.
    ##if (workflowOps.length == 0) {
    ##    wsextensions_error("Could not find any operations in the current workflow.");
    ##} // if

    ## STEP 3 - Gather all the other information from the form
    wsextensions_log("Gathering information from the form in the Suggestion Engine interface.");

    ## The desired functionality. Either some string similar to an operation
    ## or a URI to some concept in an ontology.
    var desiredFunctionality = $('#suggestionEngineDesired').attr('value');

    var direction = "";
    
    $("#suggestionEngineSuggestionTypeList option:selected").each(function () {
        direction = $(this).val();
    });

    ## create the request
    var payload = request(direction, workflowOps, workflowOps2, candidateOps, desiredFunctionality);
    wsextensions_log('Generated the SSE-WS request payload: ' + JSON.stringify(payload))

    var jsonURI = "http://172.16.140.135:8084/SSE-WS/services"
        + "/serviceSuggestion/get/jsonp"
        + "?payload=" + JSON.stringify(payload);

    wsextensions_log('Using the following URI (jQuery will add &callback=?): ' + jsonURI)
    
    ## make a JSON request
    $.getJSON(jsonURI + "&callback=?", wsextensions_se_parse_response);

} // function wsextensions_se_request

## Parses the response from the Suggestion Engine Web Service and renders the
## results to the Suggestion Engine interface within the workflow editor. This
## function is only called if the jQuery JSON request was successful.
## @author Michael Cotterell <mepcotterell@gmail.com>
function wsextensions_se_parse_response(suggestions) {            

    ## Log it
    wsextensions_log('Processing the SSE-WS response payload: ' + JSON.stringify(suggestions));

    ## The number of suggested operation returned.
    var n = suggestions.operations.length;

    ## If there were no suggestions returned then raise an error.
    if (n == 0) {
        wsextensions_error("Received a response from the Suggestion Engine Web Service, but it did not contain any results");
        ## @TODO handle this more gracefully
    } // if    
    
    $.wsxSuggestions = suggestions

    ## prepare output list
    var out = '<ul style="list-style-type: square;">';

    ## loop over suggestions
    for (var i = 0, len = n; i < len; ++i) {

        ## Each suggestion is an array of the following:
        ## 0: Operation Name
        ## 1: WSDL URL
        ## 2: Rank Score
        ## 3: Unweighted Data Mediation Sub-score
        ## 4: Unweighted Functionality Sub-score
        ## 5: Unweighted Preconditions / Effects Sub-score
        ## 6: Galaxy tool_id

        ## The operation name.         
        var op = suggestions.operations[i].operationName;

        ## The operation's WSDL URL.
        var wsdl = suggestions.operations[i].service.descriptionDocument;

        ## The operation's rank score.
        var rank = suggestions.operations[i].score;

        ## The operation's data mediation sub-score.
        var dm = suggestions.operations[i].dataMediationScore;

        ## The operation's functionality sub-score.
        var fn = suggestions.operations[i].functionalityScore;

        ## The operation's preconditons/effects sub-score.
        var pe = suggestions.operations[i].preconditionEffectScore;

        ## The short name of the wsdl, derived from the WSDL URL
        var short_wsdl = wsdl.substring(wsdl.lastIndexOf('/') + 1);

        ## The web service name, derived from the short wsdl name
        var service = short_wsdl.substring(0, short_wsdl.lastIndexOf('.'));

        ## The link which allows one to add the operation to the current workflow
        var link = '';

        ## Check to see if a galaxy tool_id was received. If so, this implies that
        ## the user has the tool already available to them in the workflow editor.
        if (suggestions.operations[i].note != null) {

            ## The galaxy tool id
            var tool_id = suggestions.operations[i].note;

            ## Generate the add link
            link = '<a href="#" onclick="add_node_for_tool( \'' + tool_id + '\', \'' + service + ' ' + op + '\' )">' + service + ' ' + op + '</a>';

        } else {

            ## Otherwise, let them know that they add this tool using Radiant Web.
            link = service + ' ' + op;

        }

        ## Prepare the result for rendering
        out += "<li>";
        out += link + "<br />";
        ## out += "<a href=\"" + wsdl + "\" style=\"color:#FF66FF;\"><span style=\"color:#FF66FF;\"><small>" + short_wsdl + "</small></span></a>" + "<br />";
        ## out += "<span style=\"color:#999999;\"><small>" + rank + " (DM: " + dm + ", FN: " + fn + ")</small></span>" + "<br />";
        out += "<span style=\"color:#999999;\"><small>" + rank + "</small></span>" + "<br />";
        out += "<br />";
        out += "</li>";
        
    } // for

    ## finish the output list
    out += "</ul>";

    ## Log it
    wsextensions_log("Response parsed, rendering results to the Suggestion Engine interface.");

    ## Hide the progress bar.
    $("#suggestion-engine-results-progress").hide();

    ## display the results
    $("#suggestion-engine-results-content").replaceWith('<div id="suggestion-engine-results-content">' + out + '</div>');

} // function wsextensions_se_parse_response



