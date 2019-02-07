package com.allenmp.rdfutils.inference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import org.apache.jena.ontology.OntClass;
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
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import com.allenmp.rdfutils.Prefixes;

/**
 * Example of programmatically defining regular expression patterns.
 * 
 * <pre>
 * [] rdf:type owl:Restriction ; 
 *     owl:onProperty {{predicate}} ;
 *     owl:someValuesFrom [
 *         rdf:type rdfs:Datatype ;
 *         owl:onDatatype xsd:string ;
 *         owl:withRestrictions (
 *             [xsd:pattern "{{regexPattern}}"^^xsd:string]
 *         ) ;
 *    ] ;
 * .
 * </pre>
 */
public class RegexPatternDemo {

    private OntModel ont = ModelFactory.createOntologyModel();

    public static void main(String[] args) {
	RegexPatternDemo demo = new RegexPatternDemo();
	demo.runDemo();
    }

    public void runDemo() {

	// Define that a Person is something that has an SSN with string
	// matching a certain pattern
	OntClass person = ont.createClass("http://www.example.org/schema#Person");
	ont.add(person, RDFS.subClassOf, OWL.Thing);
	Property ssn = ont.createDatatypeProperty("http://www.example.org/schema#socialSecurityNumber");
	String ssnPattern = "^\\d{3}-\\d{2}-\\d{4}$";
	
	Resource restriction = createPatternRestriction(ssn, Pattern.compile(ssnPattern));
	ont.add(person, RDFS.subClassOf, restriction);
	
	Property hasKey = ont.createProperty("http://www.w3.org/2002/07/owl#hasKey");
	ont.add(OWL.Thing, hasKey, ont.createList(new RDFNode[] { ssn }));
//	ont.add(OWL.Thing, hasKey, ssn);

	ont.write(System.out, "TTL");

	// Infer that something is an ex:Person because it has property matching
	// SSN
	Model instances = ModelFactory.createDefaultModel();
	Resource johnny = ont.createResource("http://www.example.org/instances#Johnny");
	instances.add(johnny, RDF.type, person);
	Literal ssnValue = ont.createLiteral("111-11-1111");
	instances.add(johnny, ssn, ssnValue);

	
	Resource person2 = ont.createResource("http://www.example.org/instances#Person2");
	instances.add(person2, RDF.type, person);
	instances.add(person2, ssn, ssnValue);
	
	
	Model infModel = ModelFactory.createInfModel(ReasonerRegistry.getOWLReasoner(), ont, instances);
	infModel.setNsPrefixes(Prefixes.getNsPrefixes());
	infModel.write(System.out, "TURTLE");
	
	try (Writer w = new FileWriter(new File("pattern-example.ttl"))) {
	    infModel.write(w, "TURTLE");
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	
	StmtIterator s = infModel.listStatements(johnny, null, (RDFNode) null);
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
    private Resource createPatternRestriction(Property predicate, Pattern regex) {

	Resource datatype = ont.createResource(); // bnode
	ont.add(datatype, RDF.type, RDFS.Datatype);

	Property onDatatype = ont.createProperty("http://www.w3.org/2002/07/owl#onDatatype");
	ont.add(datatype, onDatatype, XSD.xstring);

	Resource patternSpec = ont.createResource(); // bnode
	Property xsdPattern = ont.createProperty("http://www.w3.org/2001/XMLSchema#pattern");
	patternSpec.addProperty(xsdPattern, regex.pattern());

	RDFList datatypeRestrictions = ont.createList(new RDFNode[] { patternSpec });
	Property withRestrictions = ont.createProperty("http://www.w3.org/2002/07/owl#withRestrictions");
	ont.add(datatype, withRestrictions, datatypeRestrictions);

	SomeValuesFromRestriction restriction = ont.createSomeValuesFromRestriction(null, predicate, datatype);
	return restriction;
    }

}
