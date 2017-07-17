package utils.eval;

import com.google.common.base.Joiner;

import extendedsldnf.CostAccumulator;
import extendedsldnf.datastructure.Explanation;
import extendedsldnf.datastructure.IQueryExplanations;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by gadelrab on 5/18/17.
 */
public class ResultsEvaluator {


    private int hasExplanationsCount;
    private int isDirectExplanationOnlyCount;
    private int hasDirectExplanationCount;
    private int hasIndirectExplanationCount;
    private int areAllPureKGCount;
    private int isOnePureFromKGCount;
    private double[] explanationsPerQuery;
    private double[] explanationsWithTextPerQuery;
    private double[] explanationsWithTextOnlyPerQuery;
    private double[] explanationsWithKGFactsOnlyPerQuery;
    private List<QueryExplanationsStats> explanationsStats;
    private int newlyCoveredCount;


    List<IQueryExplanations> queryExplanations;

    public ResultsEvaluator(List<IQueryExplanations> queryExplanations) {

        this.queryExplanations=queryExplanations;

        evaluate(queryExplanations);
    }



    private void evaluate(List<IQueryExplanations> explanations){
        // Create requires stats object

        explanationsStats = explanations.stream().map(ex -> new QueryExplanationsStats(ex)).collect(Collectors.toList());

        int total=explanations.size();

        // Recall
         hasExplanationsCount= (int) explanationsStats.stream().filter(QueryExplanationsStats::hasExplanation).count();

        // isDirect
         isDirectExplanationOnlyCount= (int) explanationsStats.stream().filter(QueryExplanationsStats::isDirectExplanation).count();

        //hasDirect
         hasDirectExplanationCount= (int) explanationsStats.stream().filter(QueryExplanationsStats::hasDirectExplanation).count();

        //hasIndirect
         hasIndirectExplanationCount= (int) explanationsStats.stream().filter(QueryExplanationsStats::hasIndirectExplanation).count();

        //hasIndirect
        newlyCoveredCount= (int) explanationsStats.stream().filter(QueryExplanationsStats::isNewlyCovered).count();

        //pureEvidencesFrom KG
         areAllPureKGCount= (int) explanationsStats.stream().filter(QueryExplanationsStats::areAllPureKG).count();

        //at least one path is covered from KG only
         isOnePureFromKGCount= (int) explanationsStats.stream().filter(QueryExplanationsStats::isOnePureFromKG).count();


        //stats
        explanationsPerQuery=getAverageAndMax(explanationsStats,QueryExplanationsStats::getExplanationsCount);
        explanationsWithTextPerQuery=getAverageAndMax(explanationsStats,QueryExplanationsStats::getExplanationsWithTextCount);
        explanationsWithTextOnlyPerQuery=getAverageAndMax(explanationsStats,QueryExplanationsStats::getExplanationsWithTextOnlyCount);
        explanationsWithKGFactsOnlyPerQuery=getAverageAndMax(explanationsStats,QueryExplanationsStats::getExplanationsWithKGFactsOnlyCount);


    }

    private double[] getAverageAndMax(List<QueryExplanationsStats> explanationsStats,ToIntFunction<? super QueryExplanationsStats> function){
        IntStream explanationAggregations = explanationsStats.stream().mapToInt(function);//);
        double [] avgMax=new double[2];
        avgMax[0]=explanationAggregations.average().getAsDouble();

        explanationAggregations = explanationsStats.stream().mapToInt(function);
        avgMax[1]=explanationAggregations.max().getAsInt();
        return avgMax;

    }

    public void dump(BufferedWriter bw){
        try {

            bw.write(textToDump());
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public String toString() {
        return "ResultsEvaluator{" +
                "hasExplanationsCount=" + hasExplanationsCount +
                ", isDirectExplanationOnlyCount=" + isDirectExplanationOnlyCount +
                ", hasDirectExplanationCount=" + hasDirectExplanationCount +
                ", hasIndirectExplanationCount=" + hasIndirectExplanationCount +
                ", newlyCoveredCount=" + newlyCoveredCount +
                ", areAllPureKGCount=" + areAllPureKGCount +
                ", isOnePureFromKGCount=" + isOnePureFromKGCount +
                ", explanationsPerQuery=" + Arrays.toString(explanationsPerQuery) +
                ", explanationsWithTextPerQuery=" + Arrays.toString(explanationsWithTextPerQuery) +
                ", explanationsWithTextOnlyPerQuery=" + Arrays.toString(explanationsWithTextOnlyPerQuery) +
                ", explanationsWithKGFactsOnlyPerQuery=" + Arrays.toString(explanationsWithKGFactsOnlyPerQuery) +
                '}';
    }

    public String textToDump() {
        StringBuilder sb=new StringBuilder();
        String header=Joiner.on('\t').join(QueryExplanationsStats.outputElements);
        sb.append(header);
        sb.append('\n');
        String data=Joiner.on('\n').join(explanationsStats);
        sb.append(data);
        sb.append("\nStats [avg,max]:\t");

        sb.append(hasExplanationsCount);sb.append('\t');
        sb.append(isDirectExplanationOnlyCount);sb.append('\t');
        sb.append(hasDirectExplanationCount);sb.append('\t');
        sb.append(hasIndirectExplanationCount);sb.append('\t');
        sb.append(newlyCoveredCount);sb.append('\t');
        sb.append(areAllPureKGCount);sb.append('\t');
        sb.append(isOnePureFromKGCount);sb.append('\t');

        sb.append(Arrays.toString(explanationsPerQuery));sb.append('\t');
        sb.append(Arrays.toString(explanationsWithTextPerQuery));sb.append('\t');
        sb.append(Arrays.toString(explanationsWithTextOnlyPerQuery));sb.append('\t');
        sb.append(Arrays.toString(explanationsWithKGFactsOnlyPerQuery));sb.append('\t');
        sb.append('\n');


        return sb.toString();

    }


    public void dumpSummary(BufferedWriter bw){
        try {
            bw.write(toString());
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void dumpCost(BufferedWriter outputCostFile) {
        StringBuilder sb=new StringBuilder();

        for(IQueryExplanations queryExplanationsObject:queryExplanations){
            Collection<Explanation> explanations = queryExplanationsObject.getExplanations();
            sb.append(queryExplanationsObject.getQuery().toString()+((explanations.size()>0)? '\t':""));
            sb.append(Joiner.on('\t').join(explanations.stream().map(exp-> Integer.toString(exp.getCost().getTotalCost())).collect(Collectors.toList())));
            sb.append('\n');
        }
        try {
            outputCostFile.write(sb.toString());
            outputCostFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void dumpCostSummary(BufferedWriter outputCostSummaryFile) {
        int maxSize=queryExplanations.stream().mapToInt(IQueryExplanations::size).max().getAsInt();
        List<LinkedList<Explanation>> explanationsAsList = queryExplanations.stream().map(qexp -> new LinkedList<Explanation>(qexp.getExplanations())).collect(Collectors.toList());

        StringBuilder sb=new StringBuilder();
        sb.append("Explan\tCount\tAvg.\tMax\tMin\tSum\tAvg.time\tMax.time\tMin.time\ttotal.time\n");

        for (int i = 1; i < maxSize+1; i++) {
            //IntSummaryStatistics levelCost = explanationsAsList.stream().filter(expL -> expL.size() > 0).map(expL -> expL.removeFirst()).mapToInt(exp -> exp.getCost().getTotalCost()).summaryStatistics();
            List<CostAccumulator> levelCosts = explanationsAsList.stream().filter(expL -> expL.size() > 0).map(expL -> expL.removeFirst()).map(exp -> exp.getCost()).collect(Collectors.toList());
            IntSummaryStatistics levelCost = levelCosts.stream().mapToInt(CostAccumulator::getTotalCost).summaryStatistics();
            DoubleSummaryStatistics levelTime = levelCosts.stream().mapToDouble(CostAccumulator::getElapsedTimeSec).summaryStatistics();
            sb.append(i+"\t"+levelCost.getCount()+"\t"+levelCost.getAverage()+"\t"+levelCost.getMax()+"\t"+levelCost.getMin()+"\t"+levelCost.getSum());
            sb.append("\t"+levelTime.getAverage()+"\t"+levelTime.getMax()+"\t"+levelTime.getMin()+"\t"+levelTime.getSum());
        }

        try {
            outputCostSummaryFile.write(sb.toString());
            outputCostSummaryFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
