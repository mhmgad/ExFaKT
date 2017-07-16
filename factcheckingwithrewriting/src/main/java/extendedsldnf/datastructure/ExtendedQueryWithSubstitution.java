package extendedsldnf.datastructure;

import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.topdown.QueryWithSubstitution;
import utils.Enums;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by gadelrab on 3/6/17.
 */
public class ExtendedQueryWithSubstitution extends QueryWithSubstitution{

    public static Comparator ActionPriorityComparator=new Comparator<ExtendedQueryWithSubstitution>() {
        @Override
        public int compare(ExtendedQueryWithSubstitution o1, ExtendedQueryWithSubstitution o2) {
            return Enums.ActionType.priorityComparator.compare(o1.getSourceActionType(),o2.getSourceActionType());
        }
    };
    private final ExtendedQueryWithSubstitution parent;
    private  int rulesDepth;
    private  int treeDepth;
    private EvidenceNode evidenceNode;


//    IQuery referenceQuery;



   /* public ExtendedQueryWithSubstitution(IQuery query, Map<IVariable, ITerm> substitution,int treeDepth,int rulesDepth ) {
        this(query,substitution,query,treeDepth,rulesDepth);

    }*/

    public ExtendedQueryWithSubstitution(IQuery query, Map<IVariable, ITerm> substitution, ExtendedQueryWithSubstitution parent, int treeDepth, int rulesDepth) {
        super(query,substitution);
//        this.referenceQuery=referenceQuery;
        this.treeDepth=treeDepth;
        this.rulesDepth=rulesDepth;
        this.parent=parent;
    }

    public String toString() {
        return super.toString();
    }

    public void setEvidenceNode(EvidenceNode evidenceNode) {
        this.evidenceNode = evidenceNode;
    }

    public EvidenceNode getEvidenceNode() {
        return evidenceNode;
    }

    public int getTreeDepth() {
        return treeDepth;
    }

    public int getRulesDepth() {
        return rulesDepth;
    }

    public Enums.ActionType getSourceActionType() {
        return evidenceNode.getSourceActionType();
    }

    public ExtendedQueryWithSubstitution getParent() {
        return parent;
    }
}
