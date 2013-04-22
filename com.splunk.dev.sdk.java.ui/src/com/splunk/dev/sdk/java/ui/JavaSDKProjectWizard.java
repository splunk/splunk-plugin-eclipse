package com.splunk.dev.sdk.java.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class JavaSDKProjectWizard extends Wizard implements INewWizard {
    private JavaSDKProjectWizardPage pageOne;
    
    public JavaSDKProjectWizard() {
        setWindowTitle(JavaSDKProjectWizardMessages.JavaSDKProjectWizard_WindowTitle);
    }
    
    @Override
    public void addPages() {
        super.addPages();
        
        pageOne = new JavaSDKProjectWizardPage("New Splunk SDK for Java Project");
        addPage(pageOne);
    }
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean performFinish() {
    	JavaSDKProjectCreator creator = new JavaSDKProjectCreator(null);
    	try {
			creator.createProject(pageOne.getProjectName(), null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return true;
    }

}
