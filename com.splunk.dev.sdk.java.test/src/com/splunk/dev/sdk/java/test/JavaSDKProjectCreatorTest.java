package com.splunk.dev.sdk.java.test;
import static org.junit.Assert.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.splunk.dev.sdk.java.ui.JavaSDKProjectCreator;


public class JavaSDKProjectCreatorTest {
	private static final String PROJECT_NAME = "test project";
	public JavaSDKProjectCreator creator;
	public IProject project;
	
	@Before
	public void setUp() {
		IProgressMonitor monitor = new NullProgressMonitor();
		creator = new JavaSDKProjectCreator(monitor);
	}
	
	@After
	public void tearDown() throws CoreException {
		if (project != null) {
			creator.ensureProjectDeleted(project);
		}
	}
	
	@Test
	public void testEnsureProjectExists() throws CoreException {
		project = creator.createProject(PROJECT_NAME, null);
		
		String[] expectedNatureIds = {JavaCore.NATURE_ID};
		String[] foundNatureIds = project.getDescription().getNatureIds();
		assertEquals(expectedNatureIds.length, foundNatureIds.length);
		for (int i = 0; i < expectedNatureIds.length; i++) {
			assertEquals(expectedNatureIds[i], foundNatureIds[i]);
		}
		
		
	}
	
}
