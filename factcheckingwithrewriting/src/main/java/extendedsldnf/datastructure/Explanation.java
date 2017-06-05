package extendedsldnf.datastructure;

import com.google.common.base.Joiner;
import extendedsldnf.EvidenceNode;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import utils.Enums;

import java.util.*;

/**
 * Created by gadelrab on 3/8/17.
 */

public class Explanation {

    List<EvidenceNode> evidenceNodes=new LinkedList<>();
//    List<ExtendedQueryWithSubstitution.ExpansionMethod> sources=new LinkedList<>();


//    private Map<IVariable, ITerm> substitutions;
//    private Map<IVariable, Enums.BindingSource> substitutionsSources;

    public Explanation() {
        this(new LinkedList<EvidenceNode>()/*,new LinkedList<ExtendedQueryWithSubstitution.ExpansionMethod>()*/,new HashMap<IVariable,ITerm>(),new HashMap<IVariable,Enums.BindingSource>());

    }

    public Explanation(List<EvidenceNode> evidenceNodes/*, List<ExtendedQueryWithSubstitution.ExpansionMethod> sources*/, HashMap<IVariable, ITerm> substitutions, HashMap<IVariable, Enums.BindingSource> subtitutionsSources) {
        this.evidenceNodes = evidenceNodes;
        /*this.sources = sources;*/
//        this.substitutions=substitutions;
//        this.substitutionsSources=subtitutionsSources;
    }

//    public void add(ILiteral selectedLiteral, ExtendedQueryWithSubstitution.ExpansionMethod source) {
//        literals.add(0,selectedLiteral);
//        sources.add(0,source);
//    }

    public void add(EvidenceNode evidenceNode){
        evidenceNodes.add(evidenceNode);
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder("Explanation{[\n ");
        sb.append(Joiner.on("\n,").join(evidenceNodes));
//        sb.append("]\n,sources:");
//        List<String> subSourcesRep=substitutions.keySet().stream().map(k-> '('+k.toString()+": "+substitutions.get(k).toString()+", "+((substitutionsSources.get(k)==null)? "ORG/CONST":substitutionsSources.get(k).toString())+')').collect(Collectors.toList());
//        sb.append(subSourcesRep.toString());
        sb.append("\n]}");
        return sb.toString();
    }



//    public void setSubstitutions(Map<IVariable,ITerm> substitutions) {
//        this.substitutions = substitutions;
//    }

//    public void setSubstitutionsSources(Map<IVariable,Enums.BindingSource> substitutionsSources) {
//        this.substitutionsSources = substitutionsSources;
//    }

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
}
