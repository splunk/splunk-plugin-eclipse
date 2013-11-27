package com.splunk.modularinput.java;

import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ModularInputWizardPage extends WizardPage {
	Map<String, String> options;

	public ModularInputWizardPage(Map<String, String> options) {
		super("New Splunk modular input in Java");
		this.options = options;
		setTitle("New Splunk modular input in Java");
		setDescription("Create a new project for developing Splunk modular inputs.");
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new FillLayout());
		
		final Button button = new Button(control, SWT.PUSH);
		button.setText("Hello!");
		button.setVisible(true);
		
		setControl(control);
				
	}

}
