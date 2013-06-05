package com.splunk.dev.agent.launcher.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.splunk.dev.agent.launcher.SplunkLaunchData;

public class SplunkTab extends AbstractLaunchConfigurationTab implements
		ILaunchConfigurationTab {
	Text hostTextbox;
	Text portTextbox;
	
	@Override
	public void createControl(Composite parent) {
		Composite comp = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_BOTH);

		GridData gd;
		Label label;
		
		GridLayout layout = new GridLayout(2, false);
		comp.setLayout(layout);
		label = new Label(comp, SWT.LEFT);
		gd = new GridData();
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
		label.setText("Host and TCP port to write monitoring logs to (should be a Splunk TCP input).");
		
		label = new Label(comp, SWT.RIGHT);
		label.setLayoutData(new GridData());
		label.setText("Host:");
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				scheduleUpdateJob();
			}
		};
		
		hostTextbox = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		hostTextbox.setLayoutData(gd);
		hostTextbox.addModifyListener(modifyListener);
		
		label = new Label(comp, SWT.RIGHT);
		label.setLayoutData(new GridData());
		label.setText("Port:");
		
		portTextbox = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		portTextbox.setLayoutData(gd);
		portTextbox.addModifyListener(modifyListener);
		portTextbox.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				Text t = (Text)e.getSource();
				final String textBeforeEvent = t.getText();
		        String textAfterEvent = textBeforeEvent.substring(0, e.start) +
		        		e.text + textBeforeEvent.substring(e.end);

				e.doit = textAfterEvent.matches("^[0-9]*$");
			}
		});
		
		setControl(comp);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(SplunkLaunchData.hostAttribute, SplunkLaunchData.defaultHost);
		configuration.setAttribute(SplunkLaunchData.portAttribute, SplunkLaunchData.defaultPort);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			hostTextbox.setText(
					configuration.getAttribute(SplunkLaunchData.hostAttribute, SplunkLaunchData.defaultHost)
			);
		} catch (CoreException e) {
			hostTextbox.setText(SplunkLaunchData.defaultHost);
		}
		
		try {
			// Since we cannot have null in am int, we use -1 to represent
			// an empty text box.
			int port = configuration.getAttribute(SplunkLaunchData.portAttribute, SplunkLaunchData.defaultPort);
			if (port < 0) {
				portTextbox.setText("");
			} else {
				portTextbox.setText(String.valueOf(port));
			}	
		} catch (CoreException e) {
			portTextbox.setText(String.valueOf(SplunkLaunchData.defaultPort));
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(SplunkLaunchData.hostAttribute, hostTextbox.getText());
		
		// Since we cannot have null in an int, we use -1 to represent an
		// empty text box.
		Integer port = Integer.valueOf(portTextbox.getText());
		if (port == null) {
			configuration.setAttribute(SplunkLaunchData.portAttribute, -1);
		} else {
			configuration.setAttribute(SplunkLaunchData.portAttribute, port);
		}
	}

	@Override
	public String getName() {
		return "Splunk";
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		if (!super.isValid(launchConfig)) {
			return false;
		}
		try {
			String host = launchConfig.getAttribute(SplunkLaunchData.hostAttribute, "");
			if (host.length() <= 0) {
				setErrorMessage("Host name cannot be empty.");
				return false;
			}
			
			int port = launchConfig.getAttribute(SplunkLaunchData.portAttribute, -1);
			if (port < 1) {
				setErrorMessage("Port must be a positive integer.");
				return false;
			}
		} catch (CoreException e) {
			return false;
		}
		return true;
	}
}
