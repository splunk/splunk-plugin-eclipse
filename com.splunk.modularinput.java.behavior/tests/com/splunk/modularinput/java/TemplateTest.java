package com.splunk.modularinput.java;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.splunk.modularinput.java.Util;

import junit.framework.TestCase;

public class TemplateTest extends TestCase {
	@Test
    public void testEmpty() throws MissingTokenBindingException {
        assertEquals("", Util.expand("", null));
    }

	@Test
    public void testNoTokens() throws MissingTokenBindingException {
        String template = "This is a test of the emergency broadcast system.";
        assertEquals(template, Util.expand(template, null));
    }

	@Test
    public void testNoTokenWithEscapedDollarsigns() throws MissingTokenBindingException {
        String template = "Here $$ are $$ escaped $$ dollar signs.";

        String expected = "Here $ are $ escaped $ dollar signs.";
        assertEquals(expected, Util.expand(template, null));
    }
	
	@Test
    public void testUnclosedToken() throws MissingTokenBindingException {
        String template = "Here is $anunclosedtoken";
        assertEquals(template, Util.expand(template, null));
    }

	@Test
    public void testOneToken() throws MissingTokenBindingException {
        String template = "$boris$";
        Map<String, String> substitutions = new HashMap<String, String>();
        substitutions.put("boris", "hilda");

        String expected = "hilda";

        assertEquals(expected, Util.expand(template, substitutions));
    }

	@Test
    public void testOneOffsetToken() throws MissingTokenBindingException {
        String template = "a$boris$ def";
        Map<String, String> substitutions = new HashMap<String, String>();
        substitutions.put("boris", "hilda");

        String expected ="ahilda def";

        assertEquals(expected, Util.expand(template, substitutions));
    }

	@Test
    public void testMultiple() throws MissingTokenBindingException {
        String template = "a $boris$ $$ n $not a token $something$ qq";
        Map<String, String> substitutions = new HashMap<String, String>();
        substitutions.put("boris", "hilda");
        substitutions.put("something", "abcd");

        String expected = "a hilda $ n $not a token abcd qq";

        assertEquals(expected, Util.expand(template, substitutions));
    }
    
	@Test
    public void testKeyErrorOnMissingKey() {
    	String template = "$boris$";
    	Map<String, String> substitutions = new HashMap<String, String>();
    	
    	try {
    		Util.expand(template, substitutions);
    		fail("Did not raise KeyError when trying to substitute a nonexistant field.");
    	} catch (MissingTokenBindingException e) {
    		assertTrue(true);
    	}
    }
}
