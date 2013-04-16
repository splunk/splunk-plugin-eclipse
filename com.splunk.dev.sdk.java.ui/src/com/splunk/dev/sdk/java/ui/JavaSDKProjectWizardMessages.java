package com.splunk.dev.sdk.java.ui;

import org.eclipse.osgi.util.NLS;

public class JavaSDKProjectWizardMessages extends NLS {
    private static final String BUNDLE_NAME = "com.splunk.dev.sdk.java.ui.messages"; //$NON-NLS-1$
    public static String JavaSDKProjectWizard_WindowTitle;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, JavaSDKProjectWizardMessages.class);
    }

    private JavaSDKProjectWizardMessages() {
    }
}
