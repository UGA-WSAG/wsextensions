## Web Service Extensions for the Galaxy Workflow Editor
## @author Michael E. Cotterell <mepcotterell@gmail.com>
##
## NOTE: This file is included in editor.mako via a mako include.

<%
    wsextensions_product = "Service Suggestion Engine (SSE) Extensions"
    wsextensions_version = "1.5"
    wsextensions_enpoint = "http://wsannotations.ctegd.uga.edu/SSE-WS/services"
%>

$.wsextensions = {};
$.wsextensions.logbuffer = [];

$.wsextensions.props = {
    'product':  '${wsextensions_product}', 
    'version':  '${wsextensions_version}',
    'endpoint': '${wsextensions_enpoint}',
}; // $.wsextensions.props

$.wsextensions.models = {

    'service': function(wsdl) {
    	return { 
	    "descriptionDocument": wsdl,
	    "ontologyURI": null     
	};
    },

    'operation': function (name, ws, note) {
    	return {
	    "operationName": name,
	    "service": $.wsextensions.models.service(ws),
	    "note": note
	};
    },

    'request': function(direction, wops, wops2, cops, df) {
    	return {
            "direction": direction,
            "desiredFunctionality": df,
            "candidates": cops,
	    "workflow": wops,
	    "workflow2": wops2
	};
    },

}; // $.wsextensions.models

$.wsextensions.doc = {
    'about':            function() {/* <%include file="doc/about.html"/> */},
    'issues':           function() {/* <%include file="doc/issues.html"/> */},
    'goal':             function() {/* <%include file="doc/goal.html"/> */},
    'results':          function() {/* <%include file="doc/results.html"/> */},
    'suggestion_types': function() {/* <%include file="doc/suggestion_types.html"/> */},
    'footer':           function() {/* <%include file="doc/footer.html"/> */},
}; // $.wsextensions.doc

## This function logs a message to the logbuffer
$.wsextensions.log = function(message) {
    var msg = "[" + (new Date()) + "] " + arguments.callee.caller.name + " : " + message;
    window.console && console.log(msg);
    $.wsextensions.logbuffer.push(msg);
} // $.wsextensions.log

## This function logs an error to the logbuffer
$.wsextensions.error = function(message) {
    var caller = "SERVER SIDE";
    if (arguments.callee.caller != null) {
        caller = arguments.callee.caller.name;
    } // if
    var msg = "ERROR [" + (new Date()) + "] " + caller + " : " + message;
    window.console && console.log(msg);
    $.wsextensions.logbuffer.push(msg);
    show_modal( "Web Service Extensions Error", msg, { "Ignore Error" : hide_modal } );
} // $.wsextensions.error

## Show the log
$.wsextensions.showLogDialog = function() {
    var n = $.wsextensions.logbuffer.length;
    var out = '<textarea rows="10" cols="80">';
    for (var i = 0, len = n; i < len; ++i) {
        out += $.wsextensions.logbuffer[i] + '\n';
    } // for
    out += '</textarea>';
    show_modal( "Web Service Extensions Log", out, { "Close" : hide_modal } );
} // wsextensions_show_log

$.wsextensions.renderDoc = function(doc_func) {
    var out = doc_func.toString();  // get the function as a string
    var beg = out.indexOf('*');     // determine beginning of comment
    var end = out.lastIndexOf('*'); // determine end of comment  
    if (beg == -1 || end == -1) {
        $.wsextensions_error('Could not render documentation string: ' + out);
    } // if
    return out.substring(beg + 1, end - 1);
} // $.wsextensions_doc_render

## Handles AJAX Timeouts
$.wsextensions.getJSON = function(request, func, timeout) {

    var start = Date.now();

    var id = 'JSONP (' + start + ') ';

    var timerCheck = function() {
        $.wsextensions.error(id + 'request timed out');
	clearInterval(timer);
    } // timerCheck

    var timer = setInterval(timerCheck, timeout); 

    var responseCheck = function(response) {
	var now = Date.now();
        $.wsextensions.log(id + 'response returned in ' + (now - start) + ' ms');
	$.wsextensions.log(id + 'response: ' + JSON.stringify(response));
	if (now < start + timeout) {    
            clearInterval(timer);
	    func(response);
        } else {
	    $.wsextensions.log(id + 'response returned after timeout period');
        } // if
    } // check

    $.wsextensions.log(id + 'request: ' + request);
    $.getJSON(request, responseCheck);

} // $.wsextensions.getJSON

