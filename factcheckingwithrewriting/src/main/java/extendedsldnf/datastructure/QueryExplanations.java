package extendedsldnf.datastructure;

import com.google.common.base.Joiner;
import extendedsldnf.CostAccumulator;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.storage.IRelation;

import java.util.*;

/**
 * Created by gadelrab on 3/22/17.
 */
//TODO get red off the IRelation interface, It is useless but it is easier than implementing the whole thing form scratch
public class QueryExplanations implements IRelation, IQueryExplanations {

    private CostAccumulator costAccumulator;
    /**
     * successful rewriting paths
     */
    private SortedSet<Explanation> explanations;
    private IQuery query;


//    public Solutions() {
//        this(new ArrayList<>());
//    }

    public QueryExplanations(IQuery query, List<Explanation> explanations, CostAccumulator cost) {
        this.explanations = new TreeSet<>(explanations) ;
        this.query=query;
        this.costAccumulator=cost;
        explanations.forEach(expl->expl.getCost().setQueryFullCost(costAccumulator));
    }

    @Override
    public boolean add(ITuple tuple) {
        //TODO Remove this
        System.out.println("Adding to Relation is not supported!");
        return false;
    }

    @Override
    public boolean addAll(IRelation relation) {
        //TODO Remove this
        System.out.println("Adding to Relation is not supported!");
        return false;
    }

    @Override
    public int size() {
        //TODO Remove this
        //System.out.println("Size Of relation is not supported is not supported!");
        return explanations.size();

    }

    @Override
    public ITuple get(int index) {
        //TODO Remove this
        System.out.println("Getting tuple is not supported!");
        return null;
    }

    @Override
    public boolean contains(ITuple tuple) {
        //TODO Remove this
        System.out.println("contains is not supported!");
        return false;
    }


    @Override
    public String toString() {
        return "Explanations{[\n" +
                Joiner.on("\n\n,").join(explanations) +
                "\n]}";
    }

    @Override
    public boolean isEmpty() {
        return explanations.isEmpty();
    }

    @Override
    public IQuery getQuery() {
        return this.query;
    }

    @Override
    public Collection<Explanation> getExplanations() {
        return explanations;
    }

    @Override
    public boolean hasDirectExplanation() {
        return getExplanations().stream().anyMatch(Explanation::isDirectEvidence);
    }

    @Override
    public boolean isDirectExplanation() {
        return getExplanations().size()==1 && hasDirectExplanation();
    }

    @Override
    public boolean hasIndirectExplanation() {
        return (!isEmpty())&&!getExplanations().stream().allMatch(Explanation::isDirectEvidence);

    }

    public CostAccumulator getCostAccumulator() {
        return costAccumulator;
    }
}
