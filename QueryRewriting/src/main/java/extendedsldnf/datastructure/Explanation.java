package extendedsldnf.datastructure;

import com.google.common.base.Joiner;
import extendedsldnf.CostAccumulator;
import org.deri.iris.api.basics.IQuery;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gadelrab on 3/8/17.
 */

public class Explanation implements Comparable<Explanation> {

    private final String method;
    private final int genOrder;
    private final String id;
    List<EvidenceNode> evidenceNodes=new LinkedList<>();
    private CostAccumulator cost;
    private IQuery query;


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
}
