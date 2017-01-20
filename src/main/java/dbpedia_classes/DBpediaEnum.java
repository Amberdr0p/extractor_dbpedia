package dbpedia_classes;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public enum DBpediaEnum {
  // Product
  // Amount
  // Event ���� ��������� ����� ����� �������� dbo:wikiPageDisambiguates
  // Function <http://dbpedia.org/ontology/PersonFunction> - ��� label
  THING_OWL("<http://www.w3.org/2002/07/owl#Thing>", "http://nerd.eurecom.fr/ontology#Thing"), // 480�
  PERSON_DBO("<http://dbpedia.org/ontology/Person>", "http://nerd.eurecom.fr/ontology#Person"), // 150�
  LOCATION_DBO("<http://dbpedia.org/ontology/Location>",
      "http://nerd.eurecom.fr/ontology#Location"), // 130�
  ORGANISATION_DBO("<http://dbpedia.org/ontology/Organisation>",
      "http://nerd.eurecom.fr/ontology#Organisation"), // 28�
  ANIMAL("<http://dbpedia.org/ontology/Animal>", "http://nerd.eurecom.fr/ontology#Animal"); // 16�

  private final String type;
  private final Resource propertyType;

  DBpediaEnum(String type, String propertyType) {
    this.type = type;
    this.propertyType = ResourceFactory.createResource(propertyType);
  }

  public String getType() {
    return type;
  }

  public Resource getPropertyType() {
    return propertyType;
  }

}
