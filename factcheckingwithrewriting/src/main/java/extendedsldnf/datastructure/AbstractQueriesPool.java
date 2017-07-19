package extendedsldnf.datastructure;

import org.apache.commons.collections4.ComparatorUtils;

import java.util.*;

/**
 * Created by gadelrab on 7/14/17.
 */
public abstract class AbstractQueriesPool implements IQueriesPool {


    public static enum ComparisionMethod {DFS,HEURISTIC}

    PriorityQueue<ExtQuerySubs> queries;


    public AbstractQueriesPool() {
        queries = new PriorityQueue<>(getComparator());
    }


    @Override
    public ExtQuerySubs selectQuery() {
       return queries.remove();
    }



    @Override
    public void add(ExtQuerySubs query) {
        queries.add(query);
    }

    @Override
    public boolean isEmpty() {
        return queries.isEmpty();
    }

    @Override
    public int size() {
        return queries.size();
    }

    @Override
    public void addAll(Collection<ExtQuerySubs> queries) {
        this.queries.addAll(queries);
    }


    public abstract Comparator<ExtQuerySubs> getComparator();
}
