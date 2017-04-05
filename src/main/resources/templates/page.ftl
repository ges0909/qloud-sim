<#macro page>
<!DOCTYPE html>
<html>
<head>
    <title>Qloud-Simulator</title>
    <!--[if IE]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
	<link href="css/page.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<div id="wrapper">
		<#include "header.ftl">
    	<div id="contentliquid">
    		<div id="contentwrap">
    			<div id="content">
    				<p><#nested /></p>
    			</div>
    		</div>
 		</div>
    	<#include "menu.ftl">
 		<#-- <#include "footer.ftl"> -->
</body>
</html>
</#macro>
