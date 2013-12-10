package com.splunk.modularinput.java.ui;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface WidgetCreation<T extends Control> {
	public T init(Composite parent);
}