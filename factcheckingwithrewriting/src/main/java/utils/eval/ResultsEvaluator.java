package utils.eval;

import com.google.common.base.Joiner;

import datastructure.CorrectnessInfo;
import extendedsldnf.CostAccumulator;
import extendedsldnf.datastructure.Explanation;
import extendedsldnf.datastructure.IQueryExplanations;
import org.deri.iris.api.basics.IQuery;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by gadelrab on 5/18/17.
 */
public class ResultsEvaluator {


    private final List<CorrectnessInfo> correctnessInfos;
    DecimalFormat dcf = new DecimalFormat("0.000");

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

    public ResultsEvaluator(List<IQueryExplanations> queryExplanations, List<CorrectnessInfo> correctnessInfos) {

        this.queryExplanations=queryExplanations;
        this.correctnessInfos=correctnessInfos;
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
        sb.append("Explan\tCount\tAvg.\tMax\tMin\tSum");
        sb.append("\tAvg.time\tMax.time\tMin.time\ttotal.time");
        sb.append("\tAvg.size\tMax.size\tMin.size");
        sb.append("\tAvg.textEvid\tMax.textEvid\tMin.textEvid");
        sb.append("\tAvg.rules\tMax.rules\tMin.rules");
        sb.append("\tAvg.relCost\tMax.relCost\tMin.relCost");
        sb.append("\tAvg.relTime\tMax.relTime\tMin.relTime");

        sb.append('\n');

        for (int i = 1; i < maxSize+1; i++) {
            //IntSummaryStatistics levelCost = explanationsAsList.stream().filter(expL -> expL.size() > 0).map(expL -> expL.removeFirst()).mapToInt(exp -> exp.getCost().getTotalCost()).summaryStatistics();
            List<Explanation> levelExplanation= explanationsAsList.stream().filter(expL -> expL.size() > 0).map(expL -> expL.removeFirst()).collect(Collectors.toList());



            List<CostAccumulator> levelCosts = levelExplanation.stream().map(exp -> exp.getCost()).collect(Collectors.toList());
            IntSummaryStatistics levelCost = levelCosts.stream().mapToInt(CostAccumulator::getTotalCost).summaryStatistics();
            DoubleSummaryStatistics levelTime = levelCosts.stream().mapToDouble(CostAccumulator::getElapsedTimeSec).summaryStatistics();
            IntSummaryStatistics explanSize= levelExplanation.stream().mapToInt(Explanation::size).summaryStatistics();
            IntSummaryStatistics explanTextEvid= levelExplanation.stream().mapToInt(Explanation::getTextEvidencesCount).summaryStatistics();
            IntSummaryStatistics explanRules= levelExplanation.stream().mapToInt(Explanation::getTextEvidencesCount).summaryStatistics();
            DoubleSummaryStatistics relativeCost = levelCosts.stream().mapToDouble(CostAccumulator::getRelativeCost).summaryStatistics();
            DoubleSummaryStatistics relativeTime = levelCosts.stream().mapToDouble(CostAccumulator::getRelativeElapsedTime).summaryStatistics();


            sb.append(i);
            sb.append("\t"+levelCost.getCount()+"\t"+dcf.format(levelCost.getAverage())+"\t"+levelCost.getMax()+"\t"+levelCost.getMin()+"\t"+levelCost.getSum());
            sb.append("\t"+dcf.format(levelTime.getAverage())+"\t"+dcf.format(levelTime.getMax())+"\t"+dcf.format(levelTime.getMin())+"\t"+dcf.format(levelTime.getSum()));
            sb.append("\t"+dcf.format(explanSize.getAverage())+"\t"+explanSize.getMax()+"\t"+explanSize.getMin());
            sb.append("\t"+dcf.format(explanTextEvid.getAverage())+"\t"+explanTextEvid.getMax()+"\t"+explanTextEvid.getMin());
            sb.append("\t"+dcf.format(explanRules.getAverage())+"\t"+explanRules.getMax()+"\t"+explanRules.getMin());
            sb.append("\t"+dcf.format(relativeCost.getAverage())+"\t"+dcf.format(relativeCost.getMax())+"\t"+dcf.format(relativeCost.getMin()));
            sb.append("\t"+dcf.format(relativeTime.getAverage())+"\t"+dcf.format(relativeTime.getMax())+"\t"+dcf.format(relativeTime.getMin()));


            sb.append('\n');
        }

        try {
            outputCostSummaryFile.write(sb.toString());
            outputCostSummaryFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Average accuracy through groups as described in Ndapa's paper
     * TODO check if accuracy is averaged
     * @param groundTruth
     * @return
     */
    public double evaluateRanking(Map<IQuery,Integer> groundTruth){
        Map<String, List<CorrectnessInfo>> groupedCorrectness = correctnessInfos.stream().sorted(Comparator.comparing(CorrectnessInfo::getScore)).collect(Collectors.groupingBy(CorrectnessInfo::getGrouping));
        System.out.println("groups :"+groupedCorrectness.keySet()+"\n"+ groupedCorrectness.size() );


        int accuracyTotal=0;
        for (List<CorrectnessInfo> gInfo: groupedCorrectness.values()   ) {
            List<Integer> labels = gInfo.stream().sorted(Comparator.comparing(CorrectnessInfo::getScore)).mapToInt(g -> groundTruth.get(g.getPosQuery())).boxed().collect(Collectors.toList());
            long trueAlter=labels.stream().filter(l-> l.intValue()==1).count();
            long falseAlter=labels.stream().filter(l-> l.intValue()==0).count();
            long groundTruthScore=trueAlter*falseAlter;



            long predictionsCount=0;
            //(τ(fi)=T:τ(fj)=F) (β(fi) > β(fj))
            for(int i=0;i<labels.size();i++){
                if(labels.get(i)==1) {
                    long zeroCount = labels.stream().skip(i).filter(l-> l.intValue()==0).count();
                    predictionsCount+=zeroCount;
                }

            }
            double accuracy= (0.0+predictionsCount)/groundTruthScore;
            accuracyTotal+=accuracy;

        }
        return accuracyTotal/groupedCorrectness.size();

    }

    public void dumpCorrectnessInfo(BufferedWriter bufferedWriter) {

        StringBuilder sb=new StringBuilder();
        sb.append(CorrectnessInfo.getTabReprHeader()+"\n");
        sb.append(Joiner.on('\n').join(correctnessInfos.stream().map(inf->inf.getTabRepr()).collect(Collectors.toList())));

        try {
           bufferedWriter.write(sb.toString()+"\n");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}
