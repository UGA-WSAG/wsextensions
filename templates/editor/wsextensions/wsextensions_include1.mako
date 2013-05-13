## Include the Galaxy Web Service Extensions UI file.
<%include file="editor_wsextensions_ui.mako"/>

## Include and Execute Galaxy Web Service Extensions Logic File.
<script type='text/javascript'>
    (function () {
	<%include file="editor_wsextensions.mako"/>
    })();
</script>
