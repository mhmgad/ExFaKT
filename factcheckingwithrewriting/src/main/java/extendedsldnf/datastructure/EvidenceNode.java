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
    //private Enums.BindingSource bindingSource;
    private IRule rule;
    private Map<IVariable, ITerm> variableBindingMap;

 /*   @Override
    public double getWeight() {
        return 0;
    }

    @Override
    public double getCost() {
        return 0;
    }*/


    private ITextResult textResults;

    //public void setBindingSource(Enums.BindingSource source) {
    //    this.bindingSource=source;
    //}

    //public Enums.BindingSource getBindingSource() {
    //    return bindingSource;
    //}

    public void setRule(IRule rule) {
        this.rule = rule;
    }

    public void setVariableBindingMap(Map<IVariable, ITerm> variableBindingMap) {
        this.variableBindingMap = variableBindingMap;
    }


    ILiteral queryLiteral;
    Enums.ActionType sourceActionType = Enums.ActionType.ORG;

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
        /*node.bindingSource=bindingSource;*/
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


}
