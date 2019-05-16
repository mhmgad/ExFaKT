package dictionaries.elastic;

import basics.FactComponent;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import web.data.Paraphrase;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Singleton
public class Entities{


    static Entities instance;

private String url="http://localhost:9200";
    private  int resultSize=20;
    private  String indexName="yago_dict";
    JestClientFactory factory = new JestClientFactory();

    JestClient client;


    public Entities(String url) {
        this.url=url;
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(url)
                .multiThreaded(true)
                .connTimeout(3000000)
                .readTimeout(300000)
                .build());
        client = factory.getObject();
    }

    public Entities(String url, String indexName,int resultSize) {
        this(url);
        this.indexName = indexName;
        this.resultSize=resultSize;
    }

    public List<Paraphrase> suggestEntities(String partialMention) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        query.must(QueryBuilders.matchQuery("mention",partialMention));
        query.should(QueryBuilders.matchQuery("id",FactComponent.forYagoEntity(partialMention)));
        query.should(QueryBuilders.matchQuery("labelType","label"));

        searchSourceBuilder.query(
                query
        ).size(resultSize);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(indexName)
                .build();

        try {
            SearchResult response = client.execute(search);
            List<SearchResult.Hit<Paraphrase, Void>> responseList = response.getHits(Paraphrase.class);

            return responseList.stream().map(d -> d.source).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
    return null;
    }


//    public static Entities getInstance(){
//        return instance;
//    }

    public static Entities getInstance(String url){
//        instance.setURL(url);
        if(instance==null){
            instance=new Entities(url);
        }
        return instance;
    }

    private void setURL(String url) {
        this.url=url;
    }


    public static void main(String[] args) {

        System.out.println( getInstance("http://localhost:9200").suggestEntities("albert"));

    }
}