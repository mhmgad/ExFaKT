package extendedsldnf.datastructure;

import org.apache.commons.collections4.ComparatorUtils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gadelrab on 7/19/17.
 */
public class HeuristicBasedQueriesPool extends AbstractQueriesPool{

    /**
     * Comparator that simulates our Heursitics:
     * 1) Shorter rewritings first
     * 2) least number of rules
     * 3) least number of free variables
     * 4) least number of possible grounding (above 0) //TODO to be added
     * @return
     */
    @Override
    public Comparator<ExtQuerySubs> getComparator() {

        List<Comparator<ExtQuerySubs>> comparatorList=new LinkedList<>();
        comparatorList.add(ExtQuerySubs.sizeComparator);
        comparatorList.add(ExtQuerySubs.rulesDepthComparator);
        comparatorList.add(ExtQuerySubs.rulesDepthComparator);
        comparatorList.add(ExtQuerySubs.freeVarsComparator);
        comparatorList.add(ExtQuerySubs.treeDepthComparator.reversed());

        return ComparatorUtils.chainedComparator(comparatorList);
    }
}
