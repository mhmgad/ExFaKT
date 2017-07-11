package extendedsldnf;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import text.ITextResult;
import text.IWeightedObject;
import utils.Enums;

import java.util.Map;
import java.util.Objects;

/**
 * Created by gadelrab on 4/26/17.
 */
public class EvidenceNode implements IWeightedObject {
    //private Enums.BindingSource bindingSource;
    private IRule rule;
    private Map<IVariable, ITerm> variableBindingMap;

    @Override
    public double getWeight() {
        return 0;
    }

    @Override
    public double getCost() {
        return 0;
    }


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


    ;

    ILiteral queryLiteral;
    Enums.ActionType type = Enums.ActionType.ORG;

    public EvidenceNode(ILiteral queryLiteral, Enums.ActionType type) {
        this.queryLiteral = queryLiteral;
        this.type = type;
    }

    public ILiteral getQueryLiteral() {
        return queryLiteral;
    }

    public void setQueryLiteral(ILiteral queryLiteral) {
        this.queryLiteral = queryLiteral;
    }

    public Enums.ActionType getType() {
        return type;
    }

    public void setType(Enums.ActionType type) {
        this.type = type;
    }

    public void setTextResults(ITextResult textResults){
        this.textResults=textResults;

    }

    @Override
    public String toString() {
        return "Evidence{" +
                "literal=" + queryLiteral +
                ", type=" + type + /*((type==Type.VAR_BIND)? "("+bindingSource+")":"")+*/
                ((type == Enums.ActionType.TEXT_VALID)?
                        ", text=" + textResults.readable():"" )+
                ((type == Enums.ActionType.RULE_EXPAND)?
                        ", rule=" + rule:"" )+
                ((isVariableBinding())?
                        ", variableMap=" + variableBindingMap:"" )+
                "}"+((type == Enums.ActionType.TEXT_VALID ||type == Enums.ActionType.KG_VALID)? "**":"" );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvidenceNode)) return false;
        EvidenceNode that = (EvidenceNode) o;
        return /*getBindingSource() == that.getBindingSource() &&*/
                Objects.equals(getQueryLiteral(), that.getQueryLiteral()) &&
                getType() == that.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(/*getBindingSource(),*/ getQueryLiteral(), getType());
    }

    @Override
    public EvidenceNode clone(){
        EvidenceNode node=new EvidenceNode(this.queryLiteral,this.type);
        /*node.bindingSource=bindingSource;*/
        node.textResults=textResults;
        node.rule=rule;
        return node;
    }

    public boolean isTextMention(){ return getType().equals(Enums.ActionType.TEXT_VALID);}
    public boolean isKGFact(){ return getType().equals(Enums.ActionType.KG_VALID);}

    public  boolean isVerification() {
        return isTextMention()||isKGFact();
    }

    public  boolean isVariableBinding() {
        return (type == Enums.ActionType.TEXT_BIND||type == Enums.ActionType.KG_BIND||type == Enums.ActionType.GREEDY_BIND);
    }

    public  boolean isRuleExpansion() {
        return getType().equals(Enums.ActionType.RULE_EXPAND);
    }

}
