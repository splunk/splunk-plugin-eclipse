package com.splunk.modularinput.java;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;

import com.splunk.modularinput.java.Util;
import com.splunk.modularinput.java.behavior.Activator;
import com.splunk.project.java.ui.SplunkSDKProjectWizard;

public class ModularInputTasks {
	final static String newline = System.getProperty("line.separator");
	final static String appConfTemplate = "[launcher]" + newline + "author = $author$" + newline + "version = $version$" + newline +
			"description = $description$" + newline;
	
	final static String buildXmlTemplate = "";
	
	final static String srcDir = "src";
	final static String buildDir = "build";
	
	public static IProject generateAppSkeleton(String name, Map<String, String> options, IProgressMonitor monitor) throws CoreException {
		// Create the project
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(name);
		if (!project.exists()) {
			project.create(monitor);
		} else {
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}

		if (!project.isOpen()) {
			project.open(monitor);
		}

		// Add Java nature to the project.
		if (!project.hasNature(JavaCore.NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = JavaCore.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		}

		IJavaProject jproject = JavaCore.create(project);

		IClasspathEntry[] classpath = new IClasspathEntry[3];

		// Add src and build directories.
		IFolder src = project.getFolder(srcDir);
		if (!src.exists()) {
			src.create(false, true, new SubProgressMonitor(monitor, 1));
		}
		IPackageFragmentRoot srcPF = jproject.getPackageFragmentRoot(src);

		IFolder build = project.getFolder(buildDir);
		if (!build.exists()) {
			build.create(false, true, new SubProgressMonitor(monitor, 1));
		}

		classpath[0] = JavaCore.newSourceEntry(srcPF.getPath(), new IPath[0], build.getFullPath());


		// Add Java runtime variables.
		classpath[1] = JavaCore.newVariableEntry(
				new Path(JavaRuntime.JRELIB_VARIABLE), 
				new Path(JavaRuntime.JRESRC_VARIABLE), 
				new Path(JavaRuntime.JRESRCROOT_VARIABLE)
		);

		try {			
			IFolder defaultFolder = project.getFolder("default");
			defaultFolder.create(false, true, new SubProgressMonitor(monitor, 0)); 
			
			IFile appConf = defaultFolder.getFile("app.conf");
			Util.expandResourceToFile(Activator.PLUGIN_ID, "resources/app.conf.template", appConf, options);
			
			IFolder readmeFolder = project.getFolder("README");
			readmeFolder.create(false, true,  new SubProgressMonitor(monitor, 0));
			IFile inputConf = readmeFolder.getFile("inputs.conf.spec"); 
			Util.expandResourceToFile(Activator.PLUGIN_ID, "resources/inputs.conf.spec.template", inputConf, options);
			
			project.getFolder("jars").create(false, true, new SubProgressMonitor(monitor, 0));
			
			String[] shimFolders = new String[] {"linux_x86", "linux_x86_64", "windows_x86", "windows_x86_64", "darwin_x86_64"};
			IFolder shimFolder, bin;
			for (String s : shimFolders) {
				shimFolder = project.getFolder(s);
				shimFolder.create(false, true, new SubProgressMonitor(monitor, 0));
				bin = shimFolder.getFolder("bin");
				bin.create(false, true, new SubProgressMonitor(monitor, 0));
			}
			String appid = options.get("appid");
			Util.resourceToFile(Activator.PLUGIN_ID, "resources/shims/shim-darwin.sh", project.getFile("darwin_x86_64/bin/" + appid + ".sh"));
			Util.resourceToFile(Activator.PLUGIN_ID, "resources/shims/shim-linux.sh", project.getFile("linux_x86/bin/" + appid + ".sh"));
			Util.resourceToFile(Activator.PLUGIN_ID, "resources/shims/shim-linux.sh", project.getFile("linux_x86_64/bin/" + appid + ".sh"));
			Util.resourceToFile(Activator.PLUGIN_ID, "resources/shims/shim-windows_x86_64.exe", project.getFile("windows_x86_64/bin/" + appid + ".exe"));
			Util.resourceToFile(Activator.PLUGIN_ID, "resources/shims/shim-windows_x86.exe", project.getFile("windows_x86/bin/" + appid + ".exe"));
			
			IFolder lib = project.getFolder("lib");
			lib.create(false, true, new SubProgressMonitor(monitor, 1));
			IFile splunkSDK = Util.resourceToFile(
					com.splunk.project.java.ui.Activator.PLUGIN_ID,
					"resources/" + SplunkSDKProjectWizard.splunkSDKJarFile,
					lib.getFile(SplunkSDKProjectWizard.splunkSDKJarFile)
			);
			classpath[2] = JavaCore.newLibraryEntry(splunkSDK.getFullPath(), null, null);
			
			jproject.setRawClasspath(classpath, new SubProgressMonitor(monitor, 1));
			
			IFile buildXml = project.getFile("build.xml");
			Util.expandResourceToFile(Activator.PLUGIN_ID, "resources/build.xml.template", buildXml, options);
			
			IFile mainJava = src.getFile("Main.java");
			String templateName;
			if (options.containsKey("working_template")) {
				templateName = "resources/Main.java.template";
			} else {
				templateName = "resources/Skeleton.java.template";
			}
			Util.expandResourceToFile(Activator.PLUGIN_ID, templateName, mainJava, options);

		} catch (MissingTokenBindingException e) {
			throw new CoreException(new Status(0, Activator.PLUGIN_ID, e.getMessage(), e));
		} catch (UnterminatedConditionalException e) {
			throw new CoreException(new Status(0, Activator.PLUGIN_ID, e.getMessage(), e));
		}
		
		return project;
	}
}
