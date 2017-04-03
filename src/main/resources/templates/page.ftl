<#macro page title="">
<!DOCTYPE html>
<html>
<head>
    <title>${title}</title>
    <!--[if IE]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
	<link rel="stylesheet" type="text/css" href="css/335.css" />
</head>
<body>
	<div id="wrapper">
    <#include "header.ftl">
    <#include "menu.ftl">
    <div id="contentliquid"><div id="contentwrap">
    <div id="content">
    	<#nested />
    </div
    <#-- <#include "footer.ftl"> -->
    </div>
</body>
</html>
</#macro>
