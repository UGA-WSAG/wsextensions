
## Web Service Extensions for the Galaxy Workflow Editor
## @author Michael Cotterell <mepcotterell@gmail.com>
##
## NOTE: This file is included in editor.mako via a mako include.

## The product version
WSEXTENSIONS_VERSIONS = "1.5";

## The JSONP URI endpoint for the Suggestion Engine Web Service
## @author Michael Cotterell <mepcotterell@gmail.com>
WSEXTENSIONS_SE_SERVICE_URI = "http://172.16.140.137:8084/SSE-WS/services";

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

    ## message to show
    var msg = '<div id="wsx-suggest-doc-response"><img src="/static/images/yui/rel_interstitial_loading.gif" /></div>';
    
    ## show the modal
    show_modal( "Documentation for " + $.wsxDocNode.name + " " + $.wsxDocParam, msg, { "Close" : hide_modal } );

} // wsextensions_show_documentation
$.wsextensions_show_documentation = wsextensions_show_documentation

## Handler to show suggestion type help information
$('#suggestion-type-help').click(function() {
    
    var out = '';

    out += '<p>';
    out += 'The Service Suggestion Engine (SSE) provides three types of service suggestion.';
    out += '</p>';
	
    out += '<p>';
    out += 'In order to use the SSE, at least one tool needs to be on the current Workflow Canvas. ';
    out += 'Tools in the workflow are added to various lists by the user depending on the type of suggestion chosen and the roles these tools should take. ';
    out += 'The sections below provide information about the three different types of suggestion and their corresponding lists. ';
    out += '</p>';

    out += '<h4>';
    out += 'Forward Suggestion';
    out += '<img style="max-height: 45px; padding-left: 1em;" alt="type" src="http://i.imgur.com/ex27zRI.png" align="right">';
    out += '</h4>';

    out += '<p>';
    out += 'When a "Forward Suggestion" is chosen, the SSE will suggest a list of available tools, ranked according to how well they match some desired functionality and their compatibility with the <em>outputs</em> of tools chosen from the Workflow Canvas. ';
    out += 'The user chooses these <strong>Tools to Feed From</strong> by selecting them from the corresponding drop-down menu that appears in the "Setup & Run Query" panel to the right of the Workflow Canvas. ';
    out += 'In the image to the right, the empty box represents the <em>Tools to feed From</em> and the question mark (?) box represents tools that the user would like suggested. ';
    out += '</p>';

    out += '<h4>';
    out += 'Backward Suggestion';
    out += '<img  style="max-height: 45px; padding-left: 1em;" alt="type" src="http://i.imgur.com/XoD0WCw.png" align="right">';
    out += '</h4>';

    out += '<p>';
    out += 'When a "Backward Suggestion" is chosen, the SSE will suggest a list of available tools, ranked according to how well they match some desired functionality and their compatibility with the <em>inputs</em> of tools chosen from the Workflow Canvas. ';
    out += 'The user chooses these <strong>Tools to Feed Into</strong> by selecting them from the corresponding drop-down menu that appears in the "Setup & Run Query" panel to the right of the Workflow Canvas. ';
    out += 'In the image to the right, the empty box represents the <em>Tools to feed Into</em> and the question mark (?) box represents tools that the user would like suggested. ';
    out += '</p>';

    out += '<h4>';
    out += 'Bidirectional Suggestion';
    out += '<img  style="max-height: 45px; padding-left: 1em;" alt="type" src="http://i.imgur.com/MfTWkIm.png" align="right">';
    out += '</h4>';

    out += '<p>';
    out += 'When a "Bidirectional Suggestion" is chosen, the SSE will suggest a list of available tools, ranked according to how well they match some desired functionality and their compatibility with the <em>inputs</em> and <em>outputs</em> of certain tools chosen from the Workflow Canvas. ';
    out += 'The user chooses <strong>Tools to Feed From</strong> and <strong>Tools to Feed Into</strong> by selecting them from the corresponding drop-down menus that appears in the "Setup & Run Query" panel to the right of the Workflow Canvas. ';
    out += 'In the image to the right, the left box represents the <em>Tools to feed From</em>, the right box represents the <em>Tools to feed Into</em>, and the question mark (?) box represents tools that the user would like suggested. ';
    out += '</p>';

    out += '<hr />';

    out += '<p>';
    out += '<small style="float: left;">';
    out += 'Last Updated: 2013-04-16';
    out += '</small>';
    out += '<small style="float: right;">';
    out += '<strong>Need more information?</strong> ';
    out += 'Click <a href="http://mango.ctegd.uga.edu/jkissingLab/SWS/sse.html">here</a> to see more documentation.';
    out += '</small>';
    out += '</p>';

    show_modal( "Help: Types of Service Suggestion", out, { "Close" : hide_modal } );
});

