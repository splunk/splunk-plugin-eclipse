package com.splunk.modularinput.java;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class ModularInputWizard extends Wizard implements INewWizard {
	WizardPage page;
	Map<String, String> options;
	
	public ModularInputWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		options = new HashMap<String, String>();
		page = new ModularInputWizardPage(options);
	}
	
	@Override
	public void addPages() {
		addPage(new ModularInputWizardPage(options));
	}

	@Override
	public boolean performFinish() {
		if (options.get("appid").equals("")) {
			
		}
		
		return true;
	}
}
