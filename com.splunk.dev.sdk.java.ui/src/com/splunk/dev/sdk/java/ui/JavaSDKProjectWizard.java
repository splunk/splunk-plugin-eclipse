package com.splunk.dev.sdk.java.ui;

import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class JavaSDKProjectWizard extends Wizard implements INewWizard {
    private NewJavaProjectWizardPageOne pageOne;
    private NewJavaProjectWizardPageTwo pageTwo;
    
    
    public JavaSDKProjectWizard() {
        setWindowTitle(JavaSDKProjectWizardMessages.JavaSDKProjectWizard_WindowTitle);
    }
    
    @Override
    public void addPages() {
        super.addPages();
        
        pageOne = new NewJavaProjectWizardPageOne();
        addPage(pageOne);
        pageTwo = new NewJavaProjectWizardPageTwo(pageOne);
        addPage(pageTwo);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean performFinish() {
        // TODO Auto-generated method stub
        return true;
    }

}
