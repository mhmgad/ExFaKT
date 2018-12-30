package extendedsldnf.datastructure;

import de.mpii.datastructures.Fact;
import org.deri.iris.api.basics.IQuery;

import java.util.Collection;

/**
 * Created by gadelrab on 3/22/17.
 */
public interface IQueryExplanations {

    enum Sorting{Quality, Generation, Length, Depth, Cost};

    boolean isEmpty();

    public IQuery getQuery();

    public Fact getQueryAsFact();

    public Collection<Explanation> getExplanations();

    public boolean hasDirectExplanation();

    public boolean hasIndirectExplanation();

    public boolean isDirectExplanation();

    public double getScoreOnSize();



    public int size();

    double getAvgQuality();

    public int documentLevelCount();

    public String getReadableString();

    void sortExplanations(Sorting method);

    Sorting getSortingMethod();

    String toJsonString();

    String getQueryAsReadableString();


}
