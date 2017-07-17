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
public class ExtQuerySubs extends QueryWithSubstitution{

    public static Comparator<ExtQuerySubs> ActionPriorityComparator=new Comparator<ExtQuerySubs>() {
        @Override
        public int compare(ExtQuerySubs o1, ExtQuerySubs o2) {

            return Enums.ActionType.priorityComparator.compare(o1.getSourceActionType(), o2.getSourceActionType());
        }
    };

    public static Comparator<ExtQuerySubs> sizeComparator=Comparator.comparing(ExtQuerySubs::size);
    public static Comparator<ExtQuerySubs> rulesDepthComparator=Comparator.comparing(ExtQuerySubs::getRulesDepth);
    public static Comparator<ExtQuerySubs> freeVarsComparator=Comparator.comparing(ExtQuerySubs::freeVarsSize);
    public static Comparator<ExtQuerySubs> treeDepthComparator=Comparator.comparing(ExtQuerySubs::getTreeDepth);





    private final ExtQuerySubs parent;
    private  int rulesDepth;
    private  int treeDepth;
    private EvidenceNode evidenceNode;




    public ExtQuerySubs(IQuery query, Map<IVariable, ITerm> substitution, ExtQuerySubs parent, int treeDepth, int rulesDepth) {
        super(query,substitution);
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

    public ExtQuerySubs getParent() {
        return parent;
    }

    public int size(){return getQuery().getLiterals().size();}

    public int freeVarsSize() {
        return getQuery().getVariables().size();

    }
}