## Show the about dialog
$.wsextensions.showAboutDialog = function() {
    var out = '';
    out += $.wsextensions.renderDoc($.wsextensions.doc.about);
    out += $.wsextensions.renderDoc($.wsextensions.doc.footer);
    show_modal( "About Service Suggestion Engine (SSE) Extensions", out, { "Close" : hide_modal } );
} // $.wsextensions.showAboutDialog

## Show the issues dialog
$.wsextensions.showIssuesDialog = function() {
    var out = '';
    var n   = $.wsextensions.logbuffer.length;
    out += $.wsextensions.renderDoc($.wsextensions.doc.issues);
    out += '<textarea rows="10" cols="80">';
    for (var i = 0, len = n; i < len; ++i) {
        out += $.wsextensions.logbuffer[i] + '\n';
    } // for
    out += '</textarea>';
    out += $.wsextensions.renderDoc($.wsextensions.doc.footer);
    show_modal( "Report Issues about Sevice Suggestion Engine (SSE) Extensions", out, { "Close" : hide_modal } );
} // $.wsextensions.showIssuesDialog

## Handler to show suggestion type help information
$('#suggestion-type-help').click(function() {
    var out = '';
    out += $.wsextensions.renderDoc($.wsextensions.doc.suggestion_types);
    out += $.wsextensions.renderDoc($.wsextensions.doc.footer);
    show_modal( "Help: Types of Service Suggestion", out, { "Close" : hide_modal } );
});

## Handler to show goal help information
$('#suggestion-goal-help').click(function() {
    var out = '';
    out += $.wsextensions.renderDoc($.wsextensions.doc.goal);
    out += $.wsextensions.renderDoc($.wsextensions.doc.footer);
    show_modal( "Help: Service Suggestion Goals", out, { "Close" : hide_modal } );
});

