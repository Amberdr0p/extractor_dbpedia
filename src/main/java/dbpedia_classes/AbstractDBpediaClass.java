package dbpedia_classes;

import dbpedia.RDFStore;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public abstract class AbstractDBpediaClass {
  private static final String query_count =
      "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
          + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
          + "select (count(?label) as ?count) "
          + "where {?res rdf:type ${type}. ?res rdfs:label ?label.FILTER (lang(?label) = 'ru')}";
  private static final String query_data = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
      + "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "select ?res ?label "
      + "where {" + "?res rdf:type ${type}." + " ?res rdfs:label ?label."
      + "FILTER (lang(?label) = 'ru') } LIMIT 10000 OFFSET ${OFFSET}";
  private static final String KEY_COUNT = "count";
  private static final String KEY_RES = "res";
  private static final String KEY_LABEL = "label";

  private static final Property rdfType =
      ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
  private static final Property rdfsLabel =
      ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#label");

  public void addDataToBlazegraph(RDFStore store) {
    int count = selectCount();
    System.out.println(count);
    for (int i = 0; i < count; i += 10000) {
      System.out.println(i);
      Model model = selectData(String.valueOf(i));
      store.save("default", model);
    }
  }
  
  private int selectCount() {
    QueryExecution qexec = qeSelect(query_count.replace("${type}", getType()));
    try {
      ResultSet res = qexec.execSelect();
      while (res != null && res.hasNext()) {
        QuerySolution qs = res.next();
        return qs.get(KEY_COUNT).asLiteral().getInt();
      }
    } finally {
      qexec.close();
    }
    return 0;
  }

  private Model selectData(String offset) {
    int i = 1;
    QueryExecution qexec =
        qeSelect(query_data.replace("${OFFSET}", offset).replace("${type}", getType()));
    try {
      ResultSet results = qexec.execSelect();
      if (results != null) {
        Model model = ModelFactory.createDefaultModel();
        while (results.hasNext()) {
          QuerySolution qs = results.next();
          String uri = qs.get(KEY_RES).asResource().getURI();
          String label = qs.get(KEY_LABEL).asLiteral().getString();
          addRecToModel(model, uri, label);
          System.out.println(String.valueOf(i++) + uri + "  " + label);
        }
        return model;
      }
    } finally {
      qexec.close();
    }
    return null;
  }

  private void addRecToModel(Model model, String uri, String label) {
    Resource uriResource = ResourceFactory.createResource(uri);
    model.add(uriResource, 
        rdfType,
        getPropertyType());
    model.add(uriResource,
        rdfsLabel,
        ResourceFactory.createPlainLiteral(label));
  }
  
   public abstract Resource getPropertyType();
   public abstract String getType();

  private QueryExecution qeSelect(String queryStr) {
    Query query = QueryFactory.create(queryStr);
    return QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
  }
}
