/*
 * Copyright 2013 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.splunk.dev.sdk.java.ui;

import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.splunk.dev.sdk.java.ui.SplunkSDKProjectWizard.LoggingFramework;
import com.splunk.dev.sdk.java.ui.SplunkSDKProjectWizard.SplunkSDKProjectCreationOptions;

/*
 * NewSplunkSDKProjectWizardPageOne adds two check boxes in a group to the
 * first page of the new Java project wizard. The new behavior is all called in
 * createControl, which calls the superclass createControl, and then adds
 * our own control (which is encapsulated in createOptionalJarsControl).
 */
public class NewSplunkSDKProjectWizardPageOne extends
		NewJavaProjectWizardPageOne {
	public SplunkSDKProjectCreationOptions options;
	
	public NewSplunkSDKProjectWizardPageOne(SplunkSDKProjectCreationOptions options) {
		this.options = options;
	}
	
	public Control createOptionalJarsControl(Composite parent) {
		Group optionalJarsGroup = new Group(parent, SWT.NONE);
		
		optionalJarsGroup.setFont(parent.getFont());
		optionalJarsGroup.setText("Add optional Splunk data format support");
		optionalJarsGroup.setLayout(new GridLayout(1, false));
		
		final Label explanationLabel = new Label(optionalJarsGroup, SWT.WRAP);
		explanationLabel.setText("Splunk defaults to XML as its wire format. If you want to use its alternate " +
		                 "formats (JSON or CSV) you need to add jars so Java can parse these formats.");
		explanationLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button supportJsonButton = new Button(optionalJarsGroup, SWT.CHECK);
		supportJsonButton.setText("Add JSON support to project (optional)");
		supportJsonButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		supportJsonButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				options.supportJson = supportJsonButton.getSelection();
			}
		});

		final Button supportCsvButton = new Button(optionalJarsGroup, SWT.CHECK);
		supportCsvButton.setText("Add CSV support to project (optional)");
		supportCsvButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		supportCsvButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				options.supportCsv = supportCsvButton.getSelection();
			}
		});
		
		return optionalJarsGroup;	
	}
	
	public Control createOptionalLoggingJarsControl(Composite parent) {
		Group optionalLoggingGroup = new Group(parent, SWT.NONE);
		
		optionalLoggingGroup.setFont(parent.getFont());
		optionalLoggingGroup.setText("Add support for logging libraries");
		optionalLoggingGroup.setLayout(new GridLayout(1, false));
		
		final Label explanationLabel = new Label(optionalLoggingGroup, SWT.WRAP);
		explanationLabel.setText("The Splunk plug-in for Eclipse can set up " +
				"a logging library and code to log to Splunk and to create " +
				"logging events following Splunk's Common Information Management " +
				"recommendations");
		explanationLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		final Composite radioButtonRow = new Composite(optionalLoggingGroup, SWT.NULL);
		radioButtonRow.setLayout(new RowLayout());
		
		final Button addLoggingSupport = new Button(radioButtonRow, SWT.CHECK);
		addLoggingSupport.setText("Add logging support: ");
		
		final Button logbackButton = new Button(radioButtonRow, SWT.RADIO);
		logbackButton.setText("Logback");
		logbackButton.setSelection(true);
		logbackButton.setEnabled(false);
		logbackButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (logbackButton.getSelection()) {
					options.loggingSupport = LoggingFramework.LOGBACK;
				}
			}
		});
		
		final Button log4jButton = new Button(radioButtonRow, SWT.RADIO);
		log4jButton.setText("Log4j");
		log4jButton.setEnabled(false);		
		log4jButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (log4jButton.getSelection()) {
					options.loggingSupport = LoggingFramework.LOG4J;
				}
			}
		});
		
		
		final Button javaUtilButton = new Button(radioButtonRow, SWT.RADIO);
		javaUtilButton.setText("java.util.logging");
		javaUtilButton.setEnabled(false);
		javaUtilButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (javaUtilButton.getSelection()) {
					options.loggingSupport = LoggingFramework.JAVA_UTIL_LOGGING;
				}
			}
		});
	
		
		addLoggingSupport.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				log4jButton.setEnabled(addLoggingSupport.getSelection());
				logbackButton.setEnabled(addLoggingSupport.getSelection());
				javaUtilButton.setEnabled(addLoggingSupport.getSelection());
				
				if (addLoggingSupport.getSelection()) {
					if (javaUtilButton.getSelection()) {
						options.loggingSupport = LoggingFramework.JAVA_UTIL_LOGGING;
					} else if (log4jButton.getSelection()) {
						options.loggingSupport = LoggingFramework.LOG4J;
					} else if (logbackButton.getSelection()) {
						options.loggingSupport = LoggingFramework.LOGBACK;
					}
				} else {
					options.loggingSupport = LoggingFramework.NONE;
				}
			}
		});
		
		return (Control)optionalLoggingGroup;
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		Composite composite = (Composite)getControl();
		
		Control optionalJarsControl = createOptionalJarsControl(composite);
		optionalJarsControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Control optionalLoggingControl = createOptionalLoggingJarsControl(composite);
		optionalLoggingControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

}
