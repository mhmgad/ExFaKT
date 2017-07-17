package extendedsldnf.datastructure;

import java.util.Collection;

/**
 * Created by gadelrab on 7/14/17.
 */
public interface IQueriesPool {

    public ExtQuerySubs selectQuery();

    public void addAll(Collection<ExtQuerySubs> queries);

    public void add(ExtQuerySubs query);

    public boolean isEmpty();

    public int size();

}
