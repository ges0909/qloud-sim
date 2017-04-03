<#include "page.ftl" />
<@page title="Konfiguration">
	<form method='post'>
		<fieldset>
			<#--<legend>In-Memory Datenbank?</legend>-->
			<input type="checkbox" name="In Memory" value="true" />In-Memory Datenbank<br />
			<input type="submit" value="Setzen" />
		</fieldset>
	</form>
</@page>
