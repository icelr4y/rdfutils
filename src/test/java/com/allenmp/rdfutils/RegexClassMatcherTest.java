package com.allenmp.rdfutils;

import static org.junit.Assert.*;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.junit.Test;

import com.google.common.collect.Multimap;

public class RegexClassMatcherTest {

    @Test
    public void shouldMatchThesePhoneNumbers() throws Exception {
	String[] validPhoneNumbers = {
		// Some of the expected (valid) formats
		"(540) 123-4567",
		// Even some strange separators
		"5401234567", "540-123-4567", "540/123/4567", "540.123.4567", };
	for (String number : validPhoneNumbers) {
	    assertTrue(number.matches(RegexClassMatcher.PHONE_NUMBER.pattern()));
	}
    }

    @Test
    public void shouldFailThesePhoneNumbers() throws Exception {
	String[] invalidPhoneNumbers = {
		// leading country codes not allowed
		"1-540-123-4567",
		// too many/ too few digits
		"1-540-123-4567", "54-123-4567", "540-12-4567", "540-123-456",
		// Some of the disallowed separators
		"540\123\4567", "540\123\4567", "540,123,4567" };
	for (String number : invalidPhoneNumbers) {
	    assertFalse(number.matches(RegexClassMatcher.PHONE_NUMBER.pattern()));
	}
    }

    @Test
    public void shouldMatchTheseEmailAddresses() throws Exception {
	// Examples from
	// https://stackoverflow.com/questions/8204680/java-regex-email/13013056#13013056
	String[] emails = { "\"Fred Bloggs\"@example.com", "Chuck Norris <gmail@chucknorris.com>", "webmaster@müller.de",
		"matteo@78.47.122.114" };
	for (String email : emails) {
	    assertTrue(email.matches(RegexClassMatcher.EMAIL_ADDRESS.pattern()));
	}
    }

    @Test
    public void shouldFailTheseEmailAddresses() throws Exception {
	// Examples from
	// https://stackoverflow.com/questions/8204680/java-regex-email/13013056#13013056
	String[] invalidEmails = { "user@.invalid.com" };
	for (String email : invalidEmails) {
	    assertFalse(email.matches(RegexClassMatcher.EMAIL_ADDRESS.pattern()));
	}
    }

    @Test
    public void shouldMatchTheseIdentifiers() throws Exception {
	// It's a made-up format so make it strict
	String[] validIds = { "12-ABC01-Z", "99-ZZZ99-Z", "00-AAA00-A"};
	for (String id : validIds) {
	    assertTrue(id.matches(RegexClassMatcher.OBSCURE_ID.pattern()));
	}
    }
    
    @Test
    public void shouldFailTheseIdentifiers() throws Exception {
	String[] validIds = { "12-ABC01Z", "9-ZZZ99-Z", "00-AA00-A"};
	for (String id : validIds) {
	    assertFalse(id.matches(RegexClassMatcher.OBSCURE_ID.pattern()));
	}
    }
    
    
    @Test
    public void testName() throws Exception {
	String id = "73-MGY90-B";
	
	
	RegexClassMatcher matcher = new RegexClassMatcher();
	Multimap<OntClass, OntProperty> results = matcher.potentialAssertions(id);
	
	
	
	
	
	
	
	
    }
    
    
    
}
