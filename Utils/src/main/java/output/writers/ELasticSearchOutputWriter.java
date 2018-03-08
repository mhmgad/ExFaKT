package output.writers;


import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;

import java.io.IOException;
import java.util.Collection;

public class ELasticSearchOutputWriter<T extends SerializableData> extends AbstractOutputChannel<T>{

    private final String objectType;
    private final boolean reIndex;
    JestClientFactory factory = new JestClientFactory();

    JestClient client;


    String indexName;


    public ELasticSearchOutputWriter(String urlWithPort,String indexName,boolean reIndex,String objectType){

        this.reIndex=reIndex;
        this.objectType=objectType;
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(urlWithPort)
                .multiThreaded(true)
                .connTimeout(3000000)
                .readTimeout(300000)
                .build());
        client = factory.getObject();


        this.indexName=indexName;
        boolean indexExists = false;
        try {
            indexExists = client.execute(new IndicesExists.Builder(indexName).build()).isSucceeded();
            if (reIndex && indexExists) {
                client.execute(new DeleteIndex.Builder(indexName).build());
                client.execute(new CreateIndex.Builder(indexName).build());
            }
////            else{
//
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Collection<T> records) {
        try {
            Bulk.Builder bulkIndexBuilder = new Bulk.Builder();
            for (T record : records) {

                bulkIndexBuilder.addAction(new Index.Builder(record).index(indexName).type(objectType).build());
            }
            BulkResult res = client.execute(bulkIndexBuilder.build());

//            System.out.println(res.getResponseCode()+" "+res.getErrorMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void write(T record) {
        try {
            Index index = new Index.Builder(record).index(indexName).type(objectType).build();
            client.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean close() {

        return false;
    }

    @Override
    public String getName() {
        return "elastic-"+indexName+"-"+objectType;
    }

}
