// WSEXTENSIONS
var label = $(this).parents( "div.form-row" ).find('label');
label = $(label).text().replace("\n", "").replace(/[^A-Za-z0-9]/g, "").replace("Setatruntime", "").substring(5);
		
// WSEXTENSIONS
options[ "<small>[SSE]</small> Suggest Input Values" ] = function() {
    $.wsextensions.showInputSuggestDialog(node, label);
};

// WSEXTENSIONS
options[ "<small>[SSE]</small> Documentation" ] = function() {
    $.wsextensions.showInputDocDialog(node, label);
};


