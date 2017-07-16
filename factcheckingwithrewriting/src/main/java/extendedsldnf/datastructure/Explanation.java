package extendedsldnf.datastructure;

import com.google.common.base.Joiner;
import extendedsldnf.CostAccumulator;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by gadelrab on 3/8/17.
 */

public class Explanation implements Comparable<Explanation> {

    List<EvidenceNode> evidenceNodes=new LinkedList<>();
    private CostAccumulator cost;


    public Explanation() {
        this(new LinkedList<EvidenceNode>());

    }

    public Explanation(List<EvidenceNode> evidenceNodes) {
        this.evidenceNodes = evidenceNodes;
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
        return ((int) evidenceNodes.stream().filter(EvidenceNode::isTextMention).count());
    }

    public int getKGEvidencesCount(){
        return ((int) evidenceNodes.stream().filter(EvidenceNode::isKGFact).count());
    }

    public int getVerificationCount(){
        return (int) evidenceNodes.stream().filter(EvidenceNode::isVerification).count();
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
}
