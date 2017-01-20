package dbpedia;


import dbpedia_classes.AbstractDBpediaClass;
import dbpedia_classes.Organisation;

import java.util.ArrayList;
import java.util.List;

public class Launcher {

  static List<AbstractDBpediaClass> list = new ArrayList<AbstractDBpediaClass>();
  
  public static void main(String[] args) {
    RDFStore store = new RDFStore();
    
    // list.add(new Location());
    // list.add(new Thing());
    // list.add(new Person());
    list.add(new Organisation());
    
    
    for(AbstractDBpediaClass adbc : list) {
      adbc.addDataToBlazegraph(store);
    }
  }

}
