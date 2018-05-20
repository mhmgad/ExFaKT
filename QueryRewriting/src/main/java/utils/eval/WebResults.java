package utils.eval;

import config.Configuration;
import de.mpii.datastructures.Document;
import de.mpii.factspotting.ISpottedEvidence;
import de.mpii.factspotting.text.BingFactSpotter;
import de.mpii.factspotting.text.TextEvidence;
import extendedsldnf.datastructure.InputQuery;
import output.listner.OutputListener;
import output.writers.AbstractOutputChannel;
import output.writers.FileOutputWriter;
import output.writers.SerializableData;
import utils.Converter;
import utils.DataUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class WebResults {




    public static void main(String[] args) {
        String file="queries_all.tsv";
        LinkedHashMap<InputQuery, Integer> queries = DataUtils.loadQueries(Configuration.getInstance(), Arrays.asList("data/"+file));

        OutputListener out=new OutputListener();
        out.registerWriter(new FileOutputWriter("data/out/"+file+"_out",AbstractOutputChannel.OutputFormat.CSV,TextEvidence.getHeader()));
        BingFactSpotter fs=new BingFactSpotter();

        for(InputQuery q:queries.keySet()){


            TextEvidence ev = (TextEvidence) fs.spotFullDocuments(Converter.toFact(q.getIQuery()));

            if(ev.getDocuments().size()>0) {
                out.out(ev);
            }

        }
        out.close();
    }
}
