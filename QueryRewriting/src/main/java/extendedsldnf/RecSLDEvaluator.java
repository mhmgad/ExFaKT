/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package extendedsldnf;

import config.Configuration;
import extendedsldnf.datastructure.*;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.*;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.builtins.EqualBuiltin;
import org.deri.iris.builtins.ExactEqualBuiltin;
import org.deri.iris.evaluation.topdown.*;
import org.deri.iris.factory.Factory;
import org.deri.iris.facts.IFacts;
import org.deri.iris.rules.RuleManipulator;
import org.deri.iris.rules.optimisation.ReOrderLiteralsOptimiser;
import org.deri.iris.rules.ordering.SimpleReOrdering;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;
import org.deri.iris.utils.TermMatchingAndSubstitution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import text.FactSpottingConnector;
import text.FactSpottingResult;
import text.ITextConnector;
import utils.Enums;
import utils.SyncCounter;

import java.util.*;

import static utils.Enums.ActionType.*;

//import text.ITextResult;

/**
 * Implementation of the SLDNF evaluator. Please keep in mind that
 * SLDNF evaluation is not capable of any tabling and can get trapped
 * in an infinite loop.
 *
 * For details see 'Deduktive Datenbanken' by Cremers, Griefahn
 * and Hinze (ISBN 978-3528047009).
 *
 * @author gigi
 *
 */
public class RecSLDEvaluator implements ITopDownEvaluator, IExplanationGenerator {

    private  int maxExplanations;
    private boolean suspectsFromKG;
    private  ITextConnector textConnector;
    private  Configuration.PartialBindingType partialBindingType;
    private Logger logger = LoggerFactory.getLogger(getClass());

    protected static final int _MAX_NESTING_LEVEL = 5;


    private IExtendedFacts mFacts;
    private List<IRule> mRules;




    private static final SimpleRelationFactory srf = new SimpleRelationFactory();
    static final RuleManipulator rm = new RuleManipulator();
    private int maxRuleDepth;


    /**
     * Constructor
     *
     * @param facts one or many facts
     * @param rules list of rules
     */
    public RecSLDEvaluator(IExtendedFacts facts, List<IRule> rules) {
        this(facts,rules,null, Configuration.PartialBindingType.NONE,false,Integer.MAX_VALUE,10);
    }


    /**
     * Constructor
     *
     * @param facts one or many facts
     * @param rules list of rules
     */
    public RecSLDEvaluator(IExtendedFacts facts, List<IRule> rules, ITextConnector textConnector, Configuration.PartialBindingType partialBindingType, boolean suspectsFromKG,int maxExplanations,int maxRuleDepth) {
        mFacts = facts;
        mRules = getOrderedRules(rules);


        // Text connector
        this.textConnector=textConnector;
        this.partialBindingType=partialBindingType;
        this.suspectsFromKG=suspectsFromKG;
        this.maxExplanations=maxExplanations;

        this.maxRuleDepth=maxRuleDepth;


    }

    /**
     * Takes a set of rules and sort the body of each according to binding, and sort the rules according to first ordinary literal (Not Clear!).
     * @param rules
     * @return
     */
    public List<IRule> getOrderedRules(List<IRule> rules) {
        List<IRule> tmpRules = new LinkedList<>();
        ReOrderLiteralsOptimiser rolo = new ReOrderLiteralsOptimiser();

        for (IRule rule : rules) {
            tmpRules.add(rolo.optimise(rule));
        }

        SimpleReOrdering sro = new SimpleReOrdering();
        tmpRules = sro.reOrder(tmpRules);
        return tmpRules;
    }

    /**
     * Evaluate given query
     */
    public IRelation evaluate(IQuery query) throws EvaluationException {
        // Process the query
        //mInitialQuery = query;
        int treeDepth=0;
        int ruleDepth=0;
        ExtQuerySubs extendedQueryWithSubstitution = new ExtQuerySubs(query, new HashMap<IVariable, ITerm>(),null,treeDepth,ruleDepth);
        extendedQueryWithSubstitution.setEvidenceNode(new EvidenceNode(null,ORG));

        CostAccumulator costAccumulator=new CostAccumulator();
        List<Explanation> solutions = findAndSubstitute(extendedQueryWithSubstitution,costAccumulator);


        QueryExplanations returnedSolution=new QueryExplanations(query,solutions,costAccumulator.clone());

        logger.debug("------------");
        //logger.debug("Relation " + relation);
        logger.debug("Original Query: " + query);
        logger.debug("Solutions: "+ solutions);

        return returnedSolution;
    }

