package com.splunk.modularinput.java;
import java.io.FileNotFoundException;
import java.io.IOException;

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
		} catch (FileNotFoundException e) {
			assertTrue(true);
		}
	}
}
