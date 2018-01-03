package extendedsldnf.datastructure;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import de.mpii.datastructures.Fact;
import extendedsldnf.CostAccumulator;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.storage.IRelation;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import utils.Converter;
import utils.StringUtils;
import utils.json.CustomGson;
import utils.json.adapters.IQueryAdapter;

import java.util.*;

/**
 * Created by gadelrab on 3/22/17.
 */
//TODO get red off the IRelation interface, It is useless but it is easier than implementing the whole thing form scratch
public class QueryExplanations implements IRelation, IQueryExplanations {

    private final double quality;
    private CostAccumulator costAccumulator;


    /**
     * successful rewriting paths
     */
    private SortedSet<Explanation> explanations;


    @JsonAdapter(IQueryAdapter.class)
    private IQuery query;

    IQueryExplanations.Sorting sortingMethod=Sorting.Generation;


    public QueryExplanations() {
        this(null,new ArrayList<>(),null);
    }



    public QueryExplanations(IQuery query, List<Explanation> explanations, CostAccumulator cost) {
        this.explanations = new TreeSet<>(explanations) ;
        this.query=query;
        this.costAccumulator=cost;
        explanations.forEach(expl->expl.getCost().setQueryFullCost(costAccumulator));
        explanations.forEach(expl->expl.setQuality());
        this.quality=getAvgQuality();

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
    public double getAvgQuality() {
        if(size()==0)
            return 0;
        return explanations.stream().mapToDouble(Explanation::getQualityScore).average().getAsDouble();
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
    public Fact getQueryAsFact() {
        try {
            return Converter.toFact(getQuery().getLiterals().get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    /**
     * sum(1/explanationSize)
     * @return
     */
    @Override
    public double getScoreOnSize() {
        return getExplanations().stream().mapToDouble(ex -> 1.0 / (ex.size())).sum();
    }

    @Override
    public boolean hasIndirectExplanation() {
        return (!isEmpty())&&!getExplanations().stream().allMatch(Explanation::isDirectEvidence);

    }

    public CostAccumulator getCostAccumulator() {
        return costAccumulator;
    }

    /**
     * count the number of documents retrieved. It sums from all explanations. However, only representative if it is a direct spotting.
     * @return
     */
    public int documentLevelCount(){
            return explanations.stream().flatMap(Explanation::getTextEvidencesStream).mapToInt(EvidenceNode::getRetrievedDocsCount).sum();
    }

    public void setCostAccumulator(CostAccumulator costAccumulator) {
        this.costAccumulator = costAccumulator;
    }

    public void setExplanations(SortedSet<Explanation> explanations) {
        this.explanations = explanations;
    }

    public void setQuery(IQuery query) {
        this.query = query;
    }

    @Override
    public String getReadableString(){
        StringBuilder readable=new StringBuilder("Query:\t"+getQueryAsFact().toSearchableString().replaceAll("\t"," ").trim()+"\t("+explanations.size()+")");
        readable.append('\n');

        for (Explanation exp:explanations) {

            readable.append(StringUtils.indent(exp.getReadableString()));
        }
        readable.append("\n**********************************************\n");

        return readable.toString();
    }

    @Override
    public void sortExplanations(Sorting method) {
        setSortingMethod(method);
        Collection<Explanation> tempExplanations = getExplanations();
        switch (method){
            case Quality:
                explanations=new TreeSortedSet<>(Comparator.comparing(Explanation::getQuality));
                break;
            case Cost:
                explanations=new TreeSortedSet<>(Comparator.comparing(Explanation::getCost));
                break;
            case Generation:
                explanations=new TreeSortedSet<>(Comparator.comparing(Explanation::getGenOrder));
                break;
            case Length:
                explanations=new TreeSortedSet<>(Comparator.comparing(Explanation::size));
                break;
            case Depth:
                explanations=new TreeSortedSet<>(Comparator.comparing(Explanation::getRulesEvidencesCount));
                break;
        }
        explanations.addAll(tempExplanations);

    }

    @Override
    public Sorting getSortingMethod() {
        return sortingMethod;
    }

    @Override
    public String toJsonString() {
        Gson gson= CustomGson.getInstance().getGson();
        return gson.toJson(this);
    }

    public void setSortingMethod(Sorting sortingMethod) {
        this.sortingMethod = sortingMethod;
    }
}
