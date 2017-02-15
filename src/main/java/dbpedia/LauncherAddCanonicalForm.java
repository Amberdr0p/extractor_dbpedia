package dbpedia;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import ru.stachek66.nlp.mystem.holding.Factory;
import ru.stachek66.nlp.mystem.holding.MyStem;
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException;
import ru.stachek66.nlp.mystem.holding.Request;
import ru.stachek66.nlp.mystem.model.Info;
import scala.Option;
import scala.collection.JavaConversions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class LauncherAddCanonicalForm {

  private static final String QUERY_SELECT_COUNT =
      "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
          + "select (count(?label) as ?count) where {?uri rdfs:label ?label}";
  private static final String QUERY_SELECT_DATA =
      "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
          + "select ?uri ?label where {?uri rdfs:label ?label} "
          + "ORDER BY ?uri LIMIT ${LIMIT} OFFSET ${OFFSET}";
  private static final Pattern p = Pattern
      .compile("^[À-ÿ¨¸\\d\\s\\.\\,\\-\\\\\\/\\¹\\'\\’\\!\\+\\&\\—\\«\\»\\\"\\`\\:\\;]*[À-ÿ¨¸]+"
          + "[À-ÿ¨¸\\d\\s\\.\\,\\-\\\\\\/\\¹\\'\\’\\!\\+\\&\\—\\«\\»\\\"\\`\\:\\;]*$");
  private static final String REGEX_ROMAN_NUMERALS =
      "(M{0,3})(D?C{0,3}|C[DM])(L?X{0,3}|X[LC])(V?I{0,3}|I[VX])";
  private static final String REGEX_BRACKETS = "\\(.*\\)";

  private final static MyStem mystemAnalyzer =
      new Factory("-ld --format json").newMyStem("3.0", Option.<File>empty()).get();

  private static final Property customLemma =
      ResourceFactory.createProperty("http://www.custom-ontology.org/ner#lemma");

  private static final String KEY_COUNT = "count";
  private static final String KEY_URI = "uri";
  private static final String KEY_LABEL = "label";

  private static final Option<String> nullOption = scala.Option.apply(null);

  private static final int SHIFT = 20000;

  public static void main(String[] args) throws IOException {
    /*
     * String label = "Ãåíðèõ III (ãåðöîã Êàðèíòèè)"; String cutLabel =
     * label.replaceAll(REGEX_BRACKETS, "").replaceAll(REGEX_ROMAN_NUMERALS, "");
     * System.out.println(cutLabel);
     */
    FileWriter writer = new FileWriter("list.txt", false);

    RDFStore store = new RDFStore();
    int count = selectCount(store);
    for (int i = 0; i < count; i += SHIFT) {
      ResultSet res = store.select(QUERY_SELECT_DATA.replace("${LIMIT}", String.valueOf(SHIFT))
          .replace("${OFFSET}", String.valueOf(i)));
      if (res != null) {
        Model modelLemm = GenLemm(res, writer);
        // store.save("default", modelLemm);
      }
    }
    // System.out.println("gg");
  }

  private static Model GenLemm(ResultSet results, FileWriter writer) throws IOException {
    Model model = ModelFactory.createDefaultModel();
    while (results.hasNext()) {
      QuerySolution qs = results.next();
      String uri = qs.get(KEY_URI).asResource().getURI();
      String label = qs.get(KEY_LABEL).asLiteral().getString();

      // System.out.print("\r\n" + label);
      processingLabel(label, uri, model, writer);
    }
    return model;
  }

  private static void processingLabel(String label, String uri, Model model, FileWriter writer)
      throws IOException {
    String cutLabel = label.replaceAll(REGEX_BRACKETS, "").replaceAll(REGEX_ROMAN_NUMERALS, "");
    if (matchLabel(cutLabel)) {
      System.out.print("\r\nAdded " + uri + " " + cutLabel + " with lemma: ");
      Iterable<Info> result;
      try {
        result = JavaConversions
            .asJavaIterable(mystemAnalyzer.analyze(Request.apply(cutLabel)).info().toIterable());
        for (final Info info : result) {
          Option<String> lex = info.lex();
          if (lex != null && lex != nullOption) {
            addLemmToModel(model, uri, lex.get());
            System.out.print(info.lex().get() + " ");
          }
        }
      } catch (MyStemApplicationException e) {
        e.printStackTrace();
      }
    } else {
      writer.write(uri + " " + label + "\r\n");
      // System.out.println(uri + " " + label);
    }
  }

  private static boolean matchLabel(String label) {
    return p.matcher(label).matches();
  }

  private static void addLemmToModel(Model model, String uri, String lemma) {
    Resource uriResource = ResourceFactory.createResource(uri);
    model.add(uriResource, customLemma, ResourceFactory.createPlainLiteral(lemma));
  }

  private static int selectCount(RDFStore store) {
    ResultSet res = store.select(QUERY_SELECT_COUNT);
    while (res != null && res.hasNext()) {
      QuerySolution qs = res.next();
      return qs.get(KEY_COUNT).asLiteral().getInt();
    }

    return 0;
  }

}
