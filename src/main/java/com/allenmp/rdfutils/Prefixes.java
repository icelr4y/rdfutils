package com.allenmp.rdfutils;

import java.util.HashMap;
import java.util.Map;

public class Prefixes {

    public static Map<String, String> getNsPrefixes() {
	Map<String, String> prefixes = new HashMap<>();
	
	prefixes.put("ex", "http://www.example.org/schema#");
	prefixes.put("inst", "http://www.example.org/instances#");
	prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
	prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
	prefixes.put("p", "http://example.com/owl/families/");
	
	return prefixes;
	
	
    }
    
}
