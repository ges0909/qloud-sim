<#include "page.ftl" />
<@page>
<form method='post' enctype='multipart/form-data'>
	<legend class="title">${upload.title}</legend>
	<input type='file' name='uploaded_file'>
	<button>${upload.title}</button>
</form>
</@page>

