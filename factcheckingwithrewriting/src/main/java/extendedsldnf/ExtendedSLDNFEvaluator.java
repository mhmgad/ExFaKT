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

import extendedsldnf.datastructure.ExtendedQueryWithSubstitution;
import extendedsldnf.datastructure.Solution;
import extendedsldnf.datastructure.Solutions;
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

import java.util.*;

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
public class ExtendedSLDNFEvaluator implements ITopDownEvaluator {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final int _MAX_NESTING_LEVEL = 45;

	private IQuery mInitialQuery;
	private IFacts mFacts;
	private List<IRule> mRules;

	private static final SimpleRelationFactory srf = new SimpleRelationFactory();
	static final RuleManipulator rm = new RuleManipulator();


	/**
	 * Constructor
	 *
	 * @param facts one or many facts
	 * @param rules list of rules
	 */
	public ExtendedSLDNFEvaluator(IFacts facts, List<IRule> rules) {
		mFacts = facts;
		mRules = new LinkedList<IRule>();
		ReOrderLiteralsOptimiser rolo = new ReOrderLiteralsOptimiser();
		for (IRule rule : rules) {
			mRules.add(rolo.optimise(rule));
		}
		SimpleReOrdering sro = new SimpleReOrdering();
		mRules = sro.reOrder(mRules);
	}

	/**
	 * Evaluate given query
	 */
	public IRelation evaluate(IQuery query) throws EvaluationException {
		// Process the query
		mInitialQuery = query;
		ExtendedQueryWithSubstitution extendedQueryWithSubstitution = new ExtendedQueryWithSubstitution(query, new HashMap<IVariable, ITerm>(), ExtendedQueryWithSubstitution.Source.ORG,new HashMap<IVariable,ExtendedQueryWithSubstitution.BindingSource>());

		List<Solution> solutions = findAndSubstitute(extendedQueryWithSubstitution);

		Solutions returnedSoltion=new Solutions(solutions);

		logger.debug("------------");
		//logger.debug("Relation " + relation);
		logger.debug("Original Query: " + query);
		logger.debug("Solutions: "+ solutions);

		return returnedSoltion;
	}

	/**
	 * Return variables of the initial query
	 */
	public List<IVariable> getOutputVariables() {
		return mInitialQuery.getVariables();
	}