    /**
     * Return variables of the initial query
     */
    public List<IVariable> getOutputVariables() {
        return null;
//        return mInitialQuery.getVariables();
    }

    public List<Explanation> findAndSubstitute(ExtQuerySubs query, CostAccumulator costAccumulator) throws EvaluationException {
        //Gad: successful path queries and variables
        List<Explanation> solutions = findAndSubstitute(query, 0, false,costAccumulator,query.getQuery(),new SyncCounter());//, pathAccum,variablesMapsAccum,variableListAccum);
//		logger.debug("Path to result: " + pathAccum.toString());
//		logger.debug("substitutions Path: " + variablesMapsAccum.toString());
//		logger.debug("Variables Path: " + variableListAccum.toString());

        return solutions;
    }

    /**
     * Find recursive the next subgoal (the next query)
     * by matching the given rules and facts with the current query.
     *
     * @param query                   The current query that should match a rule head or a fact
     * @param recursionDepth          Current recursion depth
     * @param inNegationAsFailureFlip <code>true</code> if this evaluation step uses NAF, <code>false</code> otherwise
     * @return true if the query leads to a success node, false otherwise
     * @throws EvaluationException                   If something went wrong
     * @throws MaximumRecursionDepthReachedException since SLDNF evaluation does not detect infinite loops, this exception is thrown at a nesting level of 25
     */
    public List<Explanation> findAndSubstitute(final ExtQuerySubs query, int recursionDepth, boolean inNegationAsFailureFlip, CostAccumulator costAccumulator, IQuery orgQuery, SyncCounter counter) throws EvaluationException {

        // To stop infinite loop, remove this later
        // when tabling is implemented
        if (recursionDepth >= _MAX_NESTING_LEVEL)
            //throw new MaximumRecursionDepthReachedException("You may ran into an infinite loop. SLDNF evaluation does not support tabling.");
            return new ArrayList<>();

        String debugPrefix = getDebugPrefix(recursionDepth, inNegationAsFailureFlip);

        logger.debug(debugPrefix + query);
        //logger.debug(debugPrefix);

        // Selection Rule
        ILiteralSelector standardSelector = new StandardLiteralSelector();
        final ILiteral selectedLiteral = standardSelector.select(query.getQuery().getLiterals());

        if (selectedLiteral == null)
            throw new EvaluationException("The selected literal must not be null.");

        logger.debug(debugPrefix + "Selected: " + selectedLiteral);


        // Process selected literal
        List<ExtQuerySubs> subQueryList = new LinkedList<ExtQuerySubs>(); // List of new queries (incl. substitutions) to process, generated by facts and rules

        subQueryList.addAll(getSubQueryList(query, selectedLiteral, costAccumulator));

        logger.trace(debugPrefix + "subQueryList: " + subQueryList);

        int i = 0;

        List<Explanation> solutions = new LinkedList<Explanation>();

        for (ExtQuerySubs newQws : subQueryList) { // process new queries
            IQuery newQuery = newQws.getQuery();
            Map<IVariable, ITerm> newVariableMap = newQws.getSubstitution();

            if (logger.isDebugEnabled()) {
                debugPrefix = getDebugPrefix(recursionDepth, inNegationAsFailureFlip);
                i++;
                logger.debug(debugPrefix + "QWS-" + i + ": " + newQws);
            }

            // Success node (empty clause)
            if (newQuery.getLiterals().isEmpty()) {
                logger.debug("VarMap: "+newQws.getSubstitution());
                solutions.add(createExplanation(newQws, costAccumulator,orgQuery,counter.getCount()));
                continue;
            }

            // Evaluate the new query (walk the subtree)
            //IRelation relationFromSubtree =
            List<Explanation> solutionFromSubtree = findAndSubstitute(newQws, recursionDepth+1, inNegationAsFailureFlip,costAccumulator,orgQuery,counter);

            logger.debug(debugPrefix + "Old query: " + query.getQuery().getVariables() + query);
            logger.debug(debugPrefix + "New query: " + newQuery.getVariables() + newQuery + " | " + newVariableMap);
            logger.debug(debugPrefix + "Subtree Solution: " + solutionFromSubtree);

            if (solutionFromSubtree.isEmpty()/*solutionFromSubtree.isEmpty()*/) {
                if (!inNegationAsFailureFlip) {
                    continue; // Failure node (subtree returned false) - try next branch
                } else {
                    logger.debug(debugPrefix + "NAF FAILURE NODE " + solutions);
                    break; // Failure node, and we do NAF. So it is a success node
                }
            }


            solutionFromSubtree.forEach(sl -> sl.add(newQws.getEvidenceNode()));


            solutions.addAll(solutionFromSubtree);

        }
        return solutions;
    }



