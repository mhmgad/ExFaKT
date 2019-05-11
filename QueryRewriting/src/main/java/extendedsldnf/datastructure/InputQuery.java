package extendedsldnf.datastructure;

import de.mpii.datastructures.BinaryFact;
import org.deri.iris.api.basics.IQuery;
import utils.Converter;

import java.util.List;

public class InputQuery {

    int order;
    int label;
    int maxRules;
    int maxExplanations;
    List<TextualSource> textualSources;
    List<String> kgs;
    private BinaryFact ntQuery;
    private  IQuery iQuery;

    public InputQuery(IQuery query, int order,int label) {

        this.iQuery=query;
        this.ntQuery= Converter.toFact(query);
        this.order = order;
        this.label=label;
    }

    public InputQuery(BinaryFact ntQuery, int order,int label) {
        this.iQuery=Converter.toIQuery(ntQuery);
        this.ntQuery= ntQuery;
        this.order = order;
        this.label=label;
    }

    public int getMaxRules() {
        return maxRules;
    }

    public void setMaxRules(int maxRules) {
        this.maxRules = maxRules;
    }

    public int getMaxExplanations() {
        return maxExplanations;
    }

    public void setMaxExplanations(int maxExplanations) {
        this.maxExplanations = maxExplanations;
    }

    public IQuery getIQuery() {
        return iQuery;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public BinaryFact getNtQuery() {
        return ntQuery;
    }

    public void setNtQuery(BinaryFact ntQuery) {
        this.ntQuery = ntQuery;
    }

    public IQuery getiQuery() {
        return iQuery;
    }

    public void setiQuery(IQuery iQuery) {
        this.iQuery = iQuery;
    }

    public List<TextualSource> getTextualSources() {
        return textualSources;
    }

    public void setTextualSources(List<TextualSource> textualSources) {
        this.textualSources = textualSources;
    }

    public List<String> getKgs() {
        return kgs;
    }

    public void setKgs(List<String> kgs) {
        this.kgs = kgs;
    }

    @Override
    public String toString() {
        return
                "("+order+") "+ iQuery + "\t label=" + label ;
    }
}
