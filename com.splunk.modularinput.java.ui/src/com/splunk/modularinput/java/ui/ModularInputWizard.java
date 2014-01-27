package com.splunk.modularinput.java.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.splunk.modularinput.java.ModularInputTasks;

public class ModularInputWizard extends Wizard implements INewWizard {
	ModularInputWizardPage page;
	Map<String, String> options;
	Map<String, String> errors;
	
	public ModularInputWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		options = new HashMap<String, String>();
		errors = new HashMap<String, String>();
	}
	
	@Override
	public void addPages() {
		page = new ModularInputWizardPage(options, errors);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		if (page.isValid()) {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException,
						InvocationTargetException, InterruptedException {
					String projectName = options.get("appid");
					ModularInputTasks.generateAppSkeleton(projectName, options, monitor);
				}
			};
			try {
				// Fetch the IWizardContainer object that is running this wizard,
				// and which accepts WorkspaceOperation objects to execute.
				getContainer().run(true, true, op);
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(options.get("appid"));
				IFile javaFile = project.getFile("src/Main.java");
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IDE.openEditor(page, javaFile);
				return true;
			} catch (InvocationTargetException e) { 
				// One of the steps resulted in a core exception
				Throwable t = e.getTargetException();
				StatusManager.getManager().handle(new Status(
						Status.ERROR, 
						Activator.PLUGIN_ID, 
						"Error in configuring new Splunk modular input project", 
						t), 
					StatusManager.SHOW
				);
				return false;
			} catch (InterruptedException e) {
				return false;
			} catch (PartInitException e) {
				StatusManager.getManager().handle(new Status(
						Status.ERROR, 
						Activator.PLUGIN_ID, 
						"Error in configuring new Splunk modular input project", 
						e), 
					StatusManager.SHOW
				);
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean canFinish() {
		return page.isValid();
	}
	
}
