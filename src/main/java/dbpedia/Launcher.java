package dbpedia;


import dbpedia_classes.DBpediaClass;
import dbpedia_classes.DBpediaEnum;

import java.util.ArrayList;
import java.util.List;

public class Launcher {

  static List<DBpediaClass> list = new ArrayList<DBpediaClass>();
  
  public static void main(String[] args) {
    RDFStore store = new RDFStore();
    
    DBpediaClass.addDataToBlazegraph(store, DBpediaEnum.ANIMAL);
    
    /* Если надо пройтись по всем
    for (DBpediaEnum val : DBpediaEnum.values()) {
      DBpediaClass.addDataToBlazegraph(store, val);
    }
    */
  }

}
