package datastructure;

import com.google.common.base.Joiner;
import extendedsldnf.datastructure.IQueryExplanations;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.IQuery;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CorrectnessInfo implements Comparable<CorrectnessInfo>{

    public static String[] header=new String[]{"Query","posExplans","negExplans","scoreSizePosExplan","scoreSizeNegExplan","sizeScore","sizeSlabel","posQuality","negQuality","queryqScore","qualityLabel","posDocsCount","negDocsCount","DocsCScore","DocsSizeLabel","GTLabel"};

    IQuery posQuery;
    IQuery negQuery;

    IQueryExplanations posExplanations;
    IQueryExplanations negExplanations;

    DecimalFormat dcf = new DecimalFormat("0.000");
    private int gTLabel;
    private int GTLabel;

    public CorrectnessInfo(IQuery posQuery, IQuery negQuery, IQueryExplanations posExplanations, IQueryExplanations negExplanations, int groundTruthLabel) {
        this.GTLabel=groundTruthLabel;
        this.posQuery = posQuery;
        this.negQuery = negQuery;
        this.posExplanations = posExplanations;
        this.negExplanations = negExplanations;
    }

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

//    public CorrectnessInfo(IQuery posQuery, IQuery negQuery, IQueryExplanations posExplanations, IQueryExplanations negExplanations) {
//       this( posQuery,  negQuery,  posExplanations,  negExplanations,0);
//    }

    public double getSizeScore(){
    // different between positiveExplanations Size score and negative ones
        return  posExplanations.getScoreOnSize()-negExplanations.getScoreOnSize();
    }

    @Override
    public String toString() {
        return "CorrectnessInfo{" +
                "posQuery=" + posQuery +
                ", negQuery=" + negQuery +
                ", correctnessScore="+ getSizeScore()+
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
            return Double.compare(this.getSizeScore(),o.getSizeScore());

    }

    public List<IQueryExplanations> getPosNegExplanations(){
        return Arrays.asList(posExplanations, negExplanations);
    }


    public double getExplanationsQuality(boolean isPositive){
        IQueryExplanations explanations = (isPositive) ? posExplanations : negExplanations;
        return explanations.getAvgQuality();

    }


    public static String getTabReprHeader(){


        List<String> headerWithNum = IntStream.range(0, header.length).mapToObj(i ->  "(" + i + ")" + header[i]).collect(Collectors.toList());
        return Joiner.on('\t').join(headerWithNum);
    }

    public String getTabRepr() {
        StringBuilder sb=new StringBuilder();
        sb.append(getPosQuery() + "\t" + posExplanations.size() + "\t" + negExplanations.size());
        sb.append("\t" + dcf.format(posExplanations.getScoreOnSize()) + "\t" + dcf.format(negExplanations.getScoreOnSize()) + "\t" + dcf.format(getSizeScore()) + "\t" + getSizeLabel());
        sb.append("\t" + dcf.format(getExplanationsQuality(true)) + "\t" + dcf.format(getExplanationsQuality(false)) + "\t" + dcf.format(getQualityScore()) + "\t" + getQualityLabel());
        sb.append("\t" + posExplanations.documentLevelCount() + "\t" + negExplanations.documentLevelCount() + "\t" +getDocsCountSocre() +"\t" + getDocsCountLabel());
        sb.append("\t"+ getGTLabel());

        return sb.toString();
    }

    public int getSizeLabel() {
       return (getSizeScore()>0)? 1:0;
    }


    public String getGroup(){
        IAtom a = posQuery.getLiterals().get(0).getAtom();
        return a.getPredicate().getPredicateSymbol()+" "+a.getTuple().get(0).toString();
    }

    public void setGTLabel(int label) {
        this.gTLabel=label;
    }


    public Object getGTLabel() {
        return GTLabel;
    }

    public double getQualityScore() {
        return getExplanationsQuality(true) - getExplanationsQuality(false);
    }

    public double getDocsCountSocre() {
        return posExplanations.documentLevelCount()-negExplanations.documentLevelCount();
    }

    public int getQualityLabel() {
        return (getQualityScore()>0)? 1:0;
    }

    public int getDocsCountLabel() {
        return (getDocsCountSocre()>0)? 1:0;
    }


}
