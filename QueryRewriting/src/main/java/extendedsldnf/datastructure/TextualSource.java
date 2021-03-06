package extendedsldnf.datastructure;

import java.util.Objects;

public class TextualSource {

    String sourceName;
    Double weight;

    public TextualSource() {

    }

    public TextualSource(String sourceName, Double weight) {
        this.sourceName = sourceName;
        this.weight = weight;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextualSource)) return false;
        TextualSource that = (TextualSource) o;
        return sourceName.equals(that.sourceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceName);
    }


    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}

