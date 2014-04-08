# INSTALL

## SSE - Galaxy Integration Interface

1. Merge the <code>templates</code> directory into your Galaxy distribution.

2. Make the following chages to your <code>templates/workflow/editor.mako</code>
    file:

  * Find:
    ```
    // jQuery onReady
    $( function() {
    ```
    and replace with:
    ```
    // jQuery onReady
    $( function() {

        ## Include Service Suggestion Extensions
        <%include file="editor_wsextensions.mako"/>
    ```
  * Find:
    ```
    ## Div where tool details are loaded and modified.
    <div id="right-content" class="right-content"></div>
    ```
    and replace with:
    ```
    ## Include the Service Suggestions Extensions UI
    <%include file="editor_wsextensions_ui.mako"/>

    ## Div where tool details are loaded and modified.
    <div id="right-content" class="right-content"></div>
    ```
  * Find:
    ```
    var b = $('<a class="popup-arrow" id="popup-arrow-for-' + id + '">&#9660;</a>');
    var options = {};
    ```
    and replace with:
    ```
    var b = $('<a class="popup-arrow" id="popup-arrow-for-' + id + '">&#9660;</a>');
    var options = {};

    ## Include the Service Suggestion Extensions - Parameters Plugin
    <%include file="editor_wsextensions_param.mako"/>
    ```
  * That's it!

