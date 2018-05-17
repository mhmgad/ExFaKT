package extendedsldnf.datastructure;

import com.google.common.base.Joiner;
import com.google.gson.annotations.JsonAdapter;
import de.mpii.dataprocessing.util.FactUtils;
import extendedsldnf.CostAccumulator;
import org.apache.commons.lang.StringEscapeUtils;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.supercsv.cellprocessor.constraint.DMinMax;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import utils.Converter;
import utils.StringUtils;
import utils.json.adapters.IQueryAdapter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import output.writers.SerializableData;

/**
 * Created by gadelrab on 3/8/17.
 */

public class Explanation implements Comparable<Explanation>,SerializableData {



//    private static final CellProcessor[] processors=new CellProcessor[] {
//            new NotNull(), // id
//            new NotNull(), // query
//            new NotNull(), // explanation
//            new LMinMax(0L, LMinMax.MAX_LONG) , // genOrder
//            new NotNull(), // method
//            new DMinMax(0.0, DMinMax.MAX_DOUBLE) // quality
//    };


    private final String method;
    private final int genOrder;
    private final String id;
    List<EvidenceNode> evidenceNodes=new LinkedList<>();
    private CostAccumulator cost;
    @JsonAdapter(IQueryAdapter.class)
    private IQuery query;
    private double quality;


    public Explanation(IQuery query,String method,int genOrder) {
        this(new LinkedList<EvidenceNode>(), query, method, genOrder);

    }

    public Explanation(List<EvidenceNode> evidenceNodes,IQuery query,String method,int genOrder) {
        this.evidenceNodes = evidenceNodes;
        this.method=method;
        this.genOrder=genOrder;
        this.query=query;
        this.id=query+"_"+method+"_"+genOrder;
    }