	private List<Solution> findAndSubstitute(ExtendedQueryWithSubstitution query) throws EvaluationException {
		//Gad: successful path queries and variables
		List<Solution> solutions = findAndSubstitute(query, 0, false);//, pathAccum,variablesMapsAccum,variableListAccum);
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
	private List<Solution> findAndSubstitute(final ExtendedQueryWithSubstitution query, int recursionDepth, boolean inNegationAsFailureFlip/*,  List<List<ExtendedQueryWithSubstitution>> pathAccum,Multimap variablesMapsAccum,List<List<List<VariablesBindings>>> variableListAccum*/) throws EvaluationException, MaximumRecursionDepthReachedException {

		// To stop infinite loop, remove this later
		// when tabling is implemented
		if (recursionDepth >= _MAX_NESTING_LEVEL)
			throw new MaximumRecursionDepthReachedException("You may ran into an infinite loop. SLDNF evaluation does not support tabling.");

		String debugPrefix = getDebugPrefix(recursionDepth, inNegationAsFailureFlip);

		logger.debug(debugPrefix + query);
		//logger.debug(debugPrefix);

		// Selection Rule
		ILiteralSelector standardSelector = new StandardLiteralSelector();
		final ILiteral selectedLiteral = standardSelector.select(query.getQuery().getLiterals());

		if (selectedLiteral == null)
			throw new EvaluationException("The selected literal must not be null.");

		logger.debug(debugPrefix + "Selected: " + selectedLiteral);

		// The results are stored in this relation
//		IRelation relationReturned;
		//Solutions solutionsReturned;

		//TODO Gad: disabled Negation
		// Process selected literal
		// Every possibility (every child/subtree of this node) is stored in a list, which is iterated later.
		//if (selectedLiteral.isPositive()) { // Positive Query Literal - search for a success node

		// Get all possible sub-queries (branches)
		List<ExtendedQueryWithSubstitution> subQueryList = new LinkedList<ExtendedQueryWithSubstitution>(); // List of new queries (incl. substitutions) to process, generated by facts and rules
		subQueryList.addAll(getSubQueryList(query, selectedLiteral));



		logger.debug(debugPrefix + "subQueryList: " + subQueryList);

		int i = 0;

		List<Solution> solutions = new LinkedList<Solution>();

		for (ExtendedQueryWithSubstitution newQws : subQueryList) { // process new queries
			IQuery newQuery = newQws.getQuery();
			Map<IVariable, ITerm> newVariableMap = newQws.getSubstitution();

			if (logger.isDebugEnabled()) {
				debugPrefix = getDebugPrefix(recursionDepth, inNegationAsFailureFlip);
				i++;
				logger.debug(debugPrefix + "QWS-" + i + ": " + newQws);
			}

			// Success node (empty clause)
			if (newQuery.getLiterals().isEmpty()) {
//				ITuple tuple = TopDownHelper.resolveTuple(query.getQuery(), variableMap);
//				relationReturned.add(tuple);
				//solutionsReturned.add(getMappedVariableList(query.getQuery().getVariables(),newVariableMap,query.source));
				Solution solution = new Solution();
				solution.add(selectedLiteral, query.getSource());
				solution.setSubstitutions(query.getSubstitution());
				solution.setSubstitutionsSources(query.getSubstitutionSources());

				solutions.add(solution);
				continue;
			}

			// Evaluate the new query (walk the subtree)
			//IRelation relationFromSubtree =
			List<Solution> solutionFromSubtree = findAndSubstitute(newQws, ++recursionDepth, inNegationAsFailureFlip/*,pathAccum,variablesMapsAccum,variableListAccum*/);

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

			// Gad: Selected literal is added if:
			// 1)grounded atoms
			// 2)not the original Query
			// 3)was not substituted by rules.

			// If rules was not used to generate this sub query
			if(newQws.getSource()!= ExtendedQueryWithSubstitution.Source.RULES){
				// not the original query
				if(query.getSource()!= ExtendedQueryWithSubstitution.Source.ORG){
					if(selectedLiteral.getAtom().isGround())
						solutionFromSubtree.forEach(sl -> sl.add(selectedLiteral, query.getSource()));

				}
			}

			solutions.addAll(solutionFromSubtree);


			//Gad: if we reached here, that means that the pass returened non-empty relation and this contributes to the success
//

			//logger.debug(debugPrefix + "fullSubgoalRelation: " + fullSubgoalRelation);
			//relationReturned.addAll(fullSubgoalRelation);

			//logger.debug(debugPrefix + "Return: " + relationReturned);
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
	private List<ExtendedQueryWithSubstitution> getSubQueryList(ExtendedQueryWithSubstitution query, ILiteral selectedLiteral) throws EvaluationException {
		List<ExtendedQueryWithSubstitution> subQueryList = new LinkedList<ExtendedQueryWithSubstitution>();

		IAtom queryLiteralAtom = selectedLiteral.getAtom();

		if (queryLiteralAtom  instanceof IBuiltinAtom) { // BuiltIn
			subQueryList.addAll( processBuiltin(query, selectedLiteral, queryLiteralAtom) );
		} else { // Not BuiltIn	
			//Gad: check if there are no subqueries from the DB go to text
			List<ExtendedQueryWithSubstitution> subQueries = processQueryAgainstFacts(query, selectedLiteral);
			subQueryList.addAll(subQueries );
			//TODO Gad: add the extra source here ()	
			if(subQueries.isEmpty())
			{
				subQueries = processQueryAgainstText(query, selectedLiteral);
				subQueryList.addAll(subQueries );
			}
			//Gad: If still no sub queries neither from DB nor text use rules to answer. (Previously it was always called)
			if(subQueries.isEmpty()){
				subQueries = processQueryAgainstRules(query, selectedLiteral);
				subQueryList.addAll( subQueries );
			}



		}
		return subQueryList;
	}

	private List<ExtendedQueryWithSubstitution> processQueryAgainstText(ExtendedQueryWithSubstitution query, ILiteral selectedLiteral) {


		//TODO Gad: Here we should find possible bindings for ?X in p(a,?X) either:
		// 1) search for partial grounded fact p(a,?X) in th text and from sentence get top k variables
		// 2) generate all possible entities from KG and generate lits p(a,b), p(a,c) ... and search for it

		//TODO: Use the bindings to generate sub-quries.


		return new ArrayList<ExtendedQueryWithSubstitution>();
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
	private List<ExtendedQueryWithSubstitution> processQueryAgainstRules(ExtendedQueryWithSubstitution query, ILiteral selectedLiteral) throws EvaluationException {

		List<ExtendedQueryWithSubstitution> newQueryList = new LinkedList<ExtendedQueryWithSubstitution>();

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

				ExtendedQueryWithSubstitution qws = new ExtendedQueryWithSubstitution(newQuery, variableMapUnify, ExtendedQueryWithSubstitution.Source.RULES,query.getSubstitutionSources());
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
	private List<ExtendedQueryWithSubstitution> processQueryAgainstFacts(ExtendedQueryWithSubstitution query, ILiteral queryLiteral) {

		List<ExtendedQueryWithSubstitution> newQueryList = new LinkedList<ExtendedQueryWithSubstitution>();
		List<Map<IVariable, ITerm>> variableMapList = new LinkedList<Map<IVariable,ITerm>>();
		if ( getMatchingFacts( queryLiteral, variableMapList  ) ) {
			for (Map<IVariable, ITerm>variableMap : variableMapList) {
				// For every fact

				// Substitute the whole query
				IQuery substitutedQuery = TopDownHelper.substituteVariablesInToQuery(query.getQuery(), variableMap);

				// Remove the fact, ...
				LinkedList<ILiteral> literalsWithoutMatch = new LinkedList<ILiteral>( substitutedQuery.getLiterals() );
				literalsWithoutMatch.remove( queryLiteral );

				// Add the new query to the query list
				IQuery newQuery = Factory.BASIC.createQuery( literalsWithoutMatch );



				variableMap.putAll(query.getSubstitution());

				//Gad
				Map<IVariable,ExtendedQueryWithSubstitution.BindingSource> substitutionSourcesMap=new HashMap<>();
				substitutionSourcesMap.putAll(query.getSubstitutionSources());
				variableMap.keySet().forEach(k->substitutionSourcesMap.put(k, ExtendedQueryWithSubstitution.BindingSource.FACT));

				ExtendedQueryWithSubstitution qws = new ExtendedQueryWithSubstitution(newQuery,variableMap, ExtendedQueryWithSubstitution.Source.FACT,substitutedQuery,substitutionSourcesMap);
				newQueryList.add( qws );
			}
		}

		return newQueryList;
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

	private List<ExtendedQueryWithSubstitution> processBuiltin(ExtendedQueryWithSubstitution queryWithSub, ILiteral selectedQueryLiteral, IAtom queryLiteralAtom)
			throws EvaluationException {
		IQuery query=queryWithSub.getQuery();
		IBuiltinAtom builtinAtom = (IBuiltinAtom)queryLiteralAtom;
		ITuple builtinTuple = builtinAtom.getTuple();
		List<ExtendedQueryWithSubstitution> newQueryList = new LinkedList<ExtendedQueryWithSubstitution>();

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

		if (builtinEvaluation != null) {

			if (builtinTuple.getVariables().isEmpty()) {
				// Builtin tuple contained no variables, the result is
				// true or false, e.g. ADD(1, 2, 3) = true
				ExtendedQueryWithSubstitution qws = new ExtendedQueryWithSubstitution(newQuery, new HashMap<IVariable, ITerm>(), ExtendedQueryWithSubstitution.Source.BUILT_IN,queryWithSub.getSubstitutionSources());
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

					// add the new query to the query list
					newQuery = TopDownHelper.substituteVariablesInToQuery(newQuery, varMap);
					ExtendedQueryWithSubstitution qws = new ExtendedQueryWithSubstitution( newQuery, varMap,ExtendedQueryWithSubstitution.Source.BUILT_IN ,queryWithSub.getSubstitutionSources());
					newQueryList.add( qws );


					variableIndex++;
				}
			}
		} else if (unifyable) {
			// Builtin evaluation failed, unification succeeded
			// Take unify result as mapping
			Map<IVariable, ITerm> varMap = new HashMap<IVariable, ITerm>();

			varMap.putAll(varMapCTarg);

			// add the new query to the query list
			newQuery = TopDownHelper.substituteVariablesInToQuery(newQuery, varMap);
			ExtendedQueryWithSubstitution qws = new ExtendedQueryWithSubstitution( newQuery, varMap,ExtendedQueryWithSubstitution.Source.BUILT_IN ,queryWithSub.getSubstitutionSources());
			newQueryList.add( qws );
		}

		return newQueryList;
	}

	/**
	 * Tries to find a fact that matches the given query. 
	 * The variableMap will be populated if a matching fact was found.
	 * @param queryLiteral the given query
	 *
	 * @return true if a matching fact is found, false otherwise
	 */
	private boolean getMatchingFacts(ILiteral queryLiteral, List<Map<IVariable, ITerm>> variableMapList) {

		// Check all the facts
		for ( IPredicate factPredicate : mFacts.getPredicates() ) {
			// Check if the predicate and the arity matches
			if ( TopDownHelper.match(queryLiteral, factPredicate) ) {
				// We've found a match (predicates and arity match) 
				// Is the QueryTuple unifiable with one of the FactTuples?

				IRelation factRelation = mFacts.get(factPredicate);

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
		}
		if (variableMapList.isEmpty())
			return false; // No fact found

		return true;
	}


	/**
	 * Creates a debug prefix for nice output
	 *
	 * @param recursionDepth depth of recursion (0 = root)
	 * @param inNegationAsFailureFlip <code>true</code> is this a NAF tree, <code>false</code> otherwise
	 *
	 * @return debug prefix string
	 */
	private String getDebugPrefix(int recursionDepth, boolean inNegationAsFailureFlip) {
		// Debug prefix for proper output
		String debugPrefix = "";

		for (int i = 0; i < recursionDepth; i++)
			debugPrefix += "  ";

		if (inNegationAsFailureFlip)
			debugPrefix += "{NAF} ";

		return debugPrefix;
	}

}
