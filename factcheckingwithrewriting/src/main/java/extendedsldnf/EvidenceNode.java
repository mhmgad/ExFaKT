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
    private Enums.BindingSource bindingSource;
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

    public void setBindingSource(Enums.BindingSource source) {
        this.bindingSource=source;
    }

    public Enums.BindingSource getBindingSource() {
        return bindingSource;
    }

    public void setRule(IRule rule) {
        this.rule = rule;
    }

    public void setVariableBindingMap(Map<IVariable, ITerm> variableBindingMap) {
        this.variableBindingMap = variableBindingMap;
    }

    public enum Type{FACT,TEXT,BUILT_IN,BIND,RULE,ORG};

    ILiteral queryLiteral;
    Type type = Type.ORG;

    public EvidenceNode(ILiteral queryLiteral, Type type) {
        this.queryLiteral = queryLiteral;
        this.type = type;
    }

    public ILiteral getQueryLiteral() {
        return queryLiteral;
    }

    public void setQueryLiteral(ILiteral queryLiteral) {
        this.queryLiteral = queryLiteral;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setTextResults(ITextResult textResults){
        this.textResults=textResults;

    }

    @Override
    public String toString() {
        return "Evidence{" +
                "literal=" + queryLiteral +
                ", type=" + type + ((type==Type.BIND)? "("+bindingSource+")":"")+
                ((type ==Type.TEXT)?
                        ", text=" + textResults.readable():"" )+
                ((type ==Type.RULE)?
                        ", rule=" + rule:"" )+
                ((type ==Type.BIND)?
                        ", variableMap=" + variableBindingMap:"" )+
                "}"+((type ==Type.TEXT||type ==Type.FACT)? "**":"" );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvidenceNode)) return false;
        EvidenceNode that = (EvidenceNode) o;
        return getBindingSource() == that.getBindingSource() &&
                Objects.equals(getQueryLiteral(), that.getQueryLiteral()) &&
                getType() == that.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBindingSource(), getQueryLiteral(), getType());
    }

    @Override
    public EvidenceNode clone(){
        EvidenceNode node=new EvidenceNode(this.queryLiteral,this.type);
        node.bindingSource=bindingSource;
        node.textResults=textResults;
        node.rule=rule;
        return node;
    }
}
