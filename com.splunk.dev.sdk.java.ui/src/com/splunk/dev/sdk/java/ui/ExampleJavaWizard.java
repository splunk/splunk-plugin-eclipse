package com.splunk.dev.sdk.java.ui;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

@SuppressWarnings("restriction")
public class ExampleJavaWizard extends NewElementWizard implements
		IExecutableExtension {
	private NewExampleWizardPageOne pageOne;
	private NewJavaProjectWizardPageTwo pageTwo;
	private ReadmeAndChangelogModel model;
	
	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		model = new ReadmeAndChangelogModel();
	}

	@Override
	public void addPages() {
		pageOne = new NewExampleWizardPageOne(model);
		pageOne.setTitle("Title on page one");
		pageOne.setDescription("Description of what happens on page one.");
		addPage(pageOne);
		
		pageTwo = new NewJavaProjectWizardPageTwo(pageOne);
		pageTwo.setTitle("Title on page two");
		pageTwo.setDescription("Description of what happens on page two.");
		addPage(pageTwo);
	}
	
	@Override
	public boolean performCancel() {
		pageTwo.performCancel();
		return super.performCancel();
	}

	@Override
	public boolean performFinish() {
		if (super.performFinish()) { // Java project creation succeeded
			WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException,
						InvocationTargetException, InterruptedException {
					monitor.beginTask("Creating additional files", 1000);
					
					IProject project = ((IJavaProject)getCreatedElement()).getProject();
					
					// Create README
					if (model.createReadme) {
						IFile readme = project.getFile("README");
						URL url;
						try {
							url = new URL("platform:/plugin/com.splunk.dev.sdk.java.ui/resources/README");
							InputStream inputStream = url.openConnection().getInputStream();
							readme.create(inputStream, false, new SubProgressMonitor(monitor, 500));
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					// Create CHANGELOG
					if (model.createChangelog) {
						IFile changelog = project.getFile("CHANGELOG");
						URL url;
						try {
							url = new URL("platform:/plugin/com.splunk.dev.sdk.java.ui/resources/CHANGELOG");
							InputStream inputStream = url.openConnection().getInputStream();
							changelog.create(inputStream, false, new SubProgressMonitor(monitor, 500));
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					monitor.done();
				}
			};
			
			try {
				getContainer().run(true, true, operation);
			} catch (InterruptedException e) {
				return false;
			} catch (InvocationTargetException e) { // One of the steps resulted in a core exception
				Throwable t = e.getTargetException();
				MessageDialog.openError(getShell(), e.getLocalizedMessage(), "Error in configuring new Java project");
				return false;
			}

			return true;
		} else {
			return false;
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
