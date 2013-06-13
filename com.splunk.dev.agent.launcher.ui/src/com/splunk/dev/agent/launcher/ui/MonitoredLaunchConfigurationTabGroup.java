package com.splunk.dev.agent.launcher.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;

import com.splunk.dev.agent.launcher.Activator;

public class MonitoredLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public MonitoredLaunchConfigurationTabGroup() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		
		// This code is copied from the method on JavaLaunchConfigurationTabGroup,
		// with the sole exception of inserting "new SplunkTab()," right before
		// "new CommonTab()". This set of tabs changes very rarely, so it is
		// cleaner to copy this one statement than to try to dig into the
		// JDT dynamically to get the tab list.
		//
		// If you are adding additional tabs, they should go after SplunkTab
		// but before CommonTab. Eclipse's UI guidelines specify that CommonTab
		// should always be last.
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new JavaMainTab(),
				new JavaArgumentsTab(),
				new SourceLookupTab(),
				new EnvironmentTab(),
				new SplunkTab(),
				new CommonTab()
		};
		setTabs(tabs);

	}

}
