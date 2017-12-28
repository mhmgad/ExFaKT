package client.explain;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;

import com.google.inject.Singleton;
import web.data.Query;


import java.io.IOException;

@Singleton
public class ExplanationExtractorClient {


    final static String URL="http://sedna.mpi-inf.mpg.de:9350";


   public static final ExplanationExtractorClient instance=new ExplanationExtractorClient();
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private ExplanationExtractorClient() {



    }

    public void getExplanations(Query query) throws IOException {
        Gson gson=new Gson();
        String queryJson=gson.toJson(query);
        System.out.println("***********The Query:\n"+queryJson);
        HttpContent content=new JsonHttpContent(new GsonFactory(), queryJson);

        HttpRequest request = HTTP_TRANSPORT.createRequestFactory().buildPostRequest(new GenericUrl(URL), content);
        HttpResponse response = request.execute();
        if(response.getStatusCode()==200)
            System.out.println(response.getContent().toString());



    }




    public static ExplanationExtractorClient getInstance() {
        return instance;
    }
}
