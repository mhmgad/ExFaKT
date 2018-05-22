package extendedsldnf;

import config.Configuration;
import extendedsldnf.datastructure.*;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.evaluation.topdown.ILiteralSelector;
import org.deri.iris.evaluation.topdown.StandardLiteralSelector;
import org.deri.iris.storage.IRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import text.ITextConnector;
import utils.SyncCounter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static utils.Enums.ActionType.ORG;

/**
 * Created by gadelrab on 7/14/17.
 */
public class ItrSLDEvaluator extends RecSLDEvaluator {



    private Logger logger = LoggerFactory.getLogger(getClass());

    AbstractQueriesPool.ComparisionMethod compareMethod = AbstractQueriesPool.ComparisionMethod.DFS;
//
//    public HeuristicBasedEvaluator(IExtendedFacts facts, List<IRule> rules,AbstractQueriesPool.ComparisionMethod compareMethod) {
//        super(facts, rules);
//        this.compareMethod=compareMethod;
//    }

    public ItrSLDEvaluator(IExtendedFacts facts, List<IRule> rules, ITextConnector textConnector, Configuration.PartialBindingType partialBindingType, boolean suspectsFromKG, AbstractQueriesPool.ComparisionMethod compareMethod, int maxExplanations, int maxRuleDepth) {
        super(facts, rules, textConnector, partialBindingType, suspectsFromKG, maxExplanations, maxRuleDepth);
        this.compareMethod = compareMethod;

    }


    @Override
    public IRelation evaluate(IQuery query) throws EvaluationException {

//        System.out.println("Query: "+query+"\tMaxDepth: "+this.getMaxRuleDepth()+"\tMaxExplans: "+getMaxExplanations());
        ExtQuerySubs extendedQueryWithSubstitution = new ExtQuerySubs(query, new HashMap<>(), null, 0, 0);
        extendedQueryWithSubstitution.setEvidenceNode(new EvidenceNode(null, ORG));

        CostAccumulator costAccumulator = new CostAccumulator();
        IQueriesPool pool = QueriesPoolFactory.getPool(compareMethod);
        pool.add(extendedQueryWithSubstitution);
        List<Explanation> solutions = findSolutionsIteratively(pool, costAccumulator, query, new SyncCounter());

        

        QueryExplanations returnedSolution = new QueryExplanations(query, solutions, costAccumulator.clone());

        logger.debug("------------");
        //logger.debug("Relation " + relation);
        logger.debug("Original Query: " + query);
        logger.debug("Solutions: " + solutions);

        return returnedSolution;
    }

    private List<Explanation> findSolutionsIteratively(IQueriesPool pool, CostAccumulator costAccumulator, IQuery orgQuery, SyncCounter counter) {



        List<Explanation> explanations = new LinkedList<>();

        ILiteralSelector literalSelector = new StandardLiteralSelector();
//        if(compareMethod.equals(AbstractQueriesPool.ComparisionMethod.HEURISTIC)){
//            literalSelector=new OptimizedLiteralSelector(super.getFacts());
//        }


        while (!pool.isEmpty()) {

            //Stop when reaches enough explanations
            if (explanations.size() >= getMaxExplanations())
                break;

            // Global selection
            ExtQuerySubs chosenQuery = pool.selectQuery();

            logger.debug("Selected Query:" + chosenQuery.toString() + " " + chosenQuery.getEvidenceNode().toString());
            logger.debug("Pool Size:" + pool.size());

            IQuery newQuery = chosenQuery.getQuery();

            // Empty query i.e. Success
            if (newQuery.getLiterals().isEmpty()) {
                logger.debug("VarMap: " + chosenQuery.getSubstitution());
                explanations.add(createExplanation(chosenQuery, costAccumulator,orgQuery,counter.getCount()));
                continue;
            }

            //stop when maximum nesting reached
            if (chosenQuery.getRulesDepth() > getMaxRuleDepth())
                continue;

            // Local literal selection
            ILiteral selectedLiteral = literalSelector.select(newQuery.getLiterals());


            try {
                // Getting sub-queries
                List<ExtQuerySubs> subqueries = getSubQueryList(chosenQuery, selectedLiteral, costAccumulator);
                if(subqueries.size()>1000) {
                    System.out.println("["+getClass().getName()+"] Pool explosion with "+subqueries.size()+" subs (old-size="+pool.size()+") "+selectedLiteral);
                    continue;
                }
                logger.debug("Subqueries: " + subqueries);
                logger.debug("Number of Subqueries: " + subqueries.size());
                pool.addAll(subqueries);
            } catch (EvaluationException e) {
                e.printStackTrace();
            }

            logger.debug("Pool Size after:" + pool.size());

        }


        return explanations;
    }

    @Override
    /**
     * Override the original method to loop over all parents to collect the path
     */
    public Explanation createExplanation(ExtQuerySubs query, CostAccumulator costAccumulator, IQuery orgQuery, int generationOrder) {
        Explanation solution = new Explanation(orgQuery, getClass().getName()+"-"+compareMethod,generationOrder);

        for (ExtQuerySubs currentQuery = query; currentQuery != null; currentQuery = currentQuery.getParent()) {
            if (currentQuery.getEvidenceNode().getSourceActionType() != ORG) {
                solution.add(currentQuery.getEvidenceNode());
            }
        }

        solution.setCost(costAccumulator.clone());
        return solution;


    }
}
