/*
 * Copyright 2013 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.splunk.project.java.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;

/* (non-Javadoc)
 * The SplunkSDKProjectWizard class implements a small extension to the 
 * behavior of Eclipse's new Java project wizard. The wizard has two pages: 
 * NewSplunkSDKProjectWizardPageOne, which subclasses 
 * NewJavaProjectWizardPageOne and adds two additional controls to specify
 * whether to support JSON and/or CSV as wire formats from Splunk in the new 
 * project, and NewJavaProjectWizardPageTwo, which is the default page two from
 * the new Java project wizard. The important behavior happens in the 
 * performFinish method. 
 */
@SuppressWarnings("restriction")
public class SplunkSDKProjectWizard extends NewElementWizard implements
		IExecutableExtension {
	// The two pages of this wizard
	private NewSplunkSDKProjectWizardPageOne pageOne;
	private NewJavaProjectWizardPageTwo pageTwo;
	
	public enum LoggingFramework { LOG4J, LOGBACK, JAVA_UTIL_LOGGING, NONE };
	
	// Code to maintain our extra state for the wizard.
	public class SplunkSDKProjectCreationOptions {
		public boolean supportJson;
		public boolean supportCsv;
		public LoggingFramework loggingSupport;
		public boolean generateExample;
		
		public SplunkSDKProjectCreationOptions() {
			supportJson = false;
			supportCsv = false;
			loggingSupport = LoggingFramework.NONE;
		}
	}

	private SplunkSDKProjectCreationOptions options;
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		options = new SplunkSDKProjectCreationOptions();
	}
	
	@Override
	public void addPages() {
		pageOne = new NewSplunkSDKProjectWizardPageOne(options);
		pageOne.setTitle("Create a Splunk SDK for Java project");
		pageOne.setDescription("Enter a project name.");
		addPage(pageOne);
		
		pageTwo = new NewJavaProjectWizardPageTwo(pageOne);
		pageTwo.setTitle("Project settings");
		pageTwo.setDescription("Define the project build settings.");
		addPage(pageTwo);
	}

	@Override
	public boolean performCancel() {
		pageTwo.performCancel();
		return super.performCancel();
	}
	
	@Override
	public boolean performFinish() {
		// Let the superclass performFinish run first, and then we'll make
		// our additional changes to the project if it succeeds.
		if (!super.performFinish()) {
			return false; // Java project creation failed;
		} else {
			WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException,
						InvocationTargetException, InterruptedException {
					monitor.beginTask("Creating additional files", 400);
					
					IProject project = ((IJavaProject)getCreatedElement()).getProject();
					
					ProjectTasks.addSplunkFunctionality(project, options, monitor);

					monitor.done();
				}
			};
			
			try {
				// Fetch the IWizardContainer object that is running this wizard,
				// and which accepts WorkspaceOperation objects to execute.
				getContainer().run(true, true, operation);
				IProject project = ((IJavaProject)getCreatedElement()).getProject();
				IFile javaFile = project.getFile("src/Program.java");
				if (javaFile.exists()) {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IDE.openEditor(page, javaFile);
				}
				return true;
			} catch (InvocationTargetException e) { 
				// One of the steps resulted in a core exception
				Throwable t = e.getTargetException();
				StatusManager.getManager().handle(new Status(
						Status.ERROR, 
						Activator.PLUGIN_ID, 
						"Error in configuring new Splunk SDK for Java project", 
						t), 
					StatusManager.SHOW
				);
				return false;
			} catch (InterruptedException e) {
				return false;
			} catch (PartInitException e) {
				return false;
			}

		}
	}
	
	@Override
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		pageTwo.performFinish(monitor);
	}

	@Override
	public IJavaElement getCreatedElement() {
		return pageTwo.getJavaProject();
	}
}
