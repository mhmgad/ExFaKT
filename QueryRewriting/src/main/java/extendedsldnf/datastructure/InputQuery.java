package extendedsldnf.datastructure;

import de.mpii.datastructures.BinaryFact;
import org.deri.iris.api.basics.IQuery;
import utils.Converter;

public class InputQuery {

    private BinaryFact ntQuery;
    private  IQuery iQuery;
int order;
int label;


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


    public IQuery getIQuery() {
        return iQuery;
    }

    public int getOrder() {
        return order;
    }

    public int getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return
                "("+order+") "+ iQuery + "\t label=" + label ;
    }
}
