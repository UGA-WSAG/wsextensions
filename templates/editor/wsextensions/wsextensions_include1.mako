## Include the Galaxy Web Service Extensions UI file.
<%include file="wsextensions_ui.mako"/>

## Include and Execute Galaxy Web Service Extensions Logic File.
<script type='text/javascript'>
    (function () {
	<%include file="wsextensions_core.mako"/>
    })();
</script>
