package output.writers;

public interface SerializableData {


    public String toJSON();

    public String toTriple();

    public String toTsv();

}