## Handler to show result help information
$('#suggestion-result-help').click(function() {
    var out = '';
    out += $.wsextensions.renderDoc($.wsextensions.doc.results);
    out += $.wsextensions.renderDoc($.wsextensions.doc.footer);
    show_modal( "Help: Ranked Suggestion Results", out, { "Close" : hide_modal } );
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
    itm.html(name);
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
$.wsextensions.getCandidateOps = function() {

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
    $.wsextensions.log("Gathering candidate operations.");

    ## The name of the Tool sections where the Web Service Tools are located.
    ## @TODO make this an array, just in case.
    var candidateOpsSections = "Select Web Service Workflow Tool";

    ## This array will hold the candidate operations.
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
			    var op = $.wsextensions.models.operation("${wsop}", "${wsurl}", "${wstoolid}");
                            candidateOps.push(op);

                        %endif
                    %endif
                %endif
            %endfor

        } // if

    %endfor

    return candidateOps;

}; // $.wsextensions.getCandidateOps

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

## Show the input documentation dialog
$.wsextensions.showInputDocDialog = function(node, name) {

    var url     = $('input[name="url"]', $(node.form_html)).attr('value');
    var wsname  = node.name.split(" ")[0];
    var op      = node.name.split(" ")[1];
    var param   = name;
    var request = $.wsextensions.props.endpoint
    	+ "/documentationSuggestion/get/json"
        + "?wsdl="            + encodeURI(url)
        + "&param="           + param 

    ## make a JSON request
    $.wsextensions.getJSON(request + "&callback=?", $.wsextensions.renderInputDocResponse, 30 * 1000);

    ## message to show
    var msg = '<div id="wsx-suggest-doc-response"><img src="/static/images/yui/rel_interstitial_loading.gif" /></div>';
    
    ## show the modal
    show_modal( 'Documentation for "' + op + " " + param + '"', msg, { "Close" : hide_modal } );

} // $.wsextensions_show_documentation

## Render Documentation retrieved from SSE
$.wsextensions.renderInputDocResponse = function(response) {

    ## The number of lines returned.
    var n = response.length;

    ## If there were no suggestions returned then raise an error.
    if (n == 0) {

        ## wsextensions_error("Received a response from the Suggestion Engine Web Service, but it did not contain any results");
        var msg = '<em>No documentation found.</em>';
        $('#wsx-suggest-doc-response').html(msg);

    } else {    
    
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

        $('#wsx-suggest-doc-response').html(msg);

    } // if 

} // $.wsextensions.renderInputDocResponse

## Show the input documentation dialog
$.wsextensions.showInputSuggestDialog = function(node, name) {
    
    ## This is the current node.        
    ## var node = workflow.nodes[node_key];
    var wsurl  = $('input[name="url"]', $(node.form_html)).attr('value');
    var wsname  = node.name.split(" ")[0];
    var wsop    = node.name.split(" ")[1];
    var param   = name;
    var request = $.wsextensions.props.endpoint
    	+ "/parameterValueSuggestion/get/json"
        + "?wsdl="            + encodeURI(wsurl)
        + "&param="           + param 

    ## make a JSON request
    $.wsextensions.getJSON(request + "&callback=?", $.wsextensions.renderInputSuggestResponse, 5 * 1000);

    ## message to show
    var msg = '<div id="wsx-suggest-values-response"><img src="/static/images/yui/rel_interstitial_loading.gif" /></div>';
    
    ## show the modal
    show_modal( 'Suggested Input Values for "' + wsop + ' ' + param + '"', msg, { "Close" : hide_modal } );

} // $.wsextensions.showInputSuggestDialog

## Render suggested input values returned from SSE
$.wsextensions.renderInputSuggestResponse = function(response) {

    ## The number of lines returned.
    var n = response.length;

    ## If there were no suggestions returned then raise an error.
    if (n == 0) {

        ## wsextensions_error("Received a response from the Suggestion Engine Web Service, but it did not contain any results");
        var msg = '<em>No input value suggestions found.</em>';
        $('#wsx-suggest-values-response').html(msg);

    } else {    

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
      
        $('#wsx-suggest-values-response').html(msg);

    } // if

} // wsextensions_render_suggest_values

## Sets up the right panel in the workflow editor for use with the Suggestion
## Engine. This gets run when the suggestion engine popup menu button is 
## clicked.
$.wsextensions.showMainPanel = function() {

    ## Log it
    $.wsextensions.log("Rendering SSE Panel");    
        
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

    ## show the suggestion engine div
    $('#suggestion-engine').show();

    ## register the click event for the run button            
    $("#run-se-button").click($.wsextensions.querySSE);

} // $.wsextensions.showMainPanel    

## Sends the information to the Suggestion Engine Web Service, parses
## the results, and renders them to the page.
$.wsextensions.querySSE = function() {

    ## register the click event for the run button            
    //$("#run-se-button").click(wsextensions_se_request);

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

    ## Log it
    $.wsextensions.log("Preparing to query SSE.");

    ## Grab candidate operations
    var candidateOps = $.wsextensions.getCandidateOps();

    ## Did we find any candidate operations? If not, let us register an error.
    if (candidateOps.length == 0) {
        $.wsextensions.error("Could not find any candidate operations.");
    } // if

    ## STEP 2 - Gather information about the current state of the workflow. 
    $.wsextensions.log("Gathering current workflow operations.");

    ## These arrays will hold the workflow operations.
    var workflowOps1 = [];
    var workflowOps2 = [];
    var allOps = []; 

    ## Iterate over the nodes in the current workflow.
    for (var node_key in workflow.nodes) {
    
        ## This is the current node.        
        var node = workflow.nodes[node_key];

        ## Consider only nodes that are Tools.
        ## @FIXME need to only consider Web Service Tools
        if(node.type == 'tool') {

            ## Get the operation name.
            var wsop = node.name.split(" ")[1];

            ## Get the operation's WSDL URL
            ## @EPIC Uses sexy jQuery magic.
            var wsurl = $('input[name="url"]', $(node.form_html)).attr('value');

            ## Get the Web Service Tool's tool_id
            var wstoolid = node.tool_id;

	    if (validURL(wsurl)) {

                ## create operation
                var op = $.wsextensions.models.operation(wsop, wsurl, wstoolid);

                ## add it to allOps
                allOps.push(op);
                
                ## is it in the pred list?
		$("#wsx-pred-list ul").find('li').each(function(){
                    var current = $(this);
                    if (current.text().indexOf(wsop) != -1) {
	    	        workflowOps1.push(op)
                    } // if
                });

                ## is it in the succ list?
                $("#wsx-succ-list ul").find('li').each(function(){
                    var current = $(this);
                    if (current.text().indexOf(wsop) != -1) {
	    	        workflowOps2.push(op)
                    } // if
                });

	    } // if

        } // if

    } // for

    ## Did we find any operations in the current workflow? If not, let us
    ## register an error.
    if (allOps.length == 0) {

        var msg = 'In order to use the Service Suggestion Engine (SSE), at least one tool needs to be in the current Workflow Canvas. ';
        $.wsextensions.log(msg);
        show_modal('Warning - No Tools on Workflow Canvas', msg, { "Close" : hide_modal } );

    } else {

        ## If workflowOps1 or workflowOps2 are empty, the user chose "--all--"
        if (workflowOps1.length == 0) workflowOps1 = allOps;
        if (workflowOps2.length == 0) workflowOps2 = allOps;

	## Collapse the "Setup & Run Query" section
        $('[class^=wsx-toggle]', $('#wsx-section-options')).removeClass('wsx-toggle-shrink').addClass('wsx-toggle-expand');
        $('[class^=wsx-toggle]', $('#wsx-section-options')).html('[+]');
        $('#wsx-section-options-body').hide();

	## Expand the "Query Results" section
        $('[class^=wsx-toggle]', $('#wsx-section-results')).removeClass('wsx-toggle-expand').addClass('wsx-toggle-shrink');
        $('[class^=wsx-toggle]', $('#wsx-section-options')).html('[-]');
        $('#wsx-section-results-body').show();

        ## Unhide the results frame
        $('#suggestion-engine-results-frame').show();
        $("#suggestion-engine-results").show();

        ## Display the progress bar.
        $("#suggestion-engine-results-progress").show();

        ## STEP 3 - Gather all the other information from the form
        $.wsextensions.log("Gathering information from the form.");

        ## The desired functionality. Either some string similar to an operation
        ## or a URI to some concept in an ontology.
        var desiredFunctionality = $('#suggestionEngineDesired').attr('value');

	## Determine the suggestion direction
        var direction = "";
        $("#suggestionEngineSuggestionTypeList option:selected").each(function () {
            direction = $(this).val();
        });

        ## create the request
        var payload = $.wsextensions.models.request(direction, workflowOps1, workflowOps2, candidateOps, desiredFunctionality);
        var jsonURI = $.wsextensions.props.endpoint
            + "/serviceSuggestion/get/jsonp"
            + "?payload=" + JSON.stringify(payload);
    
        ## make a JSON request
        $.wsextensions.getJSON(jsonURI + "&callback=?", $.wsextensions.renderQueryResponse, 30 * 1000);

    } // if

} // $.wsextensions.querySSE

## Parses the response from the Suggestion Engine Web Service and renders the
## results to the Suggestion Engine interface within the workflow editor. This
$.wsextensions.renderQueryResponse = function(suggestions) {            

    ## Log it
    $.wsextensions.log('Processing the SSE-WS response payload: ' + JSON.stringify(suggestions));

    ## The number of suggested operation returned.
    var n = suggestions.operations.length;

    ## The default number of results to show
    var c = 5;
    
    ## Update counts
    $('#wsx-result-count-info').show();
    $('#wsx-result-count-show').text(c);
    $('#wsx-result-count').text(n);
    
    ## if there are more than 5 results, then display "Show All"
    if (n > c) {
        $('#wsx-result-more').show();         
        $('#wsx-result-more-button').click(function() {
            $('#wsx-result-list').find('li').each(function(){
                var current = $(this);
                current.show();
            });
            $('#wsx-result-count-show').text(n);
            $('#wsx-result-more').hide();
        });
    } else {
        $('#wsx-result-more').hide();
    } // if

    ## If there were no suggestions returned then raise an error.
    if (n == 0) {
        $.wsextensions.error("Received a response from the Suggestion Engine Web Service, but it did not contain any results.");
	return true;
        ## @TODO handle this more gracefully
    } // if    
   
    ## prepare output list
    var out = '<ul id="wsx-result-list" style="list-style-type: square;">';

    ## loop over suggestions
    for (var i = 0, len = n; i < len; ++i) {

        var op         = suggestions.operations[i].operationName;
        var wsdl       = suggestions.operations[i].service.descriptionDocument;
        var rank       = suggestions.operations[i].score;
        var dm         = suggestions.operations[i].dataMediationScore;
        var fn         = suggestions.operations[i].functionalityScore;
        var pe         = suggestions.operations[i].preconditionEffectScore;
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

        } // if

        ## Prepare the result for rendering
        if (i < 5) {
            out += '<li>';
        } else {
            out += '<li style="display:none;">';
        } // if

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
    $.wsextensions.log("Response parsed. Rendering results in the SSE Panel.");

    ## Hide the progress bar.
    $("#suggestion-engine-results-progress").hide();

    ## display the results
    $("#suggestion-engine-results-content").replaceWith('<div id="suggestion-engine-results-content">' + out + '</div>');

} // $.wsextensions.renderQueryResponse

## Add the dropdown menu for WS Extensions
$("#workflow-options-button").replaceWith('<a id="workflow-suggestions-button" class="panel-header-button popup" href="#">Service Suggestion Extensions</a> <a id="workflow-options-button" class="panel-header-button popup" href="#">Options</a>');

## Add the suggestion engine popup menu to the Galaxy worflow editor.
make_popupmenu( $("#workflow-suggestions-button"), {
    "Suggestion Engine": $.wsextensions.showMainPanel,
    "Report Issues":     $.wsextensions.showIssuesDialog,
    "About":             $.wsextensions.showAboutDialog,
    "View Debug Log":    $.wsextensions.showLogDialog
});


