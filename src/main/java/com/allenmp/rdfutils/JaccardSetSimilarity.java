package com.allenmp.rdfutils;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.sparql.sse.ItemTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
	
public class JaccardSetSimilarity<T> {

    private static final Logger LOG = LoggerFactory.getLogger(JaccardSetSimilarity.class);
    
    public double between(Set<T> set1, Set<T> set2) {
	if (set1.isEmpty() && set2.isEmpty()) { 
	    return 1.0;
	}
	if (set1.isEmpty() || set2.isEmpty()) {
	    return 0.0;
	}
	
	Set<T> intersection = new HashSet<>(set1);
	intersection.retainAll(set2);
	int intersectionSize = intersection.size();
	LOG.trace("Intersection: set={}", intersection);
	
	Set<T> total = new HashSet<>(set1);
	total.addAll(set2);
	int totalSize = total.size();
	LOG.trace("Total: set={}", total);
	
	double sim = ((double) intersectionSize) / totalSize;
	LOG.trace("Similarity: int={} total={} similarity={}", intersectionSize, totalSize, sim);
	return sim;
    }
    
    
    
    
}
