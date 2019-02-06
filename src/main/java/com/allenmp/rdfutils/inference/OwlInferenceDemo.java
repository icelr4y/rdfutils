package com.allenmp.rdfutils.inference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

public class OwlInferenceDemo {

    private OntModel ont = ModelFactory.createOntologyModel();

    public static void main(String[] args) {
	OwlInferenceDemo demo = new OwlInferenceDemo();
	demo.runDemo();
    }

    public void runDemo() {

	OntClass teenager = ont.createClass("http://www.example.org/schema#Teenager");
	Property age = ont.createDatatypeProperty("http://www.example.org/schema#age");

	int minTeenAge = 12;
	int maxTeenAge = 19;

	Resource restriction = createIntegerValueBetweenRestriction(age, minTeenAge, maxTeenAge);
	ont.add(teenager, RDFS.subClassOf, restriction);

	// Infer that something is an ex:Person because it has property matching
	// SSN
	Model instances = ModelFactory.createDefaultModel();
	Resource i = ont.createResource("http://www.example.org/instances#Johnny");
	instances.add(i, age, instances.createTypedLiteral(15));

	Model infModel = ModelFactory.createInfModel(ReasonerRegistry.getOWLReasoner(), ont, instances);
	infModel.setNsPrefix("ex", "http://www.example.org/schema#");
	infModel.setNsPrefix("inst", "http://www.example.org/instances#");
	infModel.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
	infModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	infModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	infModel.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
	infModel.write(System.out, "TURTLE");

	try (Writer w = new FileWriter(new File("age-example.ttl"))) {
	    infModel.write(w, "TTL");
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	StmtIterator s = infModel.listStatements(i, null, (RDFNode) null);
	while (s.hasNext()) {
	    System.out.println("Statement: " + s.next());
	}
    }

    /**
     * Define an XSD Pattern applicable to a given predicate
     * 
     * @param predicate
     *            Datatype property for the restriction
     * @param regex
     *            regular expression (already compiled to ensure it's valid)
     * @return a blank Resource corresponding to the overall property
     *         Restriction. Still needs to be asserted in the context of an
     *         owl:Class.
     */
    private Resource createIntegerValueBetweenRestriction(Property predicate, int min, int max) {

	Resource datatypeNode = ont.createResource(); // bnode
	ont.add(datatypeNode, RDF.type, RDFS.Datatype);

	Property onDatatype = ont.createProperty("http://www.w3.org/2002/07/owl#onDatatype");
	ont.add(datatypeNode, onDatatype, XSD.integer);

	Property xsdMin = ont.createProperty("http://www.w3.org/2001/XMLSchema#minExclusive");
	Resource minRestriction = ont.createResource(); // bnode
	minRestriction.addProperty(xsdMin, ont.createTypedLiteral(min, "http://www.w3.org/2001/XMLSchema#integer"));

	Property xsdMax = ont.createProperty("http://www.w3.org/2001/XMLSchema#maxInclusive");
	Resource maxRestriction = ont.createResource(); // bnode
	maxRestriction.addProperty(xsdMax, ont.createTypedLiteral(max, "http://www.w3.org/2001/XMLSchema#integer"));

	RDFList datatypeRestrictions = ont.createList(new RDFNode[] { minRestriction, maxRestriction });
	Property withRestrictions = ont.createProperty("http://www.w3.org/2002/07/owl#withRestrictions");
	ont.add(datatypeNode, withRestrictions, datatypeRestrictions);

	SomeValuesFromRestriction restriction = ont.createSomeValuesFromRestriction(null, predicate, datatypeNode);
	return restriction;
    }

}