    public void add(EvidenceNode evidenceNode){
        evidenceNodes.add(evidenceNode);
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder("Explanation{[\n ");
        sb.append(Joiner.on("\n,").join(evidenceNodes));
        sb.append("\n]" +
                "\n"+cost.toString()+"}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Explanation)) return false;
        Explanation that = (Explanation) o;
        return Objects.equals(evidenceNodes, that.evidenceNodes) ;/*&&
                    Objects.equals(substitutions, that.substitutions);*/
    }

    @Override
    public int hashCode() {
        return Objects.hash(evidenceNodes);//, substitutions);
    }

    public int getTextEvidencesCount(){
        return ((int) getTextEvidencesStream().count());
    }

    public Stream<EvidenceNode> getTextEvidencesStream() {
        return evidenceNodes.stream().filter(EvidenceNode::isTextMention);
    }

    public int getKGEvidencesCount(){
        return ((int) evidenceNodes.stream().filter(EvidenceNode::isKGFact).count());
    }

    public int getVerificationCount(){
        return (int) getVerificationEvidenceNodesStream().count();
    }

    public List<EvidenceNode> getVerificationEvidences(){
        return getVerificationEvidenceNodesStream().collect(Collectors.toList());
    }

    public List<EvidenceNode> getRulesEvidences(){
        return  evidenceNodes.stream().filter(EvidenceNode::isRuleExpansion).collect(Collectors.toList());
    }

    public int getRulesEvidencesCount(){
        return  getRulesEvidences().size();
    }

    public Stream<EvidenceNode> getVerificationEvidenceNodesStream() {
        return evidenceNodes.stream().filter(EvidenceNode::isVerification);
    }


    public boolean isEmpty(){
        return  evidenceNodes.isEmpty();
    }

    public boolean isDirectEvidence(){
        return evidenceNodes.size()==1;
    }

    public boolean inKG(){
        return  (evidenceNodes.size()==1) && evidenceNodes.get(0).isKGFact();
    }

    public boolean hasEvidencesFromKGOnly(){
        return  (!isEmpty())&& (getTextEvidencesCount()==0);
    }


    public boolean hasEvidencesFromTextOnly(){
        return  (!isEmpty())&& (getKGEvidencesCount()==0);
    }

    public int usedRulesCount(){
        return ((int) evidenceNodes.stream().filter(EvidenceNode::isRuleExpansion).count());
    }

    public int varBindingCount(){
        return ((int) evidenceNodes.stream().filter(EvidenceNode::isVariableBinding).count());
    }


    public  boolean hasTextEvidences() {

        return getTextEvidencesCount()>0;
    }

    public void setCost(CostAccumulator cost) {
        this.cost = cost;
    }

    public CostAccumulator getCost(){
        return cost;
    }

    @Override
    public int compareTo(Explanation that) {
        return cost.compareTo(that.cost);
    }

    public  int size() {
        return evidenceNodes.size();
    }


    /**
     * sum(evidencesQuality) for the grounded atoms
     * @return
     */
    public  double getQualityScore() {

        return getVerificationEvidenceNodesStream().mapToDouble(EvidenceNode::nodeQuality).sum();


    }

    public void setQuery(IQuery query) {
        this.query = query;
    }

    public String getMethod() {
        return method;
    }

    public int getGenOrder() {
        return genOrder;
    }

    public String getId() {
        return id;
    }

    public List<EvidenceNode> getEvidenceNodes() {
        return evidenceNodes;
    }

    public void setEvidenceNodes(List<EvidenceNode> evidenceNodes) {
        this.evidenceNodes = evidenceNodes;
    }

    public IQuery getQuery() {
        return query;
    }

    public String getReadableString() {
        StringBuilder readable=new StringBuilder("\nExplan "+genOrder+" -------");
        readable.append("\nFacts");

        readable.append(StringUtils.indent("\n-"+Joiner.on("\n-").join(getVerificationEvidences().stream().map(EvidenceNode::getReadableString).collect(Collectors.toList()))));

        if(usedRulesCount()>0) {
            readable.append("\nRules");
            readable.append(StringUtils.indent("\n-"+Joiner.on("\n-").join(getRulesEvidences().stream().map(EvidenceNode::getReadableString).collect(Collectors.toList()))));
        }

        readable.append("\n-------");
        readable.append("\nRelevance Score?\t");
        readable.append("\nFailure Reason?\t");
        readable.append("\n-------\n");

        return readable.toString();
    }

    public void setQuality() {
        this.quality = getQualityScore();
        this.getEvidenceNodes().forEach(EvidenceNode::setQuality);
    }

    public double getQuality() {
        return quality;
    }

    @Override
    public String toJSON() {
        return null;
    }

    @Override
    public String toTriple() {
        return null;
    }

    @Override
    public String toTsv() {
        return null;
    }

    final static String [] HEADERS=new String[] {"id","query","explanation","genOrder","method","quality","direct","rulesNum","factsNum", "fromText", "costSteps", "CostTime"};

    @Override
    public String toCsv() {

        String readableExplanation= StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeHtml(getBriefReadableString()));
        String readableQuery= StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeHtml(Converter.toFact(query).toReadableString()));
        String readableId= StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeHtml(id));
        String readableMethod= StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeHtml(method));

        int rulesNum=getRulesEvidencesCount();
        int factsNum=getVerificationCount();
        int fromText=getTextEvidencesCount();
        int costSteps= cost.getTotalCost();

        List<Object> explanationLine = Arrays.asList(new Object[] { readableId, readableQuery,readableExplanation,genOrder, readableMethod,quality, isDirectEvidence(),rulesNum,factsNum, fromText, costSteps});

        assert(explanationLine.size()==HEADERS.length);


        return Joiner.on(", ").join(explanationLine);
    }



    /**
     * Creates a compressed version of the explanation. This version has only the grounded facts with the first textual evidence if exists
     * @return
     */
    public String getBriefReadableString() {
        String text="";
        if(getVerificationCount()>1){
            text= Joiner.on("\n").join(getVerificationEvidenceNodesStream().map(EvidenceNode::getBriefReadableString).map(st-> "*. "+st).collect(Collectors.toList()));
        }else{
            text= Joiner.on("\n").join(getVerificationEvidenceNodesStream().map(EvidenceNode::getBriefReadableString).collect(Collectors.toList()));
        }
        return text;
    }

    public static void main(String[] args) {

        System.out.println("<testMe_Please>".replace(":"," ").replaceAll("^([a-z]{2,3})/","").replaceAll("(_\\(.+?\\))$"," ").trim().replace("_"," ").replaceAll("(?=\\p{Upper})"," ").replaceAll(" +", " "));
        System.out.println(FactUtils.getReadablePredicateName("<testMe_Please>"));

//        Parser p=new Parser();
//        try {
//            p.parse("c('ss\\\'ss','f')");
//        } catch (ParserException e) {
//            e.printStackTrace();
//        }
//        System.out.println(StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeHtml("Max-Planck-Institut' für\n jhzjhddj")));
//        System.out.println(StringEscapeUtils.escapeCsv("Max-Planck-Institut' für\n jhzjhddj"));

    }
}
