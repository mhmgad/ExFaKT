package sandbox;

import extras.data.process.wikidata.Paraphrase;
import output.writers.ELasticSearchOutputWriter;

import java.util.Collection;
import java.util.HashSet;

public class TestElasticWriter {



    public static void main(String[] args) {
        Paraphrase p=new Paraphrase("P0","test","P0_test","label","test");
        Paraphrase p1=new Paraphrase("P00","test","P00_test","label","test2");

        Paraphrase p3=new Paraphrase("P000","test","P000_test","label","test3");
        Collection<Paraphrase> records = new HashSet<>();
        records.add(p);
        records.add(p1);
        ELasticSearchOutputWriter<Paraphrase> writer=new ELasticSearchOutputWriter("http://localhost:9200","paraphrases","paraphrase");
        writer.write(records);
        writer.write(p3);
    }
}
