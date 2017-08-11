package extendedsldnf.datastructure;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import text.ITextResult;
import utils.Enums;

import java.util.Map;
import java.util.Objects;

/**
 * Created by gadelrab on 4/26/17.
 */
public class EvidenceNode{
    private IRule rule;
    private Map<IVariable, ITerm> variableBindingMap;
    private ITextResult textResults;
    ILiteral queryLiteral;
    Enums.ActionType sourceActionType = Enums.ActionType.ORG;
    private int treeDepth;
    private int rulesDepth;


    public void setRule(IRule rule) {
        this.rule = rule;
    }

    public void setVariableBindingMap(Map<IVariable, ITerm> variableBindingMap) {
        this.variableBindingMap = variableBindingMap;
    }

    public EvidenceNode(ILiteral queryLiteral, Enums.ActionType sourceActionType) {
        this.queryLiteral = queryLiteral;
        this.sourceActionType = sourceActionType;
    }

    public ILiteral getQueryLiteral() {
        return queryLiteral;
    }

    public void setQueryLiteral(ILiteral queryLiteral) {
        this.queryLiteral = queryLiteral;
    }

    public Enums.ActionType getSourceActionType() {
        return sourceActionType;
    }

    public void setSourceActionType(Enums.ActionType sourceActionType) {
        this.sourceActionType = sourceActionType;
    }

    public void setTextResults(ITextResult textResults){
        this.textResults=textResults;

    }

    @Override
    public String toString() {
        return "Evidence{" +
                "literal=" + queryLiteral +
                ", sourceActionType=" + sourceActionType + /*((sourceActionType==Type.VAR_BIND)? "("+bindingSource+")":"")+*/
                ((sourceActionType == Enums.ActionType.TEXT_VALID)?
                        ", text=" + textResults.readable():"" )+
                ((sourceActionType == Enums.ActionType.RULE_EXPAND)?
                        ", rule=" + rule:"" )+
                ((isVariableBinding())?
                        ", variableMap=" + variableBindingMap:"" )+
                ", rulesDepth="+ this.rulesDepth+
                ", treeDepth="+ this.rulesDepth+
                "}"+((sourceActionType == Enums.ActionType.TEXT_VALID || sourceActionType == Enums.ActionType.KG_VALID)? "**":"" );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvidenceNode)) return false;
        EvidenceNode that = (EvidenceNode) o;
        return /*getBindingSource() == that.getBindingSource() &&*/
                Objects.equals(getQueryLiteral(), that.getQueryLiteral()) &&
                getSourceActionType() == that.getSourceActionType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(/*getBindingSource(),*/ getQueryLiteral(), getSourceActionType());
    }

    @Override
    public EvidenceNode clone(){
        EvidenceNode node=new EvidenceNode(this.queryLiteral,this.sourceActionType);
        node.textResults=textResults;
        node.rule=rule;
        return node;
    }

    public boolean isTextMention(){ return getSourceActionType().equals(Enums.ActionType.TEXT_VALID);}
    public boolean isKGFact(){ return getSourceActionType().equals(Enums.ActionType.KG_VALID);}

    public  boolean isVerification() {
        return isTextMention()||isKGFact();
    }

    public  boolean isVariableBinding() {
        return (sourceActionType == Enums.ActionType.TEXT_BIND|| sourceActionType == Enums.ActionType.KG_BIND|| sourceActionType == Enums.ActionType.GREEDY_BIND);
    }

    public  boolean isRuleExpansion() {
        return getSourceActionType().equals(Enums.ActionType.RULE_EXPAND);
    }

    /**
     * Quality of the node is measured as the quality of the fact / depth of the node
     * Quality = 1 if it is KG fact and value [0-1] for Text. For now text quality =0.5
     * For other nodes it returns 0
     * @return
     */
    public  double nodeQuality() {

        if(isVariableBinding()||isRuleExpansion())
            return 0;
        double sourceQ= isKGFact()? 1:0.5;
        return sourceQ/rulesDepth;
    }

    public void setTreeDepth(int treeDepth) {
        this.treeDepth = treeDepth;
    }

    public void setRulesDepth(int rulesDepth) {
        this.rulesDepth = rulesDepth;
    }

    public  int getRetrievedDocsCount() {
        if(!isTextMention()) return 0;
        return textResults.getDocumentsCount();
    }
}
