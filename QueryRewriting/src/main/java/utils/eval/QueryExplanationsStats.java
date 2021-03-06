package utils.eval;

import com.google.common.base.Joiner;
import extendedsldnf.datastructure.Explanation;
import extendedsldnf.datastructure.IQueryExplanations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gadelrab on 5/18/17.
 */
public  class QueryExplanationsStats {

    private final IQueryExplanations explanation;

    public static String[] outputElements= new String[]{"Query","hasExplanations","isDirect","hasDirect", "hasIndirect", "newlyCovered","AllPureKG", "onePureFromKG","count","withText","fromTextOnly","fromKGOnly","avgQuality","maxQuality"};

    public QueryExplanationsStats(IQueryExplanations explanation) {
        this.explanation=explanation;
    }

    public boolean isDirectExplanation(){
        return explanation.isDirectExplanation();
    }

    public boolean hasDirectExplanation(){
        return explanation.hasDirectExplanation();
    }


    public boolean hasIndirectExplanation() {
        return explanation.hasIndirectExplanation();
    }

    public boolean hasExplanation(){
        return !explanation.isEmpty();
    }


    public int getExplanationsCount(){
        return explanation.getExplanations().size();
    }

    public int getExplanationsWithTextCount(){
        return (int) explanation.getExplanations().stream().filter(Explanation::hasTextEvidences).count();
    }

    public int getExplanationsWithTextOnlyCount(){
        return (int) explanation.getExplanations().stream().filter(Explanation::hasEvidencesFromTextOnly).count();
    }

    public int getExplanationsWithKGFactsOnlyCount(){
        return (int) explanation.getExplanations().stream().filter(Explanation::hasEvidencesFromKGOnly).count();
    }

    public boolean areAllPureKG(){
        return (!explanation.isEmpty())&&explanation.getExplanations().stream().allMatch(Explanation::hasEvidencesFromKGOnly);
    }

    public boolean isNewlyCovered(){
        return hasIndirectExplanation()&& !hasDirectExplanation();
    }


    public boolean isOnePureFromKG(){
        return explanation.getExplanations().stream().anyMatch(Explanation::hasEvidencesFromKGOnly);
    }

    @Override
    public String toString() {
        List<String> stats=new ArrayList<>();

        stats.add(explanation.getQuery().toString());
        //flags
        stats.add(""+hasExplanation());
        stats.add(""+isDirectExplanation());
        stats.add(""+hasDirectExplanation());
        stats.add(""+hasIndirectExplanation());
        stats.add(""+ isNewlyCovered());
        stats.add(""+ areAllPureKG());
        stats.add(""+ isOnePureFromKG());

        // counts
        stats.add(""+getExplanationsCount());
        stats.add(""+getExplanationsWithTextCount());
        stats.add(""+getExplanationsWithTextOnlyCount());
        stats.add(""+getExplanationsWithKGFactsOnlyCount());
        stats.add(""+getExplanationsAvgQuality());
        stats.add(""+getExplanationsMaxQuality());




        return Joiner.on('\t').join(stats);
    }

    private double getExplanationsMaxQuality() {
        if(explanation.size()==0)
            return 0;
        return explanation.getExplanations().stream().mapToDouble(Explanation::getQuality).max().getAsDouble();
    }


    public double getExplanationsAvgQuality() {
        return explanation.getAvgQuality();
    }

    public static void main(String[] args) {
//        List<Integer> vals=new ArrayList<>();
//
//        System.out.println(vals.stream().mapToInt(Integer::intValue).max().getAsInt());
    }
}
