package uk.org.taverna.scufl2.rdf.core;

import org.openrdf.elmo.annotations.rdf;

import uk.org.taverna.scufl2.rdf.common.Ontology;
import uk.org.taverna.scufl2.rdf.common.WorkflowBean;


@rdf(Ontology.CORE + "IterationStrategy")
public interface IterationStrategy extends WorkflowBean {


}
