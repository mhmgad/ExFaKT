package extendedsldnf.datastructure;

import extendedsldnf.datastructure.ExtendedQueryWithSubstitution;

import java.util.Collection;
import java.util.List;

/**
 * Created by gadelrab on 7/14/17.
 */
public interface IQueriesPool {

    public ExtendedQueryWithSubstitution selectQuery();

    public void addAll(Collection<ExtendedQueryWithSubstitution> queries);

    public void add(ExtendedQueryWithSubstitution query);

    public boolean isEmpty();

    public int size();

}
