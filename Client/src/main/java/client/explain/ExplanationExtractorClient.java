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


    final static String URL="http://sedna.mpi-inf.mpg.de:9350";


   public static final ExplanationExtractorClient instance=new ExplanationExtractorClient();
//    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private ExplanationExtractorClient() {



    }

    public IQueryExplanations getExplanations(Query query) throws IOException {
        Gson gson= CustomGson.getInstance().getGson();
        String queryJson=gson.toJson(query);
//        System.out.println("***********The Query:\n"+queryJson);
//        System.out.println(query);
//        HttpContent content=new JsonHttpContent(new GsonFactory(), query);
//
//        content.writeTo(System.out);
//
//        HttpPost post=new


        QueryExplanations queryExplanations=null;
        HttpPost p = new HttpPost(URL);

        p.setEntity(new StringEntity(queryJson,
                ContentType.create("application/json")));

        HttpClient c=HttpClients.createDefault();

        System.out.println(p.getEntity().getContent().toString());

        HttpResponse response = c.execute(p);

        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                BufferedReader br=new BufferedReader(new InputStreamReader(instream));
                try {

                     queryExplanations = gson.fromJson(br, QueryExplanations.class);
//                    for(String line = br.readLine();line!=null;line=br.readLine())
//                    System.out.println(line );

                    System.out.println(queryExplanations);
                    // do something useful
                } finally {
                    instream.close();
                }
            }
        } finally {
            //response.close();
        }

//        HttpRequest request = HTTP_TRANSPORT.createRequestFactory().buildPostRequest(new GenericUrl(URL),);
//        HttpResponse response = request.execute();
//        if(response.getStatusCode()==200)
//            System.out.println(response.getContent().toString());

    return queryExplanations;

    }




    public static ExplanationExtractorClient getInstance() {
        return instance;
    }

    public static void main(String[] args) throws IOException {
        Query exampleQ = new Query("<Albert_Einstein>", "<wasBornIn>", "<Germany>", "wasBornIn(?x,?y):- birthPlace(?x,?z), in(?z,?y).\nwasBornIn(?x,?y):-  birthPlace(?x,?z), city_in(?z,?y).");
        ExplanationExtractorClient.getInstance().getExplanations(exampleQ);

    }
}
