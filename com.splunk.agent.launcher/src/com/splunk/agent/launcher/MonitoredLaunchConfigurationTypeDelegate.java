package com.splunk.agent.launcher;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.osgi.framework.Bundle;

public class MonitoredLaunchConfigurationTypeDelegate extends
		JavaLaunchDelegate implements
		ILaunchConfigurationDelegate {
	protected File propertiesFile = null;
	protected File jarFile = null;
		
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		// Create the agent jar
		FileOutputStream jarOut;
		try {
			jarFile = File.createTempFile("splunkagent", ".jar");
			jarFile.deleteOnExit();
			jarOut = new FileOutputStream(jarFile);
		} catch (IOException e) {
			throw new CoreException(new Status(
					Status.ERROR, 
					Activator.PLUGIN_ID, 
					"Failed to create splunkagent jar file.", 
					e
			));
		}

		InputStream jarIn;
		try {
			Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
			assert bundle != null;
			Path path = new Path("resources" + File.separator + "splunkagent.jar");
			assert path != null;
			jarIn = FileLocator.openStream(bundle, path, false);
		} catch (Throwable e) {
			try {
				jarOut.close();
			} catch (IOException e1) { /* This is basically unreachable */ }
			throw new CoreException(new Status(Status.ERROR, 
					Activator.PLUGIN_ID, "Error opening jar for copying", e));
		}
		
		BufferedInputStream bin = new BufferedInputStream(jarIn);
		BufferedOutputStream bout = new BufferedOutputStream(jarOut);
		int ch;

		try {
			while ((ch = bin.read()) != -1) {
				bout.write(ch);
			}	
			bout.flush();
		} catch (IOException e) {
			throw new CoreException(new Status(Status.ERROR, 
					Activator.PLUGIN_ID, "Error writing jar", e));
		} finally {
			try {
				bin.close();
				bout.close();
				jarIn.close();
				jarOut.close();
			} catch (IOException e1) {
				throw new CoreException(new Status(Status.ERROR, 
						Activator.PLUGIN_ID, "Error closing streams", e1));
			}
		}
		
		
		// Create the properties file
		try {
			propertiesFile = File.createTempFile("splunkagent", ".properties");
			propertiesFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(propertiesFile);
			writeConfigurationToPropertiesFile(configuration, out);
			out.close();
		} catch (IOException e) {
			throw new CoreException(new Status(
					Status.ERROR, 
					Activator.PLUGIN_ID, 
					"Failed to create temporary properties file.", 
					e
			));
		}
		
		super.launch(configuration, mode, launch, monitor);
	}
	
	@Override
	public String getVMArguments(ILaunchConfiguration configuration)
			throws CoreException {
		if (propertiesFile == null) {
			throw new CoreException(new Status(
					Status.ERROR,
					Activator.PLUGIN_ID,
					"No properties file was written for this launcher."
			));
		}
		
		String args;
		try {
			args = super.getVMArguments(configuration) + 
					" -javaagent:" + jarFile.getCanonicalPath() +
					"=\"" + propertiesFile.getCanonicalPath() + "\"" +
					// This forces JDK 7 to use the JDK 6 bytecode verifier
					// so that the splunkagent.jar will work with both.
					" -XX:-UseSplitVerifier";	
			return args;
		} catch (IOException e) {
			throw new CoreException(new Status(
					Status.ERROR,
					Activator.PLUGIN_ID,
					"Could not obtain a canonical path for the properties file.",
					e
			));
		}
	}
	
	public void writeConfigurationToPropertiesFile(ILaunchConfiguration config,
			OutputStream stream) throws CoreException {
		BufferedWriter out;
		try {
			out = new BufferedWriter(new OutputStreamWriter(stream, "UTF8"));
		} catch (UnsupportedEncodingException e) {
		    AssertionError f = new AssertionError("Your system doesn't support UTF8!");
		    f.initCause(e);
		    throw f;
		}

		try {
		out.write("agent.app.name=" + config.getName());
		out.newLine();
		out.write("splunk.transport.impl=com.splunk.javaagent.transport.SplunkTCPTransport");
		out.newLine();
		out.write("splunk.transport.tcp.host=" + config.getAttribute(SplunkLaunchData.hostAttribute, SplunkLaunchData.defaultHost));
		out.newLine();
		
		int port = config.getAttribute(SplunkLaunchData.portAttribute, SplunkLaunchData.defaultPort);
		if (port < 0) {
			throw new CoreException(new Status(
				Status.ERROR,
				Activator.PLUGIN_ID,
				"Invalid port found in launch configuration."
			));
		} else {
			out.write("splunk.transport.tcp.port=" + port);
			out.newLine();
		}
		
		out.write("splunk.transport.tcp.maxQueueSize=5MB");
		out.newLine();
		out.write("splunk.transport.tcp.dropEventsOnQueueFull=false");
		out.newLine();

		out.write("trace.blacklist=com/sun,sun/,java/,javax/,com/splunk/javaagent/");
		out.newLine();
		out.write("trace.methodEntered=true");
		out.newLine();
		out.write("trace.methodExited=true");
		out.newLine();
		out.write("trace.classLoaded=true");
		out.newLine();
		out.write("trace.errors=true");
		out.newLine();

		out.write("trace.hprof=false");
		out.newLine();
		out.write("trace.jmx=true");
		out.newLine();
		out.write("trace.jmx.configfiles=jmx");
		out.newLine();
		out.write("trace.jmx.default.frequency=20");
		out.newLine();
		
		out.flush();
		} catch (IOException e) {
			throw new CoreException(new Status(
					Status.ERROR, 
					Activator.PLUGIN_ID, 
					"Error when writing properties file", 
					e
			));
		}
	}
}
