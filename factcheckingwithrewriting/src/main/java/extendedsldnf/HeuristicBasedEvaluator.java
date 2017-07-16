package extendedsldnf;

import config.Configuration;
import extendedsldnf.datastructure.*;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.topdown.ILiteralSelector;
import org.deri.iris.evaluation.topdown.StandardLiteralSelector;
import org.deri.iris.storage.IRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import text.ITextConnector;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static utils.Enums.ActionType.ORG;

/**
 * Created by gadelrab on 7/14/17.
 */
public class HeuristicBasedEvaluator extends ExtendedSLDNFEvaluator {

    private Logger logger = LoggerFactory.getLogger(getClass());


    public HeuristicBasedEvaluator(IExtendedFacts facts, List<IRule> rules) {
        super(facts, rules);
    }

    public HeuristicBasedEvaluator(IExtendedFacts facts, List<IRule> rules, ITextConnector textConnector, Configuration.PartialBindingType partialBindingType, boolean suspectsFromKG) {
        super(facts, rules, textConnector, partialBindingType, suspectsFromKG);
    }


    @Override
    public IRelation evaluate(IQuery query) throws EvaluationException {

        ExtendedQueryWithSubstitution extendedQueryWithSubstitution = new ExtendedQueryWithSubstitution(query, new HashMap<IVariable, ITerm>(),null,0,0);
        extendedQueryWithSubstitution.setEvidenceNode(new EvidenceNode(null,ORG));

        CostAccumulator costAccumulator=new CostAccumulator();
        IQueriesPool pool=new DefaultQueriesPool();
        List<Explanation> solutions = findSolutionsIteratively(pool,costAccumulator);

        QueryExplanations returnedSolution=new QueryExplanations(query,solutions);

        logger.debug("------------");
        //logger.debug("Relation " + relation);
        logger.debug("Original Query: " + query);
        logger.debug("Solutions: "+ solutions);

        return returnedSolution;
    }

    private List<Explanation> findSolutionsIteratively(IQueriesPool pool, CostAccumulator costAccumulator) {

        List<Explanation> explanations=new LinkedList<>();

        ILiteralSelector literalSelector=new StandardLiteralSelector();

        while (pool.isEmpty()){
            // Global selection
            ExtendedQueryWithSubstitution chosenQuery=pool.selectQuery();

            IQuery newQuery = chosenQuery.getQuery();
            Map<IVariable, ITerm> newVariableMap = chosenQuery.getSubstitution();

            // Empty query i.e. Success
            if (newQuery.getLiterals().isEmpty()) {
                logger.debug("VarMap: "+chosenQuery.getSubstitution());
                explanations.add(createExplanation(chosenQuery, costAccumulator));
                continue;
            }

            if(chosenQuery.getTreeDepth()>=_MAX_NESTING_LEVEL)
                continue;

            // Local literal selection
            ILiteral selectedLiteral = literalSelector.select(newQuery.getLiterals());


            try {
                // Getting sub-queries
                pool.addAll(getSubQueryList(chosenQuery, selectedLiteral, costAccumulator));
            } catch (EvaluationException e) {
                e.printStackTrace();
            }


        }


        return explanations;
    }

    @Override
    /**
     * Override the original method to loop over all parents to collect the path
     */
    public Explanation createExplanation(ExtendedQueryWithSubstitution query, CostAccumulator costAccumulator) {
        Explanation solution = new Explanation();

       for(ExtendedQueryWithSubstitution currentQuery=query; currentQuery!=null;currentQuery=currentQuery.getParent()){
            solution.add(query.getEvidenceNode());
        }

        solution.setCost(costAccumulator.clone());
        return solution;


    }
}
