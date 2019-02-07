package com.allenmp.rdfutils.inference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.Literal;
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

import com.allenmp.rdfutils.Prefixes;

public class OwlInferenceDemo {

    private OntModel ont = ModelFactory.createOntologyModel();

    public static void main(String[] args) {
	OwlInferenceDemo demo = new OwlInferenceDemo();
	demo.runOwlDemo();
    }

    
    public void runOwlDemo() {
	try (InputStream in = new FileInputStream(new File("owl-families-example.ttl"))) {
	    ont.read(in, null, "TTL");
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	Model instances = ModelFactory.createOntologyModel();
	Resource johnny = ont.createResource("http://www.example.org/instances#Johnny");
	Resource person = ont.createResource("http://example.com/owl/families/Person");
	Property hasSsn = ont.createProperty("http://example.com/owl/families/hasSSN");
	
	Literal ssnValue = ont.createTypedLiteral("123-45-6789");
	instances.add(johnny, hasSsn, ssnValue);
	instances.add(johnny, RDF.type, person);
	
	
	Resource otherGuy = ont.createResource("http://www.example.org/instances#OtherGuy");
	instances.add(otherGuy, hasSsn, ssnValue);
	instances.add(otherGuy, RDF.type, person);
	

	Model infModel = ModelFactory.createInfModel(ReasonerRegistry.getOWLReasoner(), ont, instances);
	infModel.setNsPrefixes(Prefixes.getNsPrefixes());

	try (Writer w = new FileWriter(new File("owl-families-output.ttl"))) {
	    infModel.write(w, "TTL");
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	StmtIterator s = infModel.listStatements(otherGuy, null, (RDFNode) null);
	while (s.hasNext()) {
	    System.out.println("Statement: " + s.next());
	}
	
	
	
	
	
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
	infModel.setNsPrefixes(Prefixes.getNsPrefixes());
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
