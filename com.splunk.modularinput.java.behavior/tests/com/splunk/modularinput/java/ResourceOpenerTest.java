package com.splunk.modularinput.java;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import com.splunk.modularinput.java.behavior.Activator;

import junit.framework.TestCase;


public class ResourceOpenerTest extends TestCase {
	@Test
	public void testResourceToString() throws IOException {
		String pluginId = Activator.PLUGIN_ID;
		String resourcePath = "resources/app.conf.template";
		
		String contents = Util.resourceToUTF8String(pluginId, resourcePath);
		
		assertTrue(contents.startsWith("[launcher]"));
	}
	
	@Test
	public void testNonexistantResourceToString() throws IOException {
		String pluginId = Activator.PLUGIN_ID;
		String resourcePath = "i/dont/exist";
		
		try {
			Util.resourceToUTF8String(pluginId, resourcePath);
			fail("Didn't get exception.");
		} catch (IOException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testResourceToFile() throws CoreException, IOException {
		IProgressMonitor monitor = new NullProgressMonitor();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject("project");
		project.create(monitor);
		project.open(monitor);
		
		try {
			IFile file = Util.resourceToFile(Activator.PLUGIN_ID, "resources/test.template", project.getFile("app.conf"));
			
			InputStream stream = file.getContents();
			String contents = Util.inputStreamToUTF8String(stream);
			assertEquals("$boris$ and $hilda$.", contents);
		} finally {
			project.delete(true, monitor);
		}
	}
	
	@Test
	public void testExpandResourceToFile() throws CoreException, IOException, MissingTokenBindingException, UnterminatedConditionalException {
		IProgressMonitor monitor = new NullProgressMonitor();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject("project");
		project.create(monitor);
		project.open(monitor);
		
		Map<String, String> options = new HashMap<String, String>();
		options.put("boris", "meep");
		options.put("hilda", "mope");
		
		try {
			IFile file = Util.expandResourceToFile(Activator.PLUGIN_ID, "resources/test.template", project.getFile("app.conf"), options);
			
			InputStream stream = file.getContents();
			String contents = Util.inputStreamToUTF8String(stream);
			assertEquals("meep and mope.", contents);
		} finally {
			project.delete(true, monitor);
		}
	}
}
