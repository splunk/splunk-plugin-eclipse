package com.splunk.modularinput.java;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class LabelledWidget<T extends Control> extends Composite {
	private T widget;
	private Label leftLabel;
	private Label bottomLabel = null;
	
	public LabelledWidget(
			Composite parent, 
			String label, 
			String description, 
			WidgetCreation<T> creation, 
			int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		leftLabel = new Label(this, SWT.NONE);
		leftLabel.setText(label);

		widget = creation.init(this);
		widget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if (description != null) {
			bottomLabel = new Label(this, SWT.NONE);
			bottomLabel.setText(description);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;
			bottomLabel.setLayoutData(gridData);
		}
	}

	public T getWidget() {
		return (T) widget;
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		widget.setEnabled(enabled);
		leftLabel.setEnabled(enabled);
		if (bottomLabel != null) {
			bottomLabel.setEnabled(enabled);
		}
	}
}