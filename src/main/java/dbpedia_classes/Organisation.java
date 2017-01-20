package dbpedia_classes;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class Organisation extends AbstractDBpediaClass {
  /*
   * взяли только класс dbo:Organisation (<http://dbpedia.org/ontology/Organisation>)
   * 28к
   */
 
  private static final Resource typeProperty =
      ResourceFactory.createResource("http://nerd.eurecom.fr/ontology#Organisation");
  private static final String type = "<http://dbpedia.org/ontology/Organisation>";
  
  @Override
  public String getType() {
    return type;
  }
  
  @Override
  public Resource getPropertyType() {
    return typeProperty;
  }
}
