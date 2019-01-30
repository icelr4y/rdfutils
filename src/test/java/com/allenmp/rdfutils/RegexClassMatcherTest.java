package com.allenmp.rdfutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;

import com.google.common.collect.Multimap;

public class RegexClassMatcherTest {

    @Test
    public void shouldMatchThesePhoneNumbers() {
	String[] validPhoneNumbers = {
		// Some of the expected (valid) formats
		"(987) 123-4567",
		// Even some strange separators
		"9871234567", "987-123-4567", "987/123/4567", "987.123.4567", };
	for (String number : validPhoneNumbers) {
	    assertTrue(number.matches(RegexClassMatcher.PHONE_NUMBER.pattern()));
	}
    }

    @Test
    public void shouldFailThesePhoneNumbers() {
	String[] invalidPhoneNumbers = {
		// leading country codes not allowed
		"1-987-123-4567",
		// too many/ too few digits
		"1-987-123-4567", "54-123-4567", "987-12-4567", "987-123-456",
		// Some of the disallowed separators
		"987\123\4567", "987\123\4567", "987,123,4567" };
	for (String number : invalidPhoneNumbers) {
	    assertFalse(number.matches(RegexClassMatcher.PHONE_NUMBER.pattern()));
	}
    }

    @Test
    public void shouldMatchTheseEmailAddresses() {
	// Examples from
	// https://stackoverflow.com/questions/8204680/java-regex-email/13013056#13013056
	String[] emails = { "\"Fred Bloggs\"@example.com", "Chuck Norris <gmail@chucknorris.com>", "webmaster@müller.de",
		"matteo@78.47.122.114" };
	for (String email : emails) {
	    assertTrue(email.matches(RegexClassMatcher.EMAIL_ADDRESS.pattern()));
	}
    }

    @Test
    public void shouldFailTheseEmailAddresses() {
	// Examples from
	// https://stackoverflow.com/questions/8204680/java-regex-email/13013056#13013056
	String[] invalidEmails = { "user@.invalid.com" };
	for (String email : invalidEmails) {
	    assertFalse(email.matches(RegexClassMatcher.EMAIL_ADDRESS.pattern()));
	}
    }

    @Test
    public void shouldMatchTheseIdentifiers() {
	// It's a made-up format so make it strict
	String[] validIds = { "12-ABC01-Z", "99-ZZZ99-Z", "00-AAA00-A" };
	for (String id : validIds) {
	    assertTrue(id.matches(RegexClassMatcher.OBSCURE_ID.pattern()));
	}
    }

    @Test
    public void shouldFailTheseIdentifiers() {
	String[] validIds = { "12-ABC01Z", "9-ZZZ99-Z", "00-AA00-A" };
	for (String id : validIds) {
	    assertFalse(id.matches(RegexClassMatcher.OBSCURE_ID.pattern()));
	}
    }

    /**
     * There should only be one match: :Computer and :obscureId
     */
    @Test
    public void shouldMatchObscureIdWithComputer() {
	Resource computer = ResourceFactory.createResource("http://www.example.org/schema#Computer");
	Property obscureId = ResourceFactory.createProperty("http://www.example.org/schema#obscureId");
	
	RegexClassMatcher m = new RegexClassMatcher();
	String id = "73-MGY90-B";
	Multimap<Resource, Property> results = m.potentialAssertions(id);

	assertEquals(1, results.size());
	assertTrue(results.keys().contains(computer));

	Collection<Property> predicates = results.get(computer);
	assertEquals(1, predicates.size());
	assertTrue(predicates.contains(obscureId));
    }

}
