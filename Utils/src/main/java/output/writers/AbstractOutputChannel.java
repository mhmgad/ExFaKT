package output.writers;

import java.util.Collection;

public abstract class AbstractOutputChannel<T extends SerializableData> {

    OutputFormat mode;
    public enum OutputFormat {JSON("json"),TSV("tsv"),TEXT("txt"),TRIPLE("nt"), CSV("csv");
        private final String fileExtension;




        OutputFormat(String s) {
            fileExtension = s;
        }

        public String getFileExtension() {
            return fileExtension;
        }
    }


    public void write(Collection<T> collection){
        collection.forEach(this::write);
    }



    abstract public void write(T record) ;

    abstract public boolean close();

    abstract public String getName();


    public String formatString(T record){

        switch (mode){
            case TSV:
                return record.toTsv();
            case TRIPLE:
                return record.toTriple();
            case JSON:
                return record.toJSON();
//            case CSV:
//                return record.toCsv();
            case TEXT:
            default:
                return record.toString();

        }

    }


}
