package de.infinit.emp;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class FreeMarkerConfig extends Configuration {

	public FreeMarkerConfig() {
		super(new Version(2, 3, 26));
		setClassForTemplateLoading(Application.class, "/templates");
		setDefaultEncoding("UTF-8");
		setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER /*HTML_DEBUG_HANDLER*/);
		setLogTemplateExceptions(false);
	}
}
