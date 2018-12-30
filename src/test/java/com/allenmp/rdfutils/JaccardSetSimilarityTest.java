package com.allenmp.rdfutils;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class JaccardSetSimilarityTest {

    @Test
    public void shouldReturnOneWhenBothSetsAreEmpty() throws Exception {
	Set<Integer> s1 = new HashSet<>();
	Set<Integer> s2 = new HashSet<>();

	double sim = new JaccardSetSimilarity<Integer>().between(s1, s2);
	assertEquals(1.0, sim, 0.00001);

    }

    @Test
    public void shouldReturnZeroWhenOneSetIsEmpty() throws Exception {
	Set<Integer> s1 = new HashSet<>();
	s1.add(1);
	
	Set<Integer> s2 = new HashSet<>();

	double sim = new JaccardSetSimilarity<Integer>().between(s1, s2);
	assertEquals(0.0, sim, 0.00001);
    }
    
    @Test
    public void shouldReturnZeroForDisjointSets() throws Exception {
	String[] letters1 = "a b c".split(" ");
	Set<String> s1 = new HashSet<>(Arrays.asList(letters1));
	
	String[] letters2 = "d e f".split(" ");
	Set<String> s2 = new HashSet<>(Arrays.asList(letters2));

	double sim = new JaccardSetSimilarity<String>().between(s1, s2);
	assertEquals(0.0, sim, 0.001);
    }
    
    @Test
    public void shouldCalculateCorrectResult1() throws Exception {
	String[] letters = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
	Set<String> s1 = new HashSet<>(Arrays.asList(letters));
	
	Set<String> s2 = new HashSet<>(Arrays.asList("a", "b", "c", "d", "x", "y", "z"));

	double sim = new JaccardSetSimilarity<String>().between(s1, s2);
	assertEquals(0.2692, sim, 0.001);

    }
    
    @Test
    public void shouldCalculateCorrectResult2() throws Exception {
	String[] letters1 = "a b c d e f".split(" ");
	Set<String> s1 = new HashSet<>(Arrays.asList(letters1));
	
	String[] letters2 = "d e f g h i".split(" ");
	Set<String> s2 = new HashSet<>(Arrays.asList(letters2));

	double sim = new JaccardSetSimilarity<String>().between(s1, s2);
	assertEquals(0.333, sim, 0.001);

    }
    
    
}
