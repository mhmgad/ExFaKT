package extendedsldnf.datastructure;

import extendedsldnf.EvidenceNode;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.topdown.QueryWithSubstitution;

import java.util.Map;

/**
 * Created by gadelrab on 3/6/17.
 */
public class ExtendedQueryWithSubstitution extends QueryWithSubstitution{

    private EvidenceNode evidenceNode;

//    public Map<IVariable, Enums.BindingSource> getSubstitutionSources() {
//        return substitutionSources;
//    }

    //public enum ExpansionMethod {BUILT_IN,KG_VALID,TEXT_VALID,RULES,ORG,GREEDY}

    IQuery referenceQuery;

//    Map<IVariable, Enums.BindingSource> substitutionSources;

   /* public ExpansionMethod getSource() {
        return source;
    }

    public void setSource(ExpansionMethod source) {
        this.source = source;
    }

    public ExpansionMethod source;*/

    public ExtendedQueryWithSubstitution(IQuery query, Map<IVariable, ITerm> substitution /*,ExpansionMethod source,*//* Map<IVariable, Enums.BindingSource> substitutionSources*/) {
        this(query,substitution,/*source,*/query/*,substitutionSources*/);

    }

    public ExtendedQueryWithSubstitution(IQuery query, Map<IVariable, ITerm> substitution, /*ExpansionMethod source,*/ IQuery referenceQuery/*, Map<IVariable, Enums.BindingSource> substitutionSources*/) {
        super(query,substitution);
        /*this.source=source;*/
        this.referenceQuery=referenceQuery;
//        this.substitutionSources=substitutionSources;

    }



    public String toString() {
        return super.toString() /*+ " | " + substitutionSources*/ /*+ " | " + source.toString()*/;
    }

    public void setEvidenceNode(EvidenceNode evidenceNode) {
        this.evidenceNode = evidenceNode;
    }

    public EvidenceNode getEvidenceNode() {
        return evidenceNode;
    }
}
