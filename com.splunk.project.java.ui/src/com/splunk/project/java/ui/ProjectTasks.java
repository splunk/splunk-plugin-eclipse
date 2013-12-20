package com.splunk.project.java.ui;

import java.io.File;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ClasspathEntry;

import com.splunk.project.java.ui.SplunkSDKProjectWizard.LoggingFramework;
import com.splunk.project.java.ui.SplunkSDKProjectWizard.SplunkSDKProjectCreationOptions;

public class ProjectTasks {
	public final static String splunkSDKJarFile = "splunk-sdk-java-1.2.1.jar";
	public final static String csvJarFile = "opencsv-2.3.jar";
	public final static String jsonJarFile = "gson-2.1.jar";
	
	public final static String sl4jApiJarFile = "slf4j-api-1.6.4.jar";
	public final static String splunkLoggingJarFile = "splunk-library-javalogging.jar";
	protected static final String commonsLangJarFile = "commons-lang-2.4.jar";
	
	public final static String defaultProgramFile = "Program.java";
	
	public final static String[] log4jJarFiles = {
		"log4j-1.2.16.jar",
		"slf4j-log4j12-1.6.4.jar"
	};
	public final static String log4jConfigFile = "log4j.properties";
	
	public final static String[] logbackJarFiles = {
		"logback-classic-1.0.0.jar",
		"logback-core-1.0.0.jar"
	};
	public final static String logbackConfigFile = "logback.xml";
	
	public final static String[] javaUtilJarFiles = {
		"slf4j-jdk14-1.6.4.jar"
	};
	public final static String javaUtilConfigFile = "jdklogging.properties";
	
	public static IFile addFileToProject(IProject project, String sourcePath,
			String destinationPath, IProgressMonitor monitor) throws CoreException {
		// Assemble all the paths and data for the new file. Eclipse doesn't
		// have a way to copy files from plug-ins. Instead, we open an
		// InputStream on the source and create the destination file with
		// that InputStream as its state.
		IFile destination = project.getFile(destinationPath);
		InputStream inputStream;

		try {
			inputStream = FileLocator.openStream(
					Platform.getBundle(Activator.PLUGIN_ID), 
					new Path("resources" + File.separator + sourcePath), 
					false
					);
		} catch (Throwable e) {
			throw new CoreException(new Status(Status.ERROR, 
					Activator.PLUGIN_ID, "Error opening jar for copying", e));
		}
		
		destination.create(inputStream, false, 
				new SubProgressMonitor(monitor, 100));
		
		return destination;
	}
	
	private static void addClasspathEntry(IJavaProject javaProject, IClasspathEntry entry, IProgressMonitor monitor) throws JavaModelException {
		IClasspathEntry[] oldClassPath = javaProject.getRawClasspath();
		IClasspathEntry[] newClassPath = 
				new IClasspathEntry[oldClassPath.length+1];
		System.arraycopy(oldClassPath, 0, newClassPath, 0, oldClassPath.length);
		newClassPath[oldClassPath.length] = entry;
				
		javaProject.setRawClasspath(newClassPath, 
				new SubProgressMonitor(monitor, 100));	
	}
	
	/**
	 * Copy a jar from the plugin into the lib/ directory of the project.
	 * 
	 * @param project
	 * @param jarName
	 * @param monitor
	 * @throws CoreException
	 */
	private static IFile addJarToProject(IProject project, String jarName,
			IProgressMonitor monitor) throws CoreException {
		// Ensure the lib/ folder exists in the project.
		IFolder lib = project.getFolder("lib");
		if (!lib.exists()) {
			lib.create(true, true, new SubProgressMonitor(monitor, 100));
		}

		IFile destination = 
				addFileToProject(project, 
						jarName,
						"lib" + File.separator + jarName, 
						new SubProgressMonitor(monitor, 100)
				);
		
		// Add the jar to the classpath
		IJavaProject javaProject = JavaCore.create(project);
		addClasspathEntry(
				JavaCore.create(project), 
				JavaCore.newLibraryEntry(destination.getFullPath(), null, null), 
				new NullProgressMonitor()
		);
		
		return destination;
	}
	
