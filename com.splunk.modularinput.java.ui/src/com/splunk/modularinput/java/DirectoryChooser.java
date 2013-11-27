package com.splunk.modularinput.java;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

public class DirectoryChooser extends Composite {
	Text pathText;
	Button chooseButton;
	
	public DirectoryChooser(Composite parent) {
		super(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);
		
		GridData gridData;
		
		pathText = new Text(this, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		pathText.setLayoutData(gridData);
		
		chooseButton = new Button(this, SWT.NONE);
		chooseButton.setText("Browse...");
		chooseButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(chooseButton.getShell(), SWT.NONE);
				dirDialog.setText("Choose a location for the project");
				String path = dirDialog.open();
				if (path == null) {
					return;
				} else {
					pathText.setText(path);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});	
	}
	
	public String getPath() {
		String text = pathText.getText();
		if (text.length() == 0) {
			return null;
		} else {
			return text;
		}
	}
	
	public void setPath(String path) {
		pathText.setText(path);
	}
	
	public void setDefaultPath(String projectName) {
		setPath(Platform.getLocation().append(projectName).toOSString());
	}
	
	public void setEnabled(boolean e) {
		pathText.setEnabled(e);
		chooseButton.setEnabled(e);
	}
	
	public boolean getEnabled() {
		return pathText.getEnabled();
	}
	
	public void addModifyListener(ModifyListener listener) {
		pathText.addModifyListener(listener);
	}
}