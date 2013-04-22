package com.splunk.dev.sdk.java.ui;

import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

public class NewExampleWizardPageOne extends NewJavaProjectWizardPageOne {
	public ReadmeAndChangelogModel readmeAndChangelogModel;

	public NewExampleWizardPageOne(ReadmeAndChangelogModel model) {
		readmeAndChangelogModel = model;
	}
	
	public Control createReadmeAndChangelogControl(Composite parent) {
		Group readmeAndChangelogGroup = new Group(parent, SWT.NONE);
		
		readmeAndChangelogGroup.setFont(parent.getFont());
		readmeAndChangelogGroup.setText("Create README and CHANGELOG");
		readmeAndChangelogGroup.setLayout(new GridLayout(1, false));

		final Button createReadmeButton = new Button(readmeAndChangelogGroup, SWT.CHECK);
		createReadmeButton.setText("Create a README file in project root");
		createReadmeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createReadmeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				readmeAndChangelogModel.createReadme = createReadmeButton.getSelection();
				System.out.println("Create readme: " + readmeAndChangelogModel.createReadme);
			}
		});

		final Button createChangelogButton = new Button(readmeAndChangelogGroup, SWT.CHECK);
		createChangelogButton.setText("Create a CHANGELOG file in project root");
		createChangelogButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createChangelogButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				readmeAndChangelogModel.createChangelog = createChangelogButton.getSelection();
				System.out.println("Create changelog: " + readmeAndChangelogModel.createChangelog);
			}
		});
		
		return readmeAndChangelogGroup;	
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		Composite composite = (Composite)getControl();
		
		Control readmeAndChangelogControl = createReadmeAndChangelogControl(composite);
		readmeAndChangelogControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

}
