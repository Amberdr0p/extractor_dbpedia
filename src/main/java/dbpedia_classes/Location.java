package dbpedia_classes;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class Location extends AbstractDBpediaClass {

  /*
   * взяли только класс dbo:Location (<http://dbpedia.org/ontology/Location>)
   * 130к
   */
  
  private static final Resource typeProperty =
      ResourceFactory.createResource("http://nerd.eurecom.fr/ontology#Location");
  private static final String type = "<http://dbpedia.org/ontology/Location>";
  
  @Override
  public String getType() {
    return type;
  }
  
  @Override
  public Resource getPropertyType() {
    return typeProperty;
  }

}