	public static void addSplunkFunctionality(IProject project, SplunkSDKProjectCreationOptions options, IProgressMonitor monitor) throws CoreException {
		addJarToProject(project, splunkSDKJarFile, new SubProgressMonitor(monitor, 100));
		
		if (options.supportCsv) {
			addJarToProject(project, csvJarFile, new SubProgressMonitor(monitor, 100));	
		}
		
		if (options.supportJson) {
			addJarToProject(project, jsonJarFile, new SubProgressMonitor(monitor, 100));
		}
		
		if (options.loggingSupport != LoggingFramework.NONE) {
			IJavaProject jproject = JavaCore.create(project);
			IFolder config = project.getFolder("config");
			if (!config.exists()) {
				config.create(false, true, new SubProgressMonitor(monitor, 1));
			}
			IPackageFragmentRoot srcPF = jproject.getPackageFragmentRoot(config);

			IClasspathEntry configCP = JavaCore.newSourceEntry(srcPF.getPath(), new IPath[0], srcPF.getPath());

			addClasspathEntry(
				JavaCore.create(project),
				configCP,
				new NullProgressMonitor()
			);
		}
		
		// Add logging support
		if (options.loggingSupport == LoggingFramework.LOGBACK) {
			for (String jarFile : logbackJarFiles) {
				addJarToProject(project, jarFile, new SubProgressMonitor(monitor, 100));
			}
			addFileToProject(project, 
					logbackConfigFile, 
					"config/" + logbackConfigFile,
					new SubProgressMonitor(monitor, 100)
			);
		} else if (options.loggingSupport == LoggingFramework.LOG4J) {
			for (String jarFile : log4jJarFiles) {
				addJarToProject(project, jarFile, new SubProgressMonitor(monitor, 100));
			}
			addFileToProject(project, 
					log4jConfigFile,
					"config/" + log4jConfigFile,
					new SubProgressMonitor(monitor, 100)
			);
		} else if (options.loggingSupport == LoggingFramework.JAVA_UTIL_LOGGING) {
			for (String jarFile : javaUtilJarFiles) {
				addJarToProject(project, jarFile, new SubProgressMonitor(monitor, 100));
			}
			addFileToProject(project, 
					javaUtilConfigFile, 
					"config/" + javaUtilConfigFile,
					new SubProgressMonitor(monitor, 100)
			);
		}
		
		if (options.loggingSupport != LoggingFramework.NONE) {
			addJarToProject(project, sl4jApiJarFile, new SubProgressMonitor(monitor, 100));
			addJarToProject(project, splunkLoggingJarFile, new SubProgressMonitor(monitor, 100));
			addJarToProject(project, commonsLangJarFile, new SubProgressMonitor(monitor, 100));
		}
		
		// Handle the case when the root directory of the project is the source directory.
		IJavaProject javaProject = JavaCore.create(project);
		if (javaProject.findPackageFragmentRoot(javaProject.getPath()) != null) {
			// Using the root of the project as the source directory. We must add
			// lib/ where we placed the jars to the exclusion list for the source path.
			IClasspathEntry[] entries = javaProject.getRawClasspath();
			for (int i = 0; i < entries.length; i++) {
				IClasspathEntry entry = entries[i];
				if (entry.getPath().equals(javaProject.getPath()) &&
						entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					IPath[] oldExclusions = entry.getExclusionPatterns();
					IPath[] newExclusions = new IPath[oldExclusions.length+1];
					System.arraycopy(oldExclusions, 0, newExclusions, 0, oldExclusions.length);
					newExclusions[oldExclusions.length] = project.getFolder("lib").getFullPath();
					entries[i] = JavaCore.newSourceEntry(entry.getPath(), newExclusions);
					javaProject.setRawClasspath(entries, new SubProgressMonitor(monitor, 100));
					break;
				}
			}
		}
		
		if (javaProject.findPackageFragment(javaProject.getPath()) != null) {
			// Using the root of the project as a source directory.
			addFileToProject(
					project, 
					defaultProgramFile, 
					defaultProgramFile, 
					new SubProgressMonitor(monitor, 100)
			);
		} else if (javaProject.getPackageFragmentRoot(javaProject.getPath() + File.separator + "src") != null) {
			// Use the src/ directory.
			addFileToProject(
					project,
					defaultProgramFile, 
					"src" + File.separator + defaultProgramFile, 
					new SubProgressMonitor(monitor, 100)
			);
		}
		// If neither case applies, the user has done customization,
		// is probably advanced, and doesn't need our template.
		
		monitor.done();
	}

}
