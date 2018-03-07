package extras.data.process.wikidata;

import com.google.gson.Gson;
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

    @Override
    public String toString() {
        return "Paraphrase{" +
                "id='" + id + '\'' +
                ", fullId='" + fullId + '\'' +
                ", readableId='" + readableId + '\'' +
                ", labelType='" + labelType + '\'' +
                ", mention='" + mention + '\'' +
                ", lang='" + lang + '\'' +
                '}';
    }

    @Override
    public String toJSON() {
        Gson gson=new Gson();
        return gson.toJson(this);
    }

    @Override
    public String toTriple() {
        return fullId+' '+labelType+" \""+mention+"\" .";
    }

    @Override
    public String toTsv() {
        return fullId+'\t'+labelType+'\t'+mention;
    }
}
