<#include "page.ftl" />
<@page>
<form method='post' enctype='multipart/form-data'>
	<legend class="title">${upload.title}</legend>
	<label class="label" for="email">E-Mail</label>
	<input class="box" size="40" type="text" name="email" id="email" value="">
	<input type='file' name='uploaded_file'>
	<button>${upload.title}</button>
</form>
</@page>

