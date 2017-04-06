<#include "page.ftl" />
<@page>
	<form method='POST' action='${config.url}'>
		<fieldset>
		    <legend class="title">${config.title}</legend>
			<#-- <input type="checkbox" name="In Memory" value="true" />In-Memory Datenbank<br />  -->
			<#list properties as key, value>
				<label class="label" for="${key}">${key}:</label>
				<input class="box" size="${max}" type="text" name="${key}" id="${key}" value="${value}">
  			</#list>
  		<input class="button" type="submit" value="Speichern" />
		</fieldset>
	</form>
</@page>
