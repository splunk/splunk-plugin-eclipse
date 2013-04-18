package com.splunk.dev.sdk.java.ui;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;

public class JavaSDKProjectCreator {
	private IProgressMonitor monitor;
	
	public JavaSDKProjectCreator(IProgressMonitor monitor) {
		if (monitor == null) {
			this.monitor = new NullProgressMonitor();
		} else {
			this.monitor = monitor;
		}
	}
	
	public String[] appendToArray(String[] oldNatures, String newItem) {
		String[] newNatures = new String[oldNatures.length + 1];
		
		System.arraycopy(oldNatures, 0, newNatures, 0, oldNatures.length);
		newNatures[oldNatures.length] = newItem;
		
		return newNatures;
	}
	
	public IProject createProject(String projectName, URI projectLocationURI) throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		project.create(monitor);
		project.open(monitor);
		IProjectDescription description = project.getDescription();
		description.setLocationURI(projectLocationURI);
		String[] newNatureIds = appendToArray(description.getNatureIds(), JavaCore.NATURE_ID);
		description.setNatureIds(newNatureIds);
		project.setDescription(description, monitor);
		
		return project;
	}
	
	public void ensureProjectDeleted(IProject project) throws CoreException {
		project.delete(true, true, monitor);
	}
}
