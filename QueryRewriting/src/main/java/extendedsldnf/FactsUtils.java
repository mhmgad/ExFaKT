package extendedsldnf;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.topdown.TopDownHelper;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;
import org.deri.iris.utils.TermMatchingAndSubstitution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FactsUtils {

    /**
     * Tries to find a fact that matches the given query.
     * The variableMap will be populated if a matching fact was found.
     * @param queryLiteral the given query
     *
     * @return true if a matching fact is found, false otherwise
     */
    public static boolean getMatchingFacts(ILiteral queryLiteral, List<Map<IVariable, ITerm>> variableMapList, IFacts factsSource) {

        // Check all the facts
        for ( IPredicate factPredicate : factsSource.getPredicates() ) {
            // Check if the predicate and the arity matches
            if ( TopDownHelper.match(queryLiteral, factPredicate) ) {
                // We've found a match (predicates and arity match)
                // Is the QueryTuple unifiable with one of the FactTuples?

                IRelation factRelation = factsSource.get(factPredicate);
                fillVariableMaps(queryLiteral, factRelation, variableMapList);


            }
        }
        if (variableMapList.isEmpty())
            return false; // No fact found

        return true;
    }

    public static void fillVariableMaps(ILiteral queryLiteral, IRelation factRelation, List<Map<IVariable, ITerm>> variableMapList) {
        // Substitute variables into the query
        for ( int i = 0; i < factRelation.size(); i++ ) {
            ITuple queryTuple = queryLiteral.getAtom().getTuple();
            boolean tupleUnifyable = false;
            ITuple factTuple = factRelation.get(i);
            Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
            tupleUnifyable = TermMatchingAndSubstitution.unify(queryTuple, factTuple, variableMap);
            if (tupleUnifyable) {
                queryTuple = TermMatchingAndSubstitution.substituteVariablesInToTuple(queryTuple, variableMap);
                variableMapList.add(variableMap);
            }
        }
    }

    public static int getMatchingFactCount(ILiteral literal,IFacts factsSource) {
        List<Map<IVariable, ITerm>> varsMapList=new LinkedList<>();
        FactsUtils.getMatchingFacts(literal,varsMapList,factsSource);
        return varsMapList.size();
    }
}