## Handler to show goal help information
$('#suggestion-goal-help').click(function() {
    var out = '';
    
    out += '<p>';
    out += 'The Service Suggestion Engine (SSE) allows you to specify a goal that suggested tools should fulfill. ';
    out += 'This goal can be expressed in two ways: ';
    out += '</p>';

    out += '<ol>';

    out += '<li style="padding-bottom: 1em;">';
    out += 'A simple description of what you are trying to do/find (e.g., "multiple sequence alignment" or "phylogentic protein distance"). ';
    out += '</li>';

    out += '<li>';
    out += 'An IRI or label of a known term in an ontology (e.g., "compute protein evolution distance objective" or "http://purl.obolibrary.org/obo/OBIws_0000180"). ';
    out += '</li>';

    out += '</ol>';

    out += '<hr />';

    out += '<p>';
    out += '<small style="float: left;">';
    out += 'Last Updated: 2013-04-16';
    out += '</small>';
    out += '<small style="float: right;">';
    out += '<strong>Need more information?</strong> ';
    out += 'Click <a href="http://mango.ctegd.uga.edu/jkissingLab/SWS/sse.html">here</a> to see more documentation.';
    out += '</small>';
    out += '</p>';

    show_modal( "Help: Service Suggestion Goals", out, { "Close" : hide_modal } );
});

## Handler to show goal help information
$('#suggestion-result-help').click(function() {
    var out = '';
    
    out += '<p>';
    out += 'The Service Suggestion Engine (SSE) provides tool suggestions to the user as a ranked list, scored in descending order from 1.0 (best) to 0 (worst). ';
    out += 'The implementations of the algorithms used by the SSE expands upon our previous work [1]. ';

    out += '<h4>Inputs and Scores</h4>';

    out += 'With respect to Galaxy, the SEE considers four things in order to provide suggestions: ';
    out += '</p>';

    out += '<ol>';

    out += '<li style="padding-bottom: 1em;">';
    out += '<strong>Direction:</strong> Helps the user specify where he or she would like to place the suggested tools in the workflow; ';
    out += '</li>';

    out += '<li style="padding-bottom: 1em;">';
    out += '<strong>Workflow Tool Lists: </strong> Depending on <em>direction</em>, the user adds tools currently on the Workflow Canvas to one or more lists, depending on the relationship each tool should have with tools to be suggested; ';
    out += '</li>';

    out += '<li style="padding-bottom: 1em;">';
    out += '<strong>Candidate Tool List:</strong> The list of tools, added to Galaxy, that are available for use on the Workflow Canvas; and ';
    out += '</li>';

    out += '<li>';
    out += '<strong>Goal / Functionality:</strong> A description of what the suggested operations should help accomplish, provided as either keywords (e.g., "multiple sequence alignment") or as IRI to a term in an ontology. ';
    out += '</li>';

    out += '</ol>';

    out += '<p>';
    out += 'The scores given to each tool in the results (a subset of available tools) is the weighted sum of two subscores:  ';
    out += '</p>';

    out += '<ul>';

    out += '<li style="padding-bottom: 1em;">';
    out += '<strong>Data Mediation (<code>dm</code>):</strong> This sub-score is intended to measure how well the inputs and outputs of a tool can be matched to tools in the various lists chosen by the user, either directly or through some form of data mediation. ';
    out += 'It is based on various structural, semantic and syntactic similarity metrics. ';
    out += '</li>';

    out += '<li>';
    out += '<strong>Functionality (<code>fn</code>):</strong> This sub-score is determined by how well a tool matches the goal specified by the user. ';
    out += 'If no goal is specified, then the overall score for a suggested tool is based only on the <code>dm</code> sub-score. ';
    out += '</li>';

    out += '</ul>';

    out += '<p>';
    out += 'More detailed information about how the exact scores are calculated can be found in [2]. ';
    out += 'For examples, please see the link to more documentation at the bottom of this popup. ';
    out += '</p>';

    out += '<h4>References</h4>';

    out += '<p><small>[1] <a href="http://dx.doi.org/10.1109/SERVICES.2010.65" target="_blank"><img src="http://i.imgur.com/IV43Kvj.png" border="0"></a> R. Wang, S. Ganjoo, J.A. Miller, and E.T. Kraemer. "Ranking-Based Suggestion Algorithms for Semantic Web Service Composition," In <em>Proceedings of the 2010 World Congress on Services (SERVICES-1)</em>, 2010, pp 606-613, IEEE.</small></p>';
    out += '<p><small>[2] <a href="http://dx.doi.org/10.3233/978-1-61499-084-0-29" target="_blank"><img src="http://i.imgur.com/IV43Kvj.png" border="0"></a> A. Dhamanaskar, M. E. Cotterell, J. Z. Zheng, J. A. Miller, J. C. Kissinger, and C. J. Stoeckert, "Suggestions in Galaxy Workflow Design Based on Ontologically Annotated Services," in <em>Proceedings of the 7th International Conference on Formal Ontology in Information Systems (FOIS\'12)</em>, 2012, pp. 29-42. IOS.</small></p>';

    out += '<hr />';

    out += '<p>';
    out += '<small style="float: left;">';
    out += 'Last Updated: 2013-04-16';
    out += '</small>';
    out += '<small style="float: right;">';
    out += '<strong>Need more information?</strong> ';
    out += 'Click <a href="http://mango.ctegd.uga.edu/jkissingLab/SWS/sse.html">here</a> to see more documentation.';
    out += '</small>';
    out += '</p>';

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

    ## message to show
    var msg = '<div id="wsx-suggest-values-response"><img src="/static/images/yui/rel_interstitial_loading.gif" /></div>';
    
    ## show the modal
    show_modal( "Suggest input for " + $.wsxDocNode.name + " " + $.wsxDocParam, msg, { "Close" : hide_modal } );

} // wsextensions_suggest_values
$.wsextensions_suggest_values = wsextensions_suggest_values

function wsextensions_render_suggest_values(response) {

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
$.wsextensions_render_suggest_values = wsextensions_render_suggest_values

## Show the about dialog
function wsextensions_show_about() {
    var out = '';

    out += '<p>';
    out += '<strong>Product Name:</strong> Service Suggestion Engine (SSE) Extensions';
    out += '</p>';

    out += '<p>';
    out += '<strong>Product Version:</strong> ' + WSEXTENSIONS_VERSIONS;
    out += '</p>';

    out += '<p>';
    out += 'Service Suggestion Engine (SSE) Extensions for Galaxy is based on software from the University of Georgia Web Services Annotations Group, which has been licensed under an MIT style license (see below). ';
    out += '</p>';

    out += '<p>';
    out += 'For more information, please visit <a href="http://mango.ctegd.uga.edu/jkissingLab/SWS/" target="_blank">http://mango.ctegd.uga.edu/jkissingLab/SWS/</a>. ';
    out += '</p>';

    out += '<p>';
    out += 'The user interface for this tool was implemented by <a href="http://michaelcotterell.com/">Michael E. Cotterell</a>. ';
    out += '</p>';

    out += '<hr />';

    out += '<small>';

    out += '<p>';
    out += 'Copyright (c) 2013 UGA Web Services Annotations Group and the University of Georgia ';
    out += '</p>';

    out += '<p>';
    out += 'Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: ';
    out += '</p>';

    out += '<p>';
    out += 'The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. ';
    out += '</p>';

    out += '<p>';
    out += 'THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.';
    out += '</p>';

    out += '</small>';

    out += '<hr />';

    out += '<p>';
    out += '<small style="float: left;">';
    out += 'Last Updated: 2013-04-16';
    out += '</small>';
    out += '<small style="float: right;">';
    out += '<strong>Need more information?</strong> ';
    out += 'Click <a href="http://mango.ctegd.uga.edu/jkissingLab/SWS/sse.html">here</a> to see more documentation.';
    out += '</small>';
    out += '</p>';

    show_modal( "About Web Service Extensions", out, { "Close" : hide_modal } );
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
    "Report Bug": function() {
        window.open('https://github.com/WSAnnotations/GalaxyExtensions/issues');
    },
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

    ## register the click event for the run button            
    $("#run-se-button").click(wsextensions_se_request);

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

    ## Log it
    wsextensions_log("Preparing to make a request to the Suggestion Engine Web Service.");

    ## Grab candidate operations
    var candidateOps = $wsxCandidates();

    ## Did we find any candidate operations? If not, let us register an error.
    if (candidateOps.length == 0) {
        wsextensions_error("Could not find any candidate operations.");
    } // if

    ## STEP 2 - Gather information about the current state of the workflow. 
    wsextensions_log("Gathering current workflow operations.");

    ## These arrasy will hold the workflow operations.
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

                ## add it to allOps
                allOps.push(op);
                
                ## is it in the pred list?
		$("#wsx-pred-list ul").find('li').each(function(){
                    var current = $(this);
                    if (current.text().indexOf(wsop) !== -1) {
	    	        workflowOps1.push(op)
                    } // if
                });

                ## is it in the succ list?
                $("#wsx-succ-list ul").find('li').each(function(){
                    var current = $(this);
                    if (current.text().indexOf(wsop) !== -1) {
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
        wsextensions_log("Gathering information from the form in the Suggestion Engine interface.");

        ## The desired functionality. Either some string similar to an operation
        ## or a URI to some concept in an ontology.
        var desiredFunctionality = $('#suggestionEngineDesired').attr('value');

	## Determine the suggestion direction
        var direction = "";
        $("#suggestionEngineSuggestionTypeList option:selected").each(function () {
            direction = $(this).val();
        });

        ## create the request
        var payload = request(direction, workflowOps1, workflowOps2, candidateOps, desiredFunctionality);
        wsextensions_log('Generated the SSE-WS request payload: ' + JSON.stringify(payload))

        var jsonURI = WSEXTENSIONS_SE_SERVICE_URI
            + "/serviceSuggestion/get/jsonp"
            + "?payload=" + JSON.stringify(payload);

        wsextensions_log('Using the following URI (jQuery will add &callback=?): ' + jsonURI)
    
        ## make a JSON request
        $.getJSON(jsonURI + "&callback=?", wsextensions_se_parse_response);

    } // if

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
    
    ## Update counts
    $('#wsx-result-count-info').show();
    $('#wsx-result-count-show').text(5);
    $('#wsx-result-count').text(n);
    
    ## if there are more than 5 results, then display "Show All"
    if (n > 5) {
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
        wsextensions_error("Received a response from the Suggestion Engine Web Service, but it did not contain any results");
        ## @TODO handle this more gracefully
    } // if    
    
    $.wsxSuggestions = suggestions

    ## prepare output list
    var out = '<ul id="wsx-result-list" style="list-style-type: square;">';

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
    wsextensions_log("Response parsed, rendering results to the Suggestion Engine interface.");

    ## Hide the progress bar.
    $("#suggestion-engine-results-progress").hide();

    ## display the results
    $("#suggestion-engine-results-content").replaceWith('<div id="suggestion-engine-results-content">' + out + '</div>');

} // function wsextensions_se_parse_response



