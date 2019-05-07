package client.explain;


import com.google.gson.Gson;
import com.google.inject.Singleton;
import extendedsldnf.datastructure.IQueryExplanations;
import extendedsldnf.datastructure.QueryExplanations;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import utils.json.CustomGson;
import web.data.Query;


import java.io.*;

@Singleton
public class ExplanationExtractorClient {


//     static String URL;
//    final static String URL = "http://localhost:9350";

    String url;


    public static ExplanationExtractorClient instance; //= new ExplanationExtractorClient();
//    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private ExplanationExtractorClient() {
        this.url= "http://sedna.mpi-inf.mpg.de:9350";

    }

    private ExplanationExtractorClient(String url){
        this.url=url;
    }


    public IQueryExplanations getExplanations(Query query) throws IOException {
        Gson gson = CustomGson.getInstance().getGson();
        String queryJson = gson.toJson(query);
//

        QueryExplanations queryExplanations = null;
        HttpPost p = new HttpPost(url);

        p.setEntity(new StringEntity(queryJson,
                ContentType.create("application/json")));

        HttpClient c = HttpClients.createDefault();

//        System.out.println(p.getEntity().getContent().toString());

        HttpResponse response = c.execute(p);
        if (response.getStatusLine().getStatusCode() == 200) {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                queryExplanations = gson.fromJson(br, QueryExplanations.class);
//                        System.out.println(queryExplanations);
            }

        }

        return queryExplanations;

    }


    public static ExplanationExtractorClient getInstance (String url) {
        if (instance==null)
            instance=new ExplanationExtractorClient(url);
        return instance;
//                return instance;
    }

    public static void main (String[]args) throws IOException {
        Query exampleQ = new Query("<Albert_Einstein>", "<wasBornIn>", "<Germany>", "wasBornIn(?x,?y):- birthPlace(?x,?z), in(?z,?y).\nwasBornIn(?x,?y):-  birthPlace(?x,?z), city_in(?z,?y).");
        ExplanationExtractorClient.getInstance("localhost:9400").getExplanations(exampleQ);

    }
}