    /**
     * Get possible sub-queries of this node by evaluating built-ins or
     * applying rules and facts.
     *
     * @param query current query
     * @param selectedLiteral selected literal
     *
     * @return list where the new queries are stored
     *
     * @throws EvaluationException on failure
     */
    public List<ExtQuerySubs> getSubQueryList(ExtQuerySubs query, ILiteral selectedLiteral, CostAccumulator costAccumulator) throws EvaluationException {
        List<ExtQuerySubs> subQueryList = new LinkedList<ExtQuerySubs>();

        IAtom queryLiteralAtom = selectedLiteral.getAtom();

        if (queryLiteralAtom  instanceof IBuiltinAtom) { // BuiltIn
            subQueryList.addAll( processBuiltin(query, selectedLiteral, queryLiteralAtom) );
        } else {

            // Reject self-reflexive
            Collection<ITerm> values = query.getSubstitution().values();
            int allValues=values.size();
            int uniqueValues=(new HashSet<>(values)).size();

            if(allValues>1 && uniqueValues==1){
                return subQueryList;
            }

            //If it is a fact (Grounded) and was not proved check the text
            List<ExtQuerySubs> subQueries;
            if(queryLiteralAtom.isGround()){
                subQueries = checkFact(query, selectedLiteral,  costAccumulator);
            }else{
                subQueries= bindVariables(query, selectedLiteral, costAccumulator);
            }
            subQueryList.addAll(subQueries);

            // Rules are always fired to grant reaching the whole search space
            if(mRules.size()>0 && query.getRulesDepth()<maxRuleDepth){
                subQueries = processQueryAgainstRules(query, selectedLiteral, costAccumulator);
                subQueryList.addAll( subQueries );
            }
        }
        return subQueryList;
    }

    public List<ExtQuerySubs> bindVariables(ExtQuerySubs query, ILiteral selectedLiteral, CostAccumulator costAccumulator) {
        List<ExtQuerySubs> subQueryList = new LinkedList<ExtQuerySubs>();

        // Bind from KG
        subQueryList.addAll(processQueryAgainstFacts(query, selectedLiteral, costAccumulator));

        //If it is partially grounded and it is allowed to get bindings from text
        switch (partialBindingType){
            case TEXT:
                subQueryList.addAll(processQueryAgainstText(query, selectedLiteral, costAccumulator));
                break;
            case GREEDY:
                subQueryList.addAll( bindFromKGAggressively(query, selectedLiteral, costAccumulator));
                break;
        }
        return subQueryList;
    }

    public List<ExtQuerySubs> checkFact(ExtQuerySubs query, ILiteral selectedLiteral, CostAccumulator costAccumulator) {

        List<ExtQuerySubs> subQueries=new LinkedList<>();
//        logger.debug("Fact is suspect: "+suspectsFromKG +" Query sourceActionType: "+query.getEvidenceNode().getSourceActionType() );
        if((query.getEvidenceNode().getSourceActionType()==ORG)&&suspectsFromKG){
            // Do not check the KG if the fact is suspected from KG
            logger.debug("Skip KG check! Fact is suspected from KG.");
        }else {
            subQueries = processQueryAgainstFacts(query, selectedLiteral, costAccumulator);
        }


        if(subQueries.isEmpty()) {
            subQueries = processQueryAgainstText(query, selectedLiteral, costAccumulator);
        }
        return subQueries;
    }

    public List<ExtQuerySubs> processQueryAgainstText(ExtQuerySubs query, ILiteral queryLiteral, CostAccumulator costAccumulator) {


        //TODO Gad: Here we should find possible bindings for ?X in p(a,?X) either:
        // 1) search for partial grounded fact p(a,?X) in th text and from sentence get top k variables
        // 2) generate all possible entities from KG and generate list p(a,b), p(a,c) ... and search for it

        //TODO: Use the bindings to generate sub-quries.

        List<ExtQuerySubs> newQueryList = new LinkedList<ExtQuerySubs>();


        // Check text
        FactSpottingResult result = null;
        try {
            result = textConnector.queryText(queryLiteral);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // support is found
        if(!result.found()){
            return new LinkedList<>();
        }

        List<Map<IVariable, ITerm>> variableMapList = result.getVaribalesMappings();

//        EvidenceNode evidenceNode=(queryLiteral.getAtom().isGround())? new EvidenceNode(queryLiteral, EvidenceNode.Type.TEXT_VALID):new EvidenceNode(queryLiteral, EvidenceNode.Type.VAR_BIND);
//			evidenceNode.setParentEvidence(query.getEvidenceNode());
        EvidenceNode evidenceNode=(queryLiteral.getAtom().isGround())? new EvidenceNode(queryLiteral, Enums.ActionType.TEXT_VALID):new EvidenceNode(queryLiteral, Enums.ActionType.TEXT_BIND);
//        if(evidenceNode.getSourceActionType()== EvidenceNode.Type.VAR_BIND)
//            evidenceNode.setBindingSource(Enums.BindingSource.TEXT);
//        else
        evidenceNode.setTextResults(result);

        //Count cost of validation and binding.
        if(queryLiteral.getAtom().isGround())
            costAccumulator.addCost(Enums.ActionType.TEXT_VALID);
        else
            costAccumulator.addCost(Enums.ActionType.TEXT_BIND);




        return getExtendedQueryWithSubstitutions(query,queryLiteral,variableMapList, evidenceNode,true);
    }

    /**
     * Generate aggressive binding for a predicate using all possible entities
     * @param query
     * @param queryLiteral
     * @return
     */
    public List<ExtQuerySubs> bindFromKGAggressively(ExtQuerySubs query, ILiteral queryLiteral, CostAccumulator costAccumulator) {

        IRelation factRelation=mFacts.getHypotheticalBindings(queryLiteral);
        logger.debug(queryLiteral+" Hypothetical Bindings "+factRelation.size());

        List<Map<IVariable, ITerm>> variableMapList=new LinkedList<>();
        FactsUtils.fillVariableMaps(queryLiteral,factRelation,variableMapList);

//        EvidenceNode evidenceNode=new EvidenceNode(queryLiteral, EvidenceNode.Type.VAR_BIND);
        EvidenceNode evidenceNode=new EvidenceNode(queryLiteral, Enums.ActionType.GREEDY_BIND);
//        evidenceNode.setBindingSource(Enums.BindingSource.GREEDY);

        // Count cost only once
        costAccumulator.addCost(GREEDY_BIND);

        if(factRelation.size()>0){
            return getExtendedQueryWithSubstitutions(query,queryLiteral,variableMapList,evidenceNode/*, Enums.BindingSource.GREEDY, ExtendedQueryWithSubstitution.ExpansionMethod.ENTITIES_COMB*/,false);
        }

        return  new LinkedList<>();
    }


    /**
     * Scans the knowledge base for rules that match the selected query literal.
     * If a unifiable match was found the substitution and the new query will be
     * saved and added to a list of new queries, which is returned.
     *
     * @param query the whole query
     * @param selectedLiteral the selected literal
     * @return list of queries with substitutions
     *
     * @throws EvaluationException on failure
     */
    public List<ExtQuerySubs> processQueryAgainstRules(ExtQuerySubs query, ILiteral selectedLiteral, CostAccumulator costAccumulator) throws EvaluationException {

        List<ExtQuerySubs> newQueryList = new LinkedList<ExtQuerySubs>();

        IQuery orgQuery=query.getQuery();
        //SimpleSelector


        //Add the cost only count rules access once
        costAccumulator.addCost(RULE_EXPAND);

        for (IRule rule : mRules) {
            ILiteral ruleHead = rule.getHead().get(0);

            // Potential match?
            if (TopDownHelper.match(ruleHead, selectedLiteral)) {
                Map<IVariable, ITerm> variableMapUnify = new HashMap<IVariable, ITerm>();
                ITuple queryTuple = selectedLiteral.getAtom().getTuple();

                // Occur Check
                // Replace all variables of the rule with unused ones (variables that are not in the query)
                Map<IVariable, ITerm> variableMapForOccurCheck = TopDownHelper.getVariableMapForVariableRenaming(rule, query.getQuery());
                IRule ruleAfterOccurCheck = TopDownHelper.replaceVariablesInRule(rule, variableMapForOccurCheck);

                ITuple ruleHeadAfterOCTuple = ruleAfterOccurCheck.getHead().get(0).getAtom().getTuple(); // ruleHead changed

                // Unifiable?
                boolean unifyable = TermMatchingAndSubstitution.unify(queryTuple, ruleHeadAfterOCTuple, variableMapUnify);

                IQuery newQuery = query.getQuery();
                if (unifyable) {
                    // Replace the rule head with the rule body
                    // This replacement has to be save, because we did an occur check before
                    newQuery = TopDownHelper.substituteRuleHeadWithBody( query.getQuery(), selectedLiteral, ruleAfterOccurCheck );
                }

                // Substitute the whole query
                newQuery = TopDownHelper.substituteVariablesInToQuery(newQuery, variableMapUnify);

                EvidenceNode evidenceNode= new EvidenceNode(selectedLiteral, Enums.ActionType.RULE_EXPAND);
                evidenceNode.setRule(rule);


                ExtQuerySubs qws = new ExtQuerySubs(newQuery, variableMapUnify,query,query.getTreeDepth()+1,query.getRulesDepth()+1);
                qws.setEvidenceNode(evidenceNode);
                newQueryList.add(qws);
            }
        }
        return newQueryList;
    }

    /**
     * Scans the knowledge base for rules that match the selected query literal.
     * If a unifiable match was found the substitution and the new query will be
     * saved and added to a list of new queries, which is returned.
     *
     * @param query the whole query
     * @param queryLiteral the selected literal
     * @return list of queries with substitutions
     */
    public List<ExtQuerySubs> processQueryAgainstFacts(ExtQuerySubs query, ILiteral queryLiteral, CostAccumulator costAccumulator) {


        List<Map<IVariable, ITerm>> variableMapList = new LinkedList<Map<IVariable,ITerm>>();
        if ( FactsUtils.getMatchingFacts( queryLiteral, variableMapList,mFacts  ) ) {
            boolean removePredicate=true;
//            EvidenceNode evidenceNode=(queryLiteral.getAtom().isGround())? new EvidenceNode(queryLiteral, EvidenceNode.Type.KG_VALID):new EvidenceNode(queryLiteral, EvidenceNode.Type.VAR_BIND);
            EvidenceNode evidenceNode=(queryLiteral.getAtom().isGround())? new EvidenceNode(queryLiteral, Enums.ActionType.KG_VALID):new EvidenceNode(queryLiteral, Enums.ActionType.KG_BIND);
//			evidenceNode.setParentEvidence(query.getEvidenceNode());
//            if(evidenceNode.getSourceActionType()== EvidenceNode.Type.VAR_BIND) evidenceNode.setBindingSource(Enums.BindingSource.FACT);

            //Count cost of validation and binding.
            if(queryLiteral.getAtom().isGround())
                costAccumulator.addCost(Enums.ActionType.KG_VALID);
            else
                costAccumulator.addCost(Enums.ActionType.KG_BIND);

            return getExtendedQueryWithSubstitutions(query, queryLiteral, variableMapList, evidenceNode,removePredicate);


        }

        return new LinkedList<ExtQuerySubs>();
    }

    public List<ExtQuerySubs> getExtendedQueryWithSubstitutions(ExtQuerySubs query, ILiteral queryLiteral, List<Map<IVariable, ITerm>> variableMapList, EvidenceNode evidenceNode, boolean removeQueryLiteral) {
        List<ExtQuerySubs> newQueryList = new LinkedList<ExtQuerySubs>();
        for (Map<IVariable, ITerm>variableMap : variableMapList) {

            // For every fact

            // Substitute the whole query
            IQuery substitutedQuery = TopDownHelper.substituteVariablesInToQuery(query.getQuery(), variableMap);

            IQuery newQuery = getSubQuery(queryLiteral, substitutedQuery, removeQueryLiteral);


            Map<IVariable, ITerm> combinedVariableMap=new HashMap<>();
            combinedVariableMap.putAll(query.getSubstitution());
            combinedVariableMap.putAll(variableMap);


            ExtQuerySubs qws = new ExtQuerySubs(newQuery,combinedVariableMap,query,query.getTreeDepth()+1,query.getRulesDepth());
            // extended evidence and the mapping
            EvidenceNode extendedEvidenceNode=evidenceNode.clone();
            extendedEvidenceNode.setVariableBindingMap(variableMap);

            qws.setEvidenceNode(extendedEvidenceNode);
            newQueryList.add( qws );

        }
        return newQueryList;
    }

    public IQuery getSubQuery(ILiteral queryLiteral, IQuery substitutedQuery, boolean removeQueryLiteral) {
        // Remove the fact, ...
        LinkedList<ILiteral> literalsWithoutMatch = new LinkedList<ILiteral>( substitutedQuery.getLiterals() );
        if(removeQueryLiteral)
            literalsWithoutMatch.remove( queryLiteral );

        // Add the new query to the query list
        return Factory.BASIC.createQuery( literalsWithoutMatch );
    }

    /**
     * Process a builtin atom.
     *
     * @param queryWithSub the whole query
     * @param selectedQueryLiteral the selected literal
     * @param queryLiteralAtom
     * @return List of new queries and the associated substitutions
     *
     * @throws EvaluationException on failure
     */

    public List<ExtQuerySubs> processBuiltin(ExtQuerySubs queryWithSub, ILiteral selectedQueryLiteral, IAtom queryLiteralAtom)
            throws EvaluationException {
        IQuery query=queryWithSub.getQuery();
        IBuiltinAtom builtinAtom = (IBuiltinAtom)queryLiteralAtom;
        ITuple builtinTuple = builtinAtom.getTuple();
        List<ExtQuerySubs> newQueryList = new LinkedList<ExtQuerySubs>();

        ITuple builtinEvaluation = null;
        boolean unifyable = false;
        boolean evaluationNeeded = false;

        Map<IVariable, ITerm> varMapCTarg = new HashMap<IVariable, ITerm>();

        if ( builtinAtom instanceof EqualBuiltin || builtinAtom instanceof ExactEqualBuiltin ) {
            // UNIFICATION

            assert builtinTuple.size() == 2;
            unifyable = TermMatchingAndSubstitution.unify(builtinTuple.get(0), builtinTuple.get(1), varMapCTarg );

        } else {
            // EVALUATION - every builtin except EqualBuiltin
            evaluationNeeded = true;
        }

        try {
            builtinEvaluation = builtinAtom.evaluate(builtinTuple);
        } catch (IllegalArgumentException iae) {
            // The builtin can't be evaluated yet, simply continue
        }

        List<ILiteral> literalsWithoutBuiltin = new LinkedList<ILiteral>(query.getLiterals());
        literalsWithoutBuiltin.remove(selectedQueryLiteral);
        IQuery newQuery = Factory.BASIC.createQuery( literalsWithoutBuiltin );

        EvidenceNode evidenceNode=new EvidenceNode(selectedQueryLiteral,BUILT_IN);


        if (builtinEvaluation != null) {

            if (builtinTuple.getVariables().isEmpty()) {
                // Builtin tuple contained no variables, the result is
                // true or false, e.g. ADD(1, 2, 3) = true
                ExtQuerySubs qws = new ExtQuerySubs(newQuery, new HashMap<IVariable, ITerm>(),queryWithSub,queryWithSub.getTreeDepth()+1,queryWithSub.getRulesDepth());
                qws.setEvidenceNode(evidenceNode);
                newQueryList.add( qws );

            } else {
                // Builtin tuple contained variables, so there is a
                // computed answer, e.g. ADD(1, 2, ?X) => ?X = 3
                Map<IVariable, ITerm> varMap = new HashMap<IVariable, ITerm>();
                Set<IVariable> variablesPreEvaluation = builtinTuple.getVariables();

                if (variablesPreEvaluation.size() != builtinEvaluation.size())
                    throw new EvaluationException("Builtin Evaluation failed. Expected " + variablesPreEvaluation.size() + " results, got " + builtinEvaluation.size());


                // Add every computed variable to the mapping
                int variableIndex = 0;
                for ( IVariable var : variablesPreEvaluation ) {

                    if ( unifyable ) { // unification
                        varMap.putAll(varMapCTarg);
                    } else if ( evaluationNeeded ) { // evaluation
                        ITerm termPostEvaluation = builtinEvaluation.get( variableIndex ); // get evaluated term
                        varMap.put(var, termPostEvaluation);
                    } else { // no new query / branch
                        variableIndex++;
                        continue;
                    }

                    // addAll the new query to the query list
                    newQuery = TopDownHelper.substituteVariablesInToQuery(newQuery, varMap);
                    ExtQuerySubs qws = new ExtQuerySubs( newQuery, varMap,queryWithSub,queryWithSub.getTreeDepth()+1,queryWithSub.getRulesDepth());
                    qws.setEvidenceNode(evidenceNode);
                    newQueryList.add( qws );


                    variableIndex++;
                }
            }
        } else if (unifyable) {
            // Builtin evaluation failed, unification succeeded
            // Take unify result as mapping
            Map<IVariable, ITerm> varMap = new HashMap<IVariable, ITerm>();

            varMap.putAll(varMapCTarg);

            // addAll the new query to the query list
            newQuery = TopDownHelper.substituteVariablesInToQuery(newQuery, varMap);
            ExtQuerySubs qws = new ExtQuerySubs( newQuery, varMap,queryWithSub,queryWithSub.getTreeDepth()+1,queryWithSub.getRulesDepth());
            qws.setEvidenceNode(evidenceNode);
            newQueryList.add( qws );
        }

        return newQueryList;
    }

//    /**
//     * Tries to find a fact that matches the given query.
//     * The variableMap will be populated if a matching fact was found.
//     * @param queryLiteral the given query
//     *
//     * @return true if a matching fact is found, false otherwise
//     */
//    public static boolean getMatchingFacts(ILiteral queryLiteral, List<Map<IVariable, ITerm>> variableMapList, IFacts factsSource) {
//
//        // Check all the facts
//        for ( IPredicate factPredicate : factsSource.getPredicates() ) {
//            // Check if the predicate and the arity matches
//            if ( TopDownHelper.match(queryLiteral, factPredicate) ) {
//                // We've found a match (predicates and arity match)
//                // Is the QueryTuple unifiable with one of the FactTuples?
//
//                IRelation factRelation = factsSource.get(factPredicate);
//                fillVariableMaps(queryLiteral, factRelation, variableMapList);
//
//
//            }
//        }
//        if (variableMapList.isEmpty())
//            return false; // No fact found
//
//        return true;
//    }
//
//    public static void fillVariableMaps(ILiteral queryLiteral, IRelation factRelation, List<Map<IVariable, ITerm>> variableMapList) {
//        // Substitute variables into the query
//        for ( int i = 0; i < factRelation.size(); i++ ) {
//            ITuple queryTuple = queryLiteral.getAtom().getTuple();
//            boolean tupleUnifyable = false;
//            ITuple factTuple = factRelation.get(i);
//            Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
//            tupleUnifyable = TermMatchingAndSubstitution.unify(queryTuple, factTuple, variableMap);
//            if (tupleUnifyable) {
//                queryTuple = TermMatchingAndSubstitution.substituteVariablesInToTuple(queryTuple, variableMap);
//                variableMapList.add(variableMap);
//            }
//        }
//    }


    /**
     * Creates a debug prefix for nice output
     *
     * @param recursionDepth depth of recursion (0 = root)
     * @param inNegationAsFailureFlip <code>true</code> is this a NAF tree, <code>false</code> otherwise
     *
     * @return debug prefix string
     */
    public String getDebugPrefix(int recursionDepth, boolean inNegationAsFailureFlip) {
        // Debug prefix for proper output
        String debugPrefix = "";

        for (int i = 0; i < recursionDepth; i++)
            debugPrefix += "  ";

        if (inNegationAsFailureFlip)
            debugPrefix += "{NAF} ";

        return debugPrefix;
    }

    @Override
    public IQueryExplanations getExplanation(IQuery query) throws EvaluationException {
        return (IQueryExplanations) evaluate(query);
    }



    public Explanation createExplanation(ExtQuerySubs currentQuery, CostAccumulator costAccumulator, IQuery orgQuery,int generationOrder) {
        Explanation solution = new Explanation(orgQuery, getClass().getName(),generationOrder);
        solution.add(currentQuery.getEvidenceNode());
        solution.setCost(costAccumulator.clone());
        return solution;
    }

    public int getMaxExplanations() {
        return maxExplanations;
    }

    public void setMaxExplanations(int maxExplanations) {
        this.maxExplanations = maxExplanations;
    }

    public int getMaxRuleDepth() {
        return maxRuleDepth;
    }

    public void setMaxRuleDepth(int maxRuleDepth) {
        this.maxRuleDepth = maxRuleDepth;
    }

    public IFacts getFacts() {
        return mFacts;
    }
}
