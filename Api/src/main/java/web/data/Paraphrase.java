package web.data;

import com.google.gson.Gson;
import io.searchbox.annotations.JestId;
import output.writers.SerializableData;

import java.util.Objects;
public class Paraphrase implements SerializableData{

     String predicate;
//    @JestId
//    String id;
    String entity;
    String fullId;
    String readableId;
    String labelType;
    String mention;
    String lang;

    public Paraphrase(String entity, String fullId, String readableId,String labelType, String mention, String predicate) {
        this.fullId = fullId;
        this.labelType = labelType;
        this.mention = mention;
        this.readableId=readableId;
        this.entity=entity;
        this.predicate=predicate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paraphrase)) return false;
        Paraphrase that = (Paraphrase) o;
        return Objects.equals(entity, that.entity) &&
                Objects.equals(mention, that.mention);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity, mention);
    }

    @Override
    public String toString() {
        return "Paraphrase{" +
                "id='" + entity + '\'' +
                ", fullId='" + fullId + '\'' +
                ", readableId='" + readableId + '\'' +
                ", labelType='" + labelType + '\'' +
                ", mention='" + mention + '\'' +
                ", lang='" + lang + '\'' +
                ", predicate='" + predicate + '\'' +
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

    @Override
    public String toCsv() {
        return null;
    }
}
