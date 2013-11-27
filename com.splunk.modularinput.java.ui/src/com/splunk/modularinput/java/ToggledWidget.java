package com.splunk.modularinput.java;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class ToggledWidget<T extends Control> extends Composite {
	Button toggle;
	T widget;
	boolean enableWhenChecked;
	
	public ToggledWidget(Composite parent, String toggleLabel, final boolean checkedByDefault, final boolean enableOnChecked, WidgetCreation<T> creation, int style) {
		super(parent, style);
		
		this.enableWhenChecked = enableOnChecked;
		
		setLayout(new GridLayout(1, true));
		
		toggle = new Button(this, SWT.CHECK);
		toggle.setText(toggleLabel);
		toggle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toggle.setSelection(checkedByDefault);
		
		widget = creation.init(this);
		widget.setLayoutData(new GridData(GridData.FILL_BOTH));
		widget.setEnabled(enableWhenChecked == checkedByDefault);
		
		toggle.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				widget.setEnabled(toggle.getSelection() == enableWhenChecked);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}
	
	public T getWidget() {
		return widget;
	}
	
	public void addSelectionListener(SelectionListener listener) {
		toggle.addSelectionListener(listener);
	}
	
	public boolean getWidgetEnabled() {
		return widget.getEnabled();
	}
	
	public void setWidgetEnabled(boolean selection) {
		toggle.setSelection(selection == enableWhenChecked);
	}
	
}
