## Div for suggestion engine.
## @author mepcotterell
        <div id="suggestion-engine" class="metadataForm right-content" style="display:none;">

            <div class="toolFormTitle">Service Suggestion Engine</div>

            <div class="form-row">Version: 1.6</div>

            <div id="wsx-section-options" class="metadataFormTitle">
	    <span class="wsx-toggle-shrink" style="cursor: hand; cursor: pointer;">[-]</span>            
            Setup &amp; Run Query
            </div>
            <div id="wsx-section-options-body" class="metadataFormBody">
                <div class="form-row">
                    
                    ## Choose
                    ##
                    ## forward = http://i.imgur.com/ex27zRI.png
                    ## backward = http://i.imgur.com/XoD0WCw.png
                    ## bidirectional = http://i.imgur.com/MfTWkIm.png
                    ##

                    <label>Choose Suggestion Type:</label>

                    <div id="wsx-section-options-type-body">

                        <div class="toolParamHelp">Choose the kind of suggestions you want by indicating where you think they should go in the workflow.</div>
                        
                        <p style="padding-top: 1em;">
                        <select id ="suggestionEngineSuggestionTypeList" type="text" name="successor" />
                            <option value="forward">Forward Suggestion</option>
                            <option value="backward">Backward Suggestion</option>
                            <option value="bidirectional">Bidirectional Suggestion</option>
                        </select>
                        </p>

                        <div id="suggestion-forward-image"><img style="max-height: 45px; margin: auto;" alt="type" src="http://i.imgur.com/ex27zRI.png"></div>
                        <div id="suggestion-backward-image" style="display:none;"><img  style="max-height: 45px" alt="type" src="http://i.imgur.com/XoD0WCw.png"></div>
                        <div id="suggestion-bidirectional-image" style="display:none;"><img  style="max-height: 45px" alt="type" src="http://i.imgur.com/MfTWkIm.png"></div>
                        
                        <p class="infomark">Click <a id="suggestion-type-help" href="#">here</a> for more information about the different types of service suggestion.</p>

                    </div>

                    <div id="suggestion-forward">

                        <hr />

                        <label>Tools to Feed From:</label>

                        <div id="suggestion-settings-forward">

                            <div class="toolParamHelp">Select the tools in current workflow that you think should feed into suggested service operations.</div>

                            <p style="padding-top: 1em;">
			        <div id="wsx-pred-list"><ul style="list-style-type: none; padding-left: 0; margin-left: 0;"></ul></div>
                                <select id="suggestionEnginePredecessorList" type="text" name="predecessor" style="width: 100%;" />
                                </select>
                            </p>

                        </div>

                    </div>

                    <div id="suggestion-backward" style="display:none;">

                        <hr />

                        <label>Tools to Feed Into:</label>

                            <div id="suggestion-settings-backward">

                            <div class="toolParamHelp">Select the tools in current workflow that you think should feed from suggested service operations.</div>

                            <p style="padding-top: 1em;">
			        <div id="wsx-succ-list"><ul style="list-style-type: none; padding-left: 0; margin-left: 0;"></ul></div>	
                                <select id ="suggestionEngineSuccessorList" type="text" name="successor" style="width: 100%;" />
                                </select>
                            </p>

                        </div>

                    </div>

                    <hr />
                    
                    ## Proposed concept
                    <label>Goal / Purpose:</label>

                    <div id="suggestion-settings-goal">

                        <div class="toolParamHelp">In simple terms, describe what you are trying to do/find (e.g., multiple sequence alignment).</div>

                        <p style="padding-top: 1em;">
                        <input id="suggestionEngineDesired" type="text" name="concept" value="" style="width: 100%;"/>
                        </p>
	                <p class="infomark">Click <a id="suggestion-goal-help" href="#">here</a> for more information about specifying goals.</p>

                    </div>

                    <hr />

                    ## Run button
                    <div class='action-button' style='border:1px solid black; display:inline;' id='run-se-button'>Run Query / Get Suggestions</div>
                </div>
            </div>

            <div id="suggestion-engine-results-frame" style="display:none;">

                <div id="wsx-section-results" class="metadataFormTitle">
	        <span class="wsx-toggle-shrink" style="cursor: hand; cursor: pointer;">[-]</span>            
                Query Results
                </div>
                <div id="wsx-section-results-body" class="metadataFormBody">
                    <div class="form-row">

                        ## this is where the results are displayed                    
                        <div id="suggestion-engine-results" style="display:none;">

                            <label>Ranked Suggestion Results</label>

                            <div class="toolParamHelp">These are SEE suggested tools. Click on a tool to add it to your workflow. Tools are ranked by a relevance score (1->0).</div>

			    <br />

			    <div id="wsx-result-count-info" style="display:none;" class="toolParamHelp">Showing <span id="wsx-result-count-show">?</span> of <span id="wsx-result-count">?</span> results.<span id="wsx-result-more" style="display:none;"> | <a id="wsx-result-more-button" href="#">Show All</a></span></div>

                            <p>

                                <div id="suggestion-engine-results-progress" style="display:none;">
                                    <img src="/static/images/yui/rel_interstitial_loading.gif" />
                                </div>

                                <div id="suggestion-engine-results-content"></div>

                            </p>

	                <p class="infomark">Click <a id="suggestion-result-help" href="#">here</a> for more information about how results are ranked.</p>

                        </div>
                    </div>
                </div>
            </div>

        </div>
