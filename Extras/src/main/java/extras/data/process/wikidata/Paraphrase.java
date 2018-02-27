package extras.data.process.wikidata;

import java.util.Objects;

public class Paraphrase {

    String id;
    String fullId;
    String readableId;
    String labelType;
    String mention;
    String lang;

    public Paraphrase(String fullId, String labelType, String mention) {
        this.fullId = fullId;
        this.labelType = labelType;
        this.mention = mention;
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
}
