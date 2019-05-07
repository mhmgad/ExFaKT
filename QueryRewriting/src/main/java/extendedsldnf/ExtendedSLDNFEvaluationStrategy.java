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
import extendedsldnf.datastructure.IExtendedFacts;
import org.deri.iris.EvaluationException;
import org.deri.iris.ProgramNotStratifiedException;
import org.deri.iris.RuleUnsafeException;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of the SLDNF evaluation strategy.
 * For details see 'Deduktive Datenbanken' by Cremers, Griefahn
 * and Hinze (ISBN 978-3528047009).
 *
 * @author gigi
 *
 */
public class ExtendedSLDNFEvaluationStrategy implements IEvaluationStrategy {

	/**
	 * Constructor
	 * @param facts Given facts.
	 * @param rules Given rules.
	 * @param configuration config.Configuration
	 * @throws EvaluationException
	 */
	public ExtendedSLDNFEvaluationStrategy(IFacts facts, List<IRule> rules, Configuration configuration ) throws EvaluationException {
		mFacts = facts;
		mRules = rules;
		mConfiguration = configuration;
	}

	/**
	 * Evaluate the query
	 */
	public IRelation evaluateQuery(IQuery query, List<IVariable> outputVariables) throws ProgramNotStratifiedException, RuleUnsafeException, EvaluationException {
		if( query == null )
			throw new IllegalArgumentException( "SLDEvaluationStrategy.evaluateQuery() - query must not be null." );

		RecSLDEvaluator evaluator = new RecSLDEvaluator( (List<IExtendedFacts>) mFacts, mRules );
		IRelation relation = evaluator.evaluate(query);
		if(outputVariables==null)
			outputVariables=new LinkedList<>();
 		outputVariables.addAll( evaluator.getOutputVariables() );

		return relation;
	}

	protected final IFacts mFacts;
	protected final List<IRule> mRules;
	protected final Configuration mConfiguration;

}
