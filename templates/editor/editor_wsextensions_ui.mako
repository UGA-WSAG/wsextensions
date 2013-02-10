## Div for suggestion engine.
## @author mepcotterell
        <div id="suggestion-engine" class="metadataForm right-content" style="display:none;">

            <div class="toolFormTitle">Service Suggestion Engine</div>

            <div class="form-row">Version: 1.4</div>

            <div class="metadataFormTitle">[-] Options</div>
            <div id="suggestion-options-body" class="metadataFormBody">
                <div class="form-row">
                    
                    ## Choose
                    ##
                    ## forward = http://i.imgur.com/ex27zRI.png
                    ## backward = http://i.imgur.com/XoD0WCw.png
                    ## bidirectional = http://i.imgur.com/MfTWkIm.png
                    ##

                    <label><span id="suggestion-type-toggle">[-]</span> Choose Suggestion Type:</label>
                    
                    <div id="suggestion-settings-type">

                        <div class="toolParamHelp">Customize the kind of suggestions you want by indicating where you think it should go in the workflow.</div>
                        
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
                        
                        <p class="infomark">Click <a id="suggestion-type-help" href="#">here</a> for more information about the different types of service suggestions.</p>

                    </div>

                    <div id="suggestion-forward">

                        <hr />

                        <label><span id="suggestion-forward-toggle">[-]</span> Tools to Feed From:</label>

                        <div id="suggestion-settings-forward">

                            <div class="toolParamHelp">Select the tools in current workflow that you think should feed into suggested service operations.</div>

                            <p style="padding-top: 1em;">
                                <select id="suggestionEnginePredecessorList" type="text" name="predecessor" />
                                </select>
                            </p>

                        </div>

                    </div>

                    <div id="suggestion-backward" style="display:none;">

                        <hr />

                        <label><span id="suggestion-backward-toggle">[-]</span> Tools to Feed Into:</label>

                            <div id="suggestion-settings-backward">

                            <div class="toolParamHelp">Select the tools in current workflow that you think should feed from suggested service operations.</div>

                            <p style="padding-top: 1em;">
                                <select id ="suggestionEngineSuccessorList" type="text" name="successor" />
                                </select>
                            </p>

                        </div>

                    </div>

                    <hr />
                    
                    ## Proposed concept
                    <label><span id="suggestion-goal-toggle">[-]</span> Goal / Purpose:</label>

                    <div id="suggestion-settings-goal">

                        <div class="toolParamHelp">In simple terms, describe what you are trying to do/find (e.g., multiple sequence alignment).</div>

                        <p style="padding-top: 1em;">
                        <input id="suggestionEngineDesired" type="text" name="concept" value=""/> <a id="suggestion-goal-help" href="#"><img style="padding-left: 0.5em;" alt="help" src="/static/style/info_small.png" /></a>
                        </p>

                    </div>

                    ## this area is reserved for debug output
                    <div id="suggestion-engine-debug" style="display:none;"></div>

                </div>
            </div>

            <div class="metadataFormTitle">[-] Run Suggestion Query</div>
            <div id="suggestion-run-body" class="metadataFormBody">
                <div class="form-row">
                    ## Run button
                    <div class='action-button' style='border:1px solid black;display:inline;' id='run-se-button'>Query Suggestion Engine</div>
                </div>
            </div>

            <div id="suggestion-engine-results-frame" style="display:none;">
                <div class="metadataFormTitle">[-] Query Results</div>
                <div class="metadataFormBody">
                    <div class="form-row">
                        ## this is where the results are displayed                    
                        <div id="suggestion-engine-results" style="display:none;">

                            <label>Ranked Suggestion Results</label>

                            <div class="toolParamHelp">Listed below are the suggested tools returned by the Service Suggestion Engine. Click on a tool name to add it to the current workflow. Click on the numbers in parenthesese to view a breakdown of how closely a tool matches the suggestion query. </div>

                            <p>

                            <div id="suggestion-engine-results-progress" style="display:none;">
                                <img src="/static/images/yui/rel_interstitial_loading.gif" />
                            </div>

                            <div id="suggestion-engine-results-content"></div>

                            </p>

                        </div>
                    </div>
                </div>
            </div>

        </div>
