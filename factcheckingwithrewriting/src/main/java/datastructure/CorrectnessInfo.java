package datastructure;

import extendedsldnf.datastructure.IQueryExplanations;
import org.deri.iris.api.basics.IQuery;

import java.util.Arrays;
import java.util.List;

public class CorrectnessInfo implements Comparable<CorrectnessInfo>{

    IQuery posQuery;
    IQuery negQuery;

    IQueryExplanations posExplanations;
    IQueryExplanations negExplanations;

    public IQuery getPosQuery() {
        return posQuery;
    }

    public void setPosQuery(IQuery posQuery) {
        this.posQuery = posQuery;
    }

    public IQuery getNegQuery() {
        return negQuery;
    }

    public void setNegQuery(IQuery negQuery) {
        this.negQuery = negQuery;
    }

    public IQueryExplanations getPosExplanations() {
        return posExplanations;
    }

    public void setPosExplanations(IQueryExplanations posExplanations) {
        this.posExplanations = posExplanations;
    }

    public IQueryExplanations getNegExplanations() {
        return negExplanations;
    }

    public void setNegExplanations(IQueryExplanations negExplanations) {
        this.negExplanations = negExplanations;
    }

    public CorrectnessInfo(IQuery posQuery, IQuery negQuery, IQueryExplanations posExplanations, IQueryExplanations negExplanations) {
        this.posQuery = posQuery;
        this.negQuery = negQuery;
        this.posExplanations = posExplanations;
        this.negExplanations = negExplanations;
    }

    public double getScore(){
    // different between positiveExplanations Size score and negative ones
        return  posExplanations.getScoreOnSize()-negExplanations.getScoreOnSize();
    }

    @Override
    public String toString() {
        return "CorrectnessInfo{" +
                "posQuery=" + posQuery +
                ", negQuery=" + negQuery +
                ", correctnessScore="+getScore()+
                ", posExplScore=" +  posExplanations.getScoreOnSize() + " ("+posExplanations.size()+")"+
                ", negExplScore=" + negExplanations.getScoreOnSize() + " ("+negExplanations.size()+")"+
                '}';
    }


    @Override
    public int compareTo(CorrectnessInfo o) {
//        int strDiff= this.posQuery.getLiterals().get(0).compareTo(o.posQuery.getLiterals().get(0));
//        if(strDiff!=0)
//            return strDiff;
//        else
            return Double.compare(this.getScore(),o.getScore());

    }

    public List<IQueryExplanations> getPosNegExplanations(){
        return Arrays.asList(posExplanations, negExplanations);
    }


    public static String getTabReprHeader(){
        return "Query\tposExplans\tnegExplans\tscoreSizePosExplan\tscoreSizeNegExplan\tcorrectnessSizeScore\tlabel";
    }

    public String getTabRepr() {
        return getPosQuery()+"\t"+posExplanations.size()+"\t"+negExplanations.size()+"\t"+posExplanations.getScoreOnSize()+"\t"+negExplanations.getScoreOnSize()+"\t"+getScore()+"\t"+isCorrect();
    }

    public boolean isCorrect() {
       return getScore()>0;
    }
}
