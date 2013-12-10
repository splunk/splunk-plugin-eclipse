package com.splunk.modularinput.java;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.splunk.modularinput.java.Util;
import com.splunk.modularinput.java.behavior.Activator;

public class ModularInputTasks {
	final static String newline = System.getProperty("line.separator");
	final static String appConfTemplate = "[launcher]" + newline + "author = $author$" + newline + "version = $version$" + newline +
			"description = $description$" + newline;
	
	final static String buildXmlTemplate = "";
	
	
	public static void generateAppSkeleton(IProject project, Map<String, String> options, IProgressMonitor monitor) throws CoreException {
		try {
			IFolder defaultFolder = project.getFolder("default");
			defaultFolder.create(false, true, new SubProgressMonitor(monitor, 0)); 
			
			IFile appConf = defaultFolder.getFile("app.conf");
			Util.expandResourceToFile(Activator.PLUGIN_ID, "resources/app.conf.template", appConf, options);
			
			project.getFolder("README").create(false, true,  new SubProgressMonitor(monitor, 0));
			project.getFolder("jars").create(false, true, new SubProgressMonitor(monitor, 0));
			
			String[] shimFolders = new String[] {"linux_x86", "linux_x86_64", "windows_x86", "windows_x86_64", "darwin_x86_64"};
			IFolder shimFolder;
			for (String s : shimFolders) {
				shimFolder = project.getFolder(s);
				shimFolder.create(false, true, new SubProgressMonitor(monitor, 0));
				shimFolder.getFolder("bin").create(false, true, new SubProgressMonitor(monitor, 0));
			}
			
			project.getFolder("src").create(false, true, new SubProgressMonitor(monitor, 0));
			
			IFile buildXml = project.getFile("build.xml");
			Util.expandResourceToFile(Activator.PLUGIN_ID, "resources/build.xml.template", buildXml, options);
		} catch (MissingTokenBindingException e) {
			throw new CoreException(new Status(0, Activator.PLUGIN_ID, e.getMessage(), e));
		} catch (UnterminatedConditionalException e) {
			throw new CoreException(new Status(0, Activator.PLUGIN_ID, e.getMessage(), e));
		}
	}
}
