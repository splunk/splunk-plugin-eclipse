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
package com.splunk.dev.sdk.java.ui;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author fross
 *
 */
@SuppressWarnings("restriction")
public class SplunkSDKProjectWizard extends NewElementWizard implements
		IExecutableExtension {
	private NewSplunkSDKProjectWizardPageOne pageOne;
	private NewJavaProjectWizardPageTwo pageTwo;
	private SplunkSDKProjectCreationOptions options;
	
	public final static String splunkSDKJarFile = "splunk-sdk-java-1.1.jar";
	public final static String csvJarFile = "opencsv-2.3.jar";
	public final static String jsonJarFile = "gson-2.1.jar";

	
	public class SplunkSDKProjectCreationOptions {
		public boolean supportJson;
		public boolean supportCsv;
		
		public SplunkSDKProjectCreationOptions() {
			supportJson = false;
			supportCsv = false;
		}
	}

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

	/**
	 * Copy a jar from the plugin into the lib/ directory of the project.
	 * 
	 * @param project
	 * @param jarName
	 * @param monitor
	 * @throws CoreException
	 */
	private void addJarToProject(IProject project, String jarName, IProgressMonitor monitor) throws CoreException {
		IFolder lib = project.getFolder("lib");
		if (!lib.exists()) {
			lib.create(true, true, new SubProgressMonitor(monitor, 100));
		}

		IFile destination = project.getFile("lib" + File.separator + jarName);
		URL url;
		InputStream inputStream;

		try {
			inputStream = FileLocator.openStream(Platform.getBundle(Activator.PLUGIN_ID), new Path(jarName), false);
		} catch (Throwable e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error opening jar for copying", e));
		}

		destination.create(inputStream, false, new SubProgressMonitor(monitor, 100));
		
		// Add the jar to the classpath
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] oldClassPath = javaProject.getRawClasspath();
		IClasspathEntry[] newClassPath = new IClasspathEntry[oldClassPath.length+1];
		System.arraycopy(oldClassPath, 0, newClassPath, 0, oldClassPath.length);
		newClassPath[oldClassPath.length] = JavaCore.newLibraryEntry(destination.getFullPath(), null, null);
		javaProject.setRawClasspath(newClassPath, new SubProgressMonitor(monitor, 100));
	}
	
	@Override
	public boolean performFinish() {
		if (!super.performFinish()) {
			return false; // Java project creation failed;
		} else {
			WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException,
						InvocationTargetException, InterruptedException {
					monitor.beginTask("Creating additional files", 400);
					
					IProject project = ((IJavaProject)getCreatedElement()).getProject();

					addJarToProject(project, splunkSDKJarFile, new SubProgressMonitor(monitor, 100));
					
					if (options.supportCsv) {
						addJarToProject(project, csvJarFile, new SubProgressMonitor(monitor, 100));	
					}
					
					if (options.supportJson) {
						addJarToProject(project, jsonJarFile, new SubProgressMonitor(monitor, 100));
					}
					
					IJavaProject javaProject = (IJavaProject)getCreatedElement();
					if (javaProject.findPackageFragmentRoot(javaProject.getPath()) != null) {
						// Using the root of the project as the source directory. We must add
						// lib/ where we placed the jars to the exclusion list for the source path.
						IClasspathEntry[] entries = javaProject.getRawClasspath();
						for (int i = 0; i < entries.length; i++) {
							IClasspathEntry entry = entries[i];
							if (entry.getPath().equals(javaProject.getPath()) &&
									entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
								IPath[] oldExclusions = entry.getExclusionPatterns();
								IPath[] newExclusions = new IPath[oldExclusions.length+1];
								System.arraycopy(oldExclusions, 0, newExclusions, 0, oldExclusions.length);
								newExclusions[oldExclusions.length] = project.getFolder("lib").getFullPath();
								entries[i] = JavaCore.newSourceEntry(entry.getPath(), newExclusions);
								javaProject.setRawClasspath(entries, new SubProgressMonitor(monitor, 100));
								break;
							}
						}
					}
							
					monitor.done();
				}
			};
			
			try {
				getContainer().run(true, true, operation);
				return true;
			} catch (InvocationTargetException e) { // One of the steps resulted in a core exception
				Throwable t = e.getTargetException();
				StatusManager.getManager().handle(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error in configuring new Splunk SDK for Java project", t), StatusManager.SHOW);
				return false;
			} catch (InterruptedException e) {
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
