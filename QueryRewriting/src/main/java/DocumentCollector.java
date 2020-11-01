import config.Configuration;
import de.mpii.factspotting.text.ElasticSearchFactSpotter;
import de.mpii.factspotting.text.TextEvidence;
import extendedsldnf.datastructure.InputQuery;
import mpi.tools.javatools.util.FileUtils;
import utils.Converter;
import utils.DataUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class DocumentCollector {


    public static void main(String[] args) throws IOException {

        LinkedHashMap<InputQuery, Integer> queries = DataUtils.loadQueries(Configuration.getInstance(), Arrays.asList("data/queries_manually.tsv"));
        BufferedWriter outbw = FileUtils.getBufferedUTF8Writer("data/queries_manually_voting.tsv");
        LinkedHashMap<InputQuery,TextEvidence> results=new LinkedHashMap<>();
        ElasticSearchFactSpotter spotter=new ElasticSearchFactSpotter();

        for(InputQuery query: queries.keySet()){
            System.out.println(query);
            TextEvidence ev=spotter.spot(Converter.toFact(query.getIQuery()));

            results.put(query, ev);
            outbw.write(Converter.toFact(query.getIQuery()).toSearchableString()+"\t"+results.get(query).getDocuments().size()+"\t"+query.getLabel()+"\n");

        }

        outbw.close();






    }
}
