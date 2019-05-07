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
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.evaluation.IEvaluationStrategyFactory;
import org.deri.iris.facts.IFacts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Factory for Extended SLDNF evaluation strategy
 *
 *
 */
public class ExtendedSLDNFEvaluationStrategyFactory implements IEvaluationStrategyFactory
{

	//TODO  Gad:added debuging
	private Logger logger = LoggerFactory.getLogger(getClass());


	public IEvaluationStrategy createEvaluator( IFacts facts, List<IRule> rules, org.deri.iris.Configuration configuration )
	                throws EvaluationException
	{
		logger.debug("Creating SLDNF Evaluation");
		return new ExtendedSLDNFEvaluationStrategy( facts, rules, (Configuration) configuration );
	}

}
//