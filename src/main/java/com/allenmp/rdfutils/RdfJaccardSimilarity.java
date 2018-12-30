package com.allenmp.rdfutils;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RdfJaccardSimilarity {
    
    private static final Logger LOG = LoggerFactory.getLogger(RdfJaccardSimilarity.class);
    
    
    public double between(Model model, Resource r1, Resource r2) { 
	if (!model.containsResource(r1)) { 
	    throw new IllegalArgumentException();
	}
	if (!model.containsResource(r2)) { 
	    throw new IllegalArgumentException();
	}
	
	StmtIterator i1 = model.listStatements(r1, null, (RDFNode) null);
	Set<String> set1 = makeSet(i1);
	
	StmtIterator i2 = model.listStatements(r2, null, (RDFNode) null);
	Set<String> set2 = makeSet(i2);
	
	JaccardSetSimilarity<String> sim = new JaccardSetSimilarity<>();
	return sim.between(set1, set2);
	
    }
    
    
    private static Set<String> makeSet(StmtIterator iter) { 
	Set<String> set = new HashSet<>();
	while (iter.hasNext()) {
	    Statement st = iter.nextStatement();
	    String pred = st.getPredicate().getURI();
	    String obj = st.getObject().isResource() ? st.getObject().asResource().getURI() : st.getObject().asLiteral().toString();
	    String entry = pred + ":" + obj;
	    LOG.trace("Entry: entry={}", entry);
	    set.add(entry);
	}
	return set;
    }
    
    
}
