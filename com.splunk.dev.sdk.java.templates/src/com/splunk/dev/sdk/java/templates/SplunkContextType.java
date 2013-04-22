package com.splunk.dev.sdk.java.templates;

import org.eclipse.jdt.internal.corext.template.java.AbstractJavaContextType;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jface.text.templates.TemplateContextType;



public class SplunkContextType extends AbstractJavaContextType {
	private static final String SPLUNK_CONTEXT_ID = "splunk";

	@Override
	protected void initializeContext(JavaContext context) {
		context.addCompatibleContextType(SPLUNK_CONTEXT_ID);
	}
}
