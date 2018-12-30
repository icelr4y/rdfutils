package com.allenmp.rdfutils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;

public class RdfJaccardSimilarityTest {

    @Test
    public void testName() throws Exception {
	
	
	Model m = ModelFactory.createDefaultModel();
	Resource a = m.createResource("A");
	Resource b = m.createResource("B");
	Resource c = m.createResource("C");
	Resource d = m.createResource("D");
	
	Property p1 = m.createProperty("p1");
	Property p2 = m.createProperty("p2");
	Property p3 = m.createProperty("p3");
	Property p4 = m.createProperty("p4");
	Property p5 = m.createProperty("p5");
	Property p6 = m.createProperty("p6");
	
	m.add(m.createStatement(a, p1, "v1"));
	m.add(m.createStatement(a, p2, "v2"));
	m.add(m.createStatement(a, p3, "v3"));
	m.add(m.createStatement(a, p3, "v4"));
	m.add(m.createStatement(a, p4, "val"));
	m.add(m.createStatement(a, p5, c));
	
	m.add(m.createStatement(b, p1, "v1")); // match
	m.add(m.createStatement(b, p2, "vv"));
	m.add(m.createStatement(b, p3, "v3"));
	m.add(m.createStatement(b, p4, "val"));
	m.add(m.createStatement(b, p4, "val2"));
	m.add(m.createStatement(b, p5, c));
	m.add(m.createStatement(b, p6, d));
	
	try (OutputStream o = new FileOutputStream(new File("model.ttl"))) {
	    m.write(o, "TTL");
	}
	RdfJaccardSimilarity sim = new RdfJaccardSimilarity();
	double j = sim.between(m, a, b);
	
	assertEquals(0.444, j, 0.001);
	
    }
    
    
    
}
