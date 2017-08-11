package extendedsldnf.datastructure;

import org.deri.iris.api.basics.IQuery;

import java.util.Collection;

/**
 * Created by gadelrab on 3/22/17.
 */
public interface IQueryExplanations {


    boolean isEmpty();

    public IQuery getQuery();

    public Collection<Explanation> getExplanations();

    public boolean hasDirectExplanation();

    public boolean hasIndirectExplanation();

    public boolean isDirectExplanation();

    public double getScoreOnSize();



    public int size();

    double getAvgQuality();

    public int documentLevelCount();
}
