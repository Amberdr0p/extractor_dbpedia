package dbpedia;

import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.apache.jena.atlas.web.auth.SimpleAuthenticator;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.subjects.PublishSubject;


public class RDFStore {
  
  private static final String TRIPLESTORE_STORE_URL = "http://winghouse.semiot.ru:3030/blazegraph/sparql";
  private static final String TRIPLESTORE_USERNAME = "admin";
  private static final String TRIPLESTORE_PASSWORD = "pw";
  // private static final String TRIPLESTORE_STORE_URL = "";
  
  private static final Logger logger = LoggerFactory.getLogger(RDFStore.class);
  private final HttpAuthenticator httpAuthenticator;
  private final PublishSubject<Model> ps = PublishSubject.create();

  public RDFStore() {
    httpAuthenticator = new SimpleAuthenticator(
        TRIPLESTORE_USERNAME,
        TRIPLESTORE_PASSWORD.toCharArray());

    /* ps.buffer(
        configuration.getAsInteger(Keys.STORE_OPERATION_BUFFERIDLE), TimeUnit.SECONDS,
        configuration.getAsInteger(Keys.STORE_OPERATION_BUFFERSIZE))
        .subscribe(new BatchUploader()); */
  }

  public void save(Model model) {
    ps.onNext(model);
  }

  public void save(String graphUri, Model model) {
    DatasetAccessorFactory
        .createHTTP(TRIPLESTORE_STORE_URL, httpAuthenticator)
        .add(graphUri, model);
  }

  public ResultSet select(String query) {
    return select(QueryFactory.create(query));
  }

  public ResultSet select(Query query) {
    Query select = QueryFactory.create(query);
    ResultSet rs = QueryExecutionFactory
        .createServiceRequest(
            TRIPLESTORE_STORE_URL, // TRIPLESTORE_QUERY_URL,
            select,
            httpAuthenticator)
        .execSelect();
    return rs;
  }

  public void update(String update) {
    UpdateRequest updateRequest = UpdateFactory.create(update);

    UpdateExecutionFactory.createRemote(
        updateRequest,
        TRIPLESTORE_STORE_URL, httpAuthenticator)
        .execute();
  }

  /* private class BatchUploader implements Observer<List<Model>> {

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
      logger.error(e.getMessage(), e);
    }

    @Override
    public void onNext(List<Model> models) {
      if (!models.isEmpty()) {
        long start = System.currentTimeMillis();
        Model buffer = ModelFactory.createDefaultModel();
        models.forEach(buffer::add);
        long end = System.currentTimeMillis();
        logger.info("Buffered {} models in {} ms", models.size(), end - start);

        start = System.currentTimeMillis();
        DatasetAccessorFactory
            .createHTTP(configuration.getAsString(Keys.TRIPLESTORE_STORE_URL), httpAuthenticator)
            .add(buffer);
        end = System.currentTimeMillis();

        logger.info("Uploaded {} models in {} ms", models.size(), end - start);
      }
    }
  } */
}
