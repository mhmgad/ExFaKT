package output.writers;

import java.util.Collection;

public abstract class AbstractOutputChannel {

    OutputFormat mode;
    public enum OutputFormat {JSON(".json"),TSV(".tsv"),TEXT(".txt"),TRIPLE(".nt");
        private final String fileExtention;




        private OutputFormat(String s) {
            fileExtention = s;
        }
    }


    public void write(Collection<SerializableData> collection){
        collection.forEach(item->this.write(item));
    }



    abstract public void write(SerializableData record) ;

    abstract public boolean close();

    abstract public String getName();


    public String formatString(SerializableData record){

        switch (mode){
            case TSV:
                return record.toTsv();
            case TRIPLE:
                return record.toTriple();
            case JSON:
                return record.toJSON();
            case TEXT:
            default:
                return record.toString();

        }

    }


}
