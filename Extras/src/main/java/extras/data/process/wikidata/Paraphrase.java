package extras.data.process.wikidata;

import io.searchbox.annotations.JestId;
import output.writers.SerializableData;

import java.util.Objects;

public class Paraphrase implements SerializableData{

    @JestId
    String id;
    String fullId;
    String readableId;
    String labelType;
    String mention;
    String lang;

    public Paraphrase(String id, String fullId, String readableId,String labelType, String mention) {
        this.fullId = fullId;
        this.labelType = labelType;
        this.mention = mention;
        this.readableId=readableId;
        this.id=id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paraphrase)) return false;
        Paraphrase that = (Paraphrase) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(mention, that.mention);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mention);
    }

    public String toJOSN() {
        return null;
    }

    @Override
    public String toJSON() {
        return null;
    }

    @Override
    public String toTriple() {
        return null;
    }

    @Override
    public String toTsv() {
        return null;
    }
}
