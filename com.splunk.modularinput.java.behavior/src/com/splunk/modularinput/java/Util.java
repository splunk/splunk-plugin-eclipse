package com.splunk.modularinput.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class Util {	
	public static String expand(String template, Map<String,String> tokens) throws MissingTokenBindingException, UnterminatedConditionalException {
        int offset = 0;
        StringBuilder output = new StringBuilder();
        Pattern tokenRegex = Pattern.compile("(?<!\\$)\\$(([A-Za-z0-9_-]+)|if\\(([A-Za-z0-9_-]+)\\))\\$");
        Matcher tokenMatch;
        Pattern endRegex = Pattern.compile("\\$endif\\$");
        Matcher tokenEnd;

        while (offset < template.length()) {
        	
        	tokenMatch = tokenRegex.matcher(template);
        	tokenMatch = tokenMatch.region(offset, template.length());

        	if (tokenMatch.lookingAt()) {
        		String tokenName = tokenMatch.group(1);
        		if (tokenName.startsWith("if(")) {
        			String token = tokenName.substring(3, tokenName.length()-1);

        			// This is a conditional.
        			tokenEnd = endRegex.matcher(template);
        			if (tokenEnd.find(tokenMatch.end())) {
        				if (tokens.containsKey(token)) {
        					String toExpand = template.substring(tokenMatch.end(), tokenEnd.start());
        					output.append(expand(toExpand, tokens));
        				}
            			offset = tokenEnd.end();
        			} else {
        				throw new UnterminatedConditionalException(token);
        			}
        		} else {
        			// Direct token interpolation.
        			offset += tokenMatch.end() - tokenMatch.start();
        			if (!tokens.containsKey(tokenName)) {
        				throw new MissingTokenBindingException(tokenName);
        			}
        			output.append(tokens.get(tokenName));
        		}
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
	
	public static IFile expandResourceToFile(String pluginId, String resourcePath, IFile destination, Map<String, String> options)
			throws CoreException, MissingTokenBindingException, UnterminatedConditionalException {
		String template;
		try {
			template = resourceToUTF8String(pluginId, resourcePath);
		} catch (Throwable e) {
			throw new CoreException(new Status(Status.ERROR,
					pluginId, "Error reading resource.", e));
		}
		String output = expand(template, options);
		try {
			destination.create(new ByteArrayInputStream(output.getBytes("UTF-8")), false, new NullProgressMonitor());
		} catch (UnsupportedEncodingException e) {
			assert(false); // This is supposed to be impossible for UTF-8, according to the Java spec.
		}
		return destination;
	}
}
