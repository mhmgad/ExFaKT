package extendedsldnf.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Fact;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import text.FactSpottingResult;
import utils.Converter;
import utils.Enums;
import utils.StringUtils;
import utils.json.adapters.ILiteralAdapter;
import utils.json.adapters.IRuleAdapter;

import javax.ws.rs.NotSupportedException;
import java.util.Map;
import java.util.Objects;

/**
 * Created by gadelrab on 4/26/17.
 */
public class EvidenceNode{

    //    @JsonAdapter(IRuleAdapter.class)
    private IRule rule;
    //TODO to be fixed later
    //@Expose(deserialize = false)
    //private Map<IVariable, ITerm> variableBindingMap;
    private FactSpottingResult textResults;

//    @JsonAdapter(ILiteralAdapter.class)
    ILiteral queryLiteral;
    Enums.ActionType sourceActionType = Enums.ActionType.ORG;
    private int treeDepth;
    private int rulesDepth;
    private double quality;

    public IRule getRule() {
        return rule;
    }

    public FactSpottingResult getTextResults() {
        return textResults;
    }

    public void setRule(IRule rule) {
        this.rule = rule;
    }

    public void setVariableBindingMap(Map<IVariable, ITerm> variableBindingMap) {
        //this.variableBindingMap = variableBindingMap;
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

    public void setTextResults(FactSpottingResult textResults){
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
                        ", rule=" + rule:"" )
                +
//                ((isVariableBinding())?
//                        ", variableMap=" + variableBindingMap:"" )+
                ","+
                " rulesDepth="+ this.rulesDepth+
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
        return sourceQ/(Math.max(rulesDepth,1));
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

    public String getReadableQuery(){
        try {
            BinaryFact fact=Converter.toFact(queryLiteral);
            return fact.toSearchableString().replace("\t"," ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getReadableString() {
        try {
        if(isRuleExpansion())
            return rule.toString();
        else
            if(isKGFact()) {
                    return Converter.toFact(queryLiteral).toReadableString()+"\t KG fact";
            }
            else
                if(isTextMention()){
                    String fact= Converter.toFact(queryLiteral).toReadableString()+"\t from the text";
                    fact+="\n"+ StringUtils.indent(getTextResults().getEvidence().getReadableString());
                    return fact;
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public void setQuality() {
        this.quality=nodeQuality();
    }

    /**
     * returns a brief repersentation for the
     * @return
     */
    public String getBriefReadableString(){


        if(!isVerification()){
            throw new NotSupportedException("not yet supported");
        }
        if(isKGFact()){
            try {
                return Converter.toFact(queryLiteral).toReadableString();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if(isTextMention()){
            return textResults.getEvidence().getDocuments().get(0).getBriefReadableString();
        }
        return null;

    }


//    public static class Serializer implements JsonSerializer<EvidenceNode>, JsonDeserializer<EvidenceNode> {
//
//        @Override
//        public JsonElement serialize(EvidenceNode evidenceNode, Type type, JsonSerializationContext jsonSerializationContext) {
//
//        }
//
//        @Override
//        public EvidenceNode deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//            return null;
//        }
//    }

    public Fact getQueryAsFact() {
        try {
            return Converter.toFact(queryLiteral);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getQueryAsReadableString() {
        return getQueryAsFact().toReadableString();
    }
}
