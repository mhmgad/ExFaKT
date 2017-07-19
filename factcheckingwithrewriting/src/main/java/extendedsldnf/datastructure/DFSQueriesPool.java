package extendedsldnf.datastructure;

import org.apache.commons.collections4.ComparatorUtils;

import java.util.Comparator;

/**
 * Created by gadelrab on 7/19/17.
 */
public class DFSQueriesPool extends AbstractQueriesPool{

    /**
     * Comparator that simulates DFS strategy by considering:
     * 1) Depth in expansion tree (Deeper first)
     * 2) Priority of source action (smaller first)
     * @return
     */
    @Override
    public Comparator<ExtQuerySubs> getComparator() {
        return ComparatorUtils.chainedComparator(new Comparator[]{ExtQuerySubs.treeDepthComparator.reversed(), ExtQuerySubs.ActionPriorityComparator});
    }
}
