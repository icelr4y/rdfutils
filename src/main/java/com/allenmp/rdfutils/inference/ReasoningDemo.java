package com.allenmp.rdfutils.inference;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.ReasonerFactory;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstration of using nested {@linkplain Model}s to separate statements from
 * inferences
 * 
 * @author mallen
 *
 */
public class ReasoningDemo {

    private static final Logger LOG = LoggerFactory.getLogger(ReasoningDemo.class);

    public static void main(String[] args) {

	String namespace = "NS#";

	OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
	OntClass classA = schema.createClass(namespace + "ClassA");
	OntClass classB = schema.createClass(namespace + "ClassB");
	schema.add(classA, RDFS.subClassOf, classB);

	StmtIterator schemaIter = schema.listStatements();
	while (schemaIter.hasNext()) {
	    Statement s = schemaIter.next();
	    LOG.info("Schema: {}", s);
	}

	Model data = ModelFactory.createDefaultModel();
	Resource r1 = data.createResource(namespace + "R1");
	data.add(r1, RDF.type, classA);

	StmtIterator dataIter = data.listStatements();
	while (dataIter.hasNext()) {
	    Statement s = dataIter.next();
	    LOG.info("Data: {}", s);
	}

	InfModel infModel = ModelFactory.createInfModel(ReasonerRegistry.getRDFSReasoner(), schema, data);
	infModel.setNsPrefix("ex", namespace);

	StmtIterator iter = infModel.difference(schema).listStatements();
	while (iter.hasNext()) {
	    Statement s = iter.next();
	    LOG.info("Inf: {}", s);
	}

	if (infModel.getDeductionsModel() != null) {
	    StmtIterator deductionsIter = infModel.getDeductionsModel().listStatements();
	    while (deductionsIter.hasNext()) {
		Statement s = deductionsIter.next();
		LOG.info("Deduction: {}", s);
	    }
	}

    }

}
