package output.writers;


public interface SerializableData {


     String toJSON();

     String toTriple();

     String toTsv();

     String toCsv();



}
