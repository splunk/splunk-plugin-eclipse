package com.splunk.dev.sdk.java.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class JavaSDKProjectWizardPage extends WizardPage {
	private String newProjectName;

	public JavaSDKProjectWizardPage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	public JavaSDKProjectWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		// TODO Auto-generated constructor stub
	}

	public String getProjectName() {
		return newProjectName;
	}
	
	@Override
	public void createControl(Composite container) {
		Composite parent = new Composite(container, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
		
		GridData gridData;

		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("Project name");
		gridData = new GridData(SWT.END);
		projectNameLabel.setLayoutData(gridData);
		
		newProjectName = "";
		final Text text = new Text(parent, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gridData);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				newProjectName = text.getText();
				System.out.println("Project name set: " + newProjectName);
			}
		});

		setControl(parent);
	}

}
