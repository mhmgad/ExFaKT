package extendedsldnf.datastructure;

import org.apache.commons.collections4.ComparatorUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by gadelrab on 7/14/17.
 */
public class DefaultQueriesPool implements IQueriesPool {


    PriorityQueue<ExtendedQueryWithSubstitution> queries;

    public DefaultQueriesPool() {
        queries=new PriorityQueue<>(getDFSComparator());

    }

    /**
     * Comparator that simulates DFS strategy by considering:
     * 1) Depth in expansion tree (Deeper first)
     * 2) Priority of source action (smaller first)
     * @return
     */
    private Comparator<ExtendedQueryWithSubstitution> getDFSComparator() {
     return ComparatorUtils.chainedComparator(new Comparator[]{Comparator.comparing(ExtendedQueryWithSubstitution::getTreeDepth).reversed(),ExtendedQueryWithSubstitution.ActionPriorityComparator});

    }

    @Override
    public ExtendedQueryWithSubstitution selectQuery() {
       return queries.remove();
    }



    @Override
    public void addAll(ExtendedQueryWithSubstitution query) {

        queries.add(query);
    }

    @Override
    public boolean isEmpty() {
        return queries.isEmpty();
    }

    @Override
    public void addAll(Collection<ExtendedQueryWithSubstitution> queries) {

        queries.addAll(queries);

    }
}
