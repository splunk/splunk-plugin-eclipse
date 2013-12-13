package com.splunk.modularinput.java;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class ModularInputTasksTest extends TestCase {
	private static final String newline = System.getProperty("line.separator");
	final IProgressMonitor monitor = new NullProgressMonitor();
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@After
	public void tearDown() throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("delete-me");
		if (project.exists()) {
			project.delete(true, monitor);
		}
		super.tearDown();
	}
	
	@Test
	public void testFailureOnNoAuthorField() throws CoreException {
		Map<String, String> options = new HashMap<String, String>();
		options.put("version", "0.1");
		
		try {
			ModularInputTasks.generateAppSkeleton("delete-me", options, monitor);
			fail("Did not throw exception when fed insufficient token bindings.");
		} catch (CoreException e) {
			assertTrue(e.getCause() instanceof MissingTokenBindingException);
			String tokenName = ((MissingTokenBindingException)e.getCause()).tokenName;
			assertEquals("author", tokenName);
		}
	}
	
	
	
	@Test
	public void testCreation() throws CoreException, IOException {
		Map<String, String> options = new HashMap<String, String>();
		options.put("version", "0.1");
		options.put("author", "Boris the mad baboon");
		options.put("appid", "testapp");
		options.put("description", "This is a test of the emergency broadcast system.");
		options.put("is_visible", "false");
		options.put("label", "Yurk!");
		
		IProject project = ModularInputTasks.generateAppSkeleton("delete-me", options, monitor);
		
		for (String folder : new String[] { "default", "README", "jars", "linux_x86", "linux_x86_64", 
				"windows_x86", "windows_x86_64", "darwin_x86_64", "src", "build" }) {
			assertTrue(project.getFolder(folder).exists());
		}
		
		for (String folder : new String[] { "linux_x86", "linux_x86_64", "windows_x86", "windows_x86_64", "darwin_x86_64" }) {
			assertTrue(project.getFolder(folder).getFolder("bin").exists());
		}
				
		assertTrue(project.getFolder("default").getFile("app.conf").exists());
		assertTrue(Util.inputStreamToUTF8String(project.getFolder("default").getFile("app.conf").getContents()).startsWith(
				"[launcher]" + newline + 
				"author = " + options.get("author") + newline +
				"description = " + options.get("description") + newline +
				"version = " + options.get("version") + newline
		));
		
		assertTrue(project.getFile("build.xml").exists());
		assertTrue(Util.inputStreamToUTF8String(project.getFile("build.xml").getContents()).startsWith(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		));
				
	}
}
