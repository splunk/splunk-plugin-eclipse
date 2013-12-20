package com.splunk.modularinput.java;

public class UnterminatedConditionalException extends Exception {
	public String tokenName;
	
	public UnterminatedConditionalException(String missingTokenName) {
		super("Unterminated conditional on token \"" + missingTokenName + "\"");
		tokenName = missingTokenName;
	}
}
