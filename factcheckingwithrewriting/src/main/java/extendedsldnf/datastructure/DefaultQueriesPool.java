package extendedsldnf.datastructure;

import org.apache.commons.collections4.ComparatorUtils;
import utils.Enums;

import java.util.*;

/**
 * Created by gadelrab on 7/14/17.
 */
public class DefaultQueriesPool implements IQueriesPool {


    public static enum ComparisionMethod {DFS,HEURISTIC}

    PriorityQueue<ExtQuerySubs> queries;


    public DefaultQueriesPool(ComparisionMethod method) {
        switch (method) {
            case DFS:
                queries = new PriorityQueue<>(getDFSComparator());
                break;
            case HEURISTIC:
                queries = new PriorityQueue<>(getHeuristicComparator());
        }


    }

    /**
     * Comparator that simulates DFS strategy by considering:
     * 1) Depth in expansion tree (Deeper first)
     * 2) Priority of source action (smaller first)
     * @return
     */
    private Comparator<ExtQuerySubs> getDFSComparator() {
     return ComparatorUtils.chainedComparator(new Comparator[]{ExtQuerySubs.treeDepthComparator.reversed(), ExtQuerySubs.ActionPriorityComparator});

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

    /**
     * Comparator that simulates our Heursitics:
     * 1) Shorter rewritings first
     * 2) least number of rules
     * 3) least number of free variables
     * 4) least number of possible grounding (above 0) //TODO to be added
     * @return
     */
    public Comparator<ExtQuerySubs> getHeuristicComparator() {

        List<Comparator<ExtQuerySubs>> comparatorList=new LinkedList<>();
        comparatorList.add(ExtQuerySubs.sizeComparator);
        comparatorList.add(ExtQuerySubs.rulesDepthComparator);
        comparatorList.add(ExtQuerySubs.rulesDepthComparator);
        comparatorList.add(ExtQuerySubs.freeVarsComparator);
        comparatorList.add(ExtQuerySubs.treeDepthComparator.reversed());



        return ComparatorUtils.chainedComparator(comparatorList);
    }
}
