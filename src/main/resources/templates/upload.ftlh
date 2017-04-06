<#include "page.ftl" />
<@page>
<form method='post' enctype='multipart/form-data'>
	<legend class="title">${upload.title}</legend>
	<label class="label" for="email">Nutzer E-Mail:</label>
	<input class="box" size="40" type="text" name="email" id="email" value="">
	<label class="label" for="upload">CSV-Datei:</label>
	<input class="box" type='file' name='uploadFile'>
	<button>${upload.title}</button>
</form>
</@page>

