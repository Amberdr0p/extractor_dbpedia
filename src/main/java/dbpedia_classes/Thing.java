package dbpedia_classes;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class Thing extends AbstractDBpediaClass {

  /*
   * взяли только класс owl:Thing (<http://www.w3.org/2002/07/owl#Thing>)
   */
 
  private static final Resource typeProperty =
      ResourceFactory.createResource("http://nerd.eurecom.fr/ontology#Thing");
  private static final String type = "<http://www.w3.org/2002/07/owl#Thing>";
  
  @Override
  public String getType() {
    return type;
  }
  
  @Override
  public Resource getPropertyType() {
    return typeProperty;
  }
  
}
