package dbpedia_classes;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class Person extends AbstractDBpediaClass {
  /*
   * ����� ������ ����� dbo:Person (<http://dbpedia.org/ontology/Person>)
   * �� ��������� � foaf:Person 150�
   */
 
  private static final Resource typeProperty =
      ResourceFactory.createResource("http://nerd.eurecom.fr/ontology#Person");
  private static final String type = "<http://dbpedia.org/ontology/Person>";
  
  @Override
  public String getType() {
    return type;
  }
  
  @Override
  public Resource getPropertyType() {
    return typeProperty;
  }
}
