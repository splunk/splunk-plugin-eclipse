package com.splunk.modularinput.java;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.FilerException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

public class Util {	
	public static String expand(String template, Map<String,String> tokens) throws MissingTokenBindingException {
        int offset = 0;
        StringBuilder output = new StringBuilder();
        Pattern tokenRegex = Pattern.compile("(?<!\\$)\\$([A-Za-z0-9_-]+)\\$(?!\\$)");
        Matcher m;

        while (offset < template.length()) {
            m = tokenRegex.matcher(template.substring(offset));

            if (m.lookingAt()) {
                String tokenName = m.group(1);
                offset += m.end() - m.start();
                if (!tokens.containsKey(tokenName)) {
                	throw new MissingTokenBindingException(tokenName);
                }
                String tokenValue = tokens.get(tokenName);
                output.append(tokens.get(tokenName));
            } else if (template.substring(offset).startsWith("$$")) {
                output.append("$");
                offset += 2;
            } else {
                output.append(template.charAt(offset));
                offset++;
            }
        }

        return output.toString();
    }
	
	public static String inputStreamToUTF8String(InputStream stream) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = stream.read(buffer)) != -1) {
			out.write(buffer, 0, length);
		}
		return new String(out.toByteArray(), "UTF-8");
	}
	
	public static String resourceToUTF8String(String pluginId, String resourcePath) throws IOException {
		InputStream inputStream;
		inputStream = resourceToInputStream(pluginId, resourcePath);
		
		return inputStreamToUTF8String(inputStream);
	}
	
	public static IFile resourceToFile(String pluginId, String resourcePath, IFile destination) throws CoreException {
		InputStream inputStream;
		try {
			inputStream = resourceToInputStream(pluginId, resourcePath);
		} catch (Throwable e) {
			throw new CoreException(new Status(Status.ERROR, 
				pluginId, "Error copying resource to file", e));
		}

		destination.create(inputStream, false, new NullProgressMonitor());
		
		return destination;
	}
	
	public static InputStream resourceToInputStream(String pluginId, String resourcePath) throws IOException {
		try {
			return FileLocator.openStream(
				Platform.getBundle(pluginId), 
				new Path(resourcePath), 
				false
			);
		} catch (ExceptionInInitializerError e) {
			throw new FileNotFoundException("Could not find resource '" + resourcePath + "' in plugin " + pluginId);
		}
	}
}
