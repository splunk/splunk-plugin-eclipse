package com.splunk.modularinput.java;

public class MissingTokenBindingException extends Exception {
	public String tokenName;
	
	public MissingTokenBindingException(String missingTokenName) {
		super("No value found to interpolate for token \"" + missingTokenName + "\"");
		tokenName = missingTokenName;
	}
}