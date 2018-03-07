package output.writers;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;

import java.io.IOException;
import java.util.Collection;

public class ELasticSearchOutputWriter extends AbstractOutputChannel< SerializableData>{

    JestClientFactory factory = new JestClientFactory();

    JestClient client;


    String indexName;


    public ELasticSearchOutputWriter(String urlWithPort,String indexName){

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
            if (!indexExists) {
                client.execute(new CreateIndex.Builder(indexName).build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Collection<SerializableData> records) {
        try {
            Bulk.Builder bulkIndexBuilder = new Bulk.Builder();
            for (SerializableData record : records) {

                bulkIndexBuilder.addAction(new Index.Builder(record).index(indexName).build());
            }
            client.execute(bulkIndexBuilder.build());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void write(SerializableData record) {
        try {
            client.execute(new Index.Builder(record).index(indexName).build());
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
        return null;
    }
}
