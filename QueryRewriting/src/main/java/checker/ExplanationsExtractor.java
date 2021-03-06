package checker;

import config.Configuration;
import extendedsldnf.EvaluatorFactory;
import extendedsldnf.RecSLDEvaluator;
import extendedsldnf.datastructure.IExtendedFacts;
import extendedsldnf.datastructure.IQueryExplanations;
import extendedsldnf.datastructure.InputQuery;
import extendedsldnf.datastructure.TextualSource;
import org.deri.iris.ConfigurationThreadLocalStorage;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import text.FactSpottingConnector;
import text.ITextConnector;
import utils.DataUtils;

import javax.inject.Singleton;
import java.util.*;

/**
 * Created by gadelrab on 3/22/17.
 */
@Singleton
public class ExplanationsExtractor implements IDeepChecker/*<InputQuery>*/ {


    private static final ExplanationsExtractor explanationsExtractor=new ExplanationsExtractor();

    private IEvaluationStrategy evaluationStrategy;
    private Logger logger = LoggerFactory.getLogger(getClass());

//    /**
//     * The engine used to spot facts
//     */
//    private IFactSpotter<BinaryFact> factIFactSpotter;


    /**
     * Rewriting defaultRules (iris defaultRules)
     *
     */
    private List<IRule> defaultRules;

    /**
     * Facts  (iris facts)
     */
//    private Map<IPredicate,IRelation> factsMap;
    private List<IExtendedFacts> facts;


    /**
     * FactChecking with Rewriting configuration
     */
    private Configuration config;


    /**
     *KB (Where reasoning is performed... according to IRIS implementation)
     * Knowledge base objects hides the process of applying reasoners on the facts
     * It should be removed later
     */
    //private IKnowledgeBase knowledgeBase;

            // Evalautor






    private ExplanationsExtractor() {
        // load Config
        config= Configuration.getInstance();
        // Store the configuration object against the current thread.
        ConfigurationThreadLocalStorage.setConfiguration(config);


        // Load Rules
        defaultRules =DataUtils.loadRules(config.getRulesFiles());

        if (logger.isDebugEnabled()) {
            logger.debug("IRIS knowledge-base init");
            logger.debug("========================");

            for (IRule rule : defaultRules) {
                logger.debug(rule.toString());
            }

            logger.debug("------------------------");
        }

        //Create KB
        //knowledgeBase= KnowledgeBaseFactory.createKnowledgeBase(factsMap,defaultRules,config);


        // Load facts
        //TODO create loader for several KGS
        facts= Arrays.asList(DataUtils.loadFacts(config));
        //logger.debug(facts.toString());
        // Checks if there external data sources
//        if (config.externalDataSources.size() > 0)
//            facts = new FactsWithExternalData(facts,
//                    config.externalDataSources);



// Init evaluation Strategy .. useful if we are going to optimize the program
//        if (config.programOptmimisers.size() > 0)
//            evaluationStrategy = new OptimisedProgramStrategyAdaptor(facts,
//                    defaultRules, config);
//        else
//            evaluationStrategy = config.evaluationStrategyFactory
//                    .createEvaluator(facts, defaultRules, config);


        System.out.println("Extractor initialized!");

    }

    public static synchronized ExplanationsExtractor getInstance(){
        return explanationsExtractor;
    }

    @Override
    public IQueryExplanations check(InputQuery query) {
//        try {
//
//           // return (IExplaination) evaluationStrategy.evaluateQuery(query,null);
//            ExtendedSLDNFEvaluator evaluator ;
//            EvaluatorFactory evaluatorFactory=new EvaluatorFactory(config);
//            evaluator = evaluatorFactory.getEvaluator(facts, defaultRules);
//            IQueryExplanations relation = evaluator.getExplanation(query);
//            return  relation;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;

        return check(query, new HashSet<>());
    }

    @Override
    public IQueryExplanations check(InputQuery query,Collection<IRule> specificRules) {
        try {
            System.out.println("Query : "+query.toString());
            // return (IExplaination) evaluationStrategy.evaluateQuery(query,null);
            List<IRule> rules=new LinkedList<>(defaultRules);
            rules.addAll(specificRules);


            RecSLDEvaluator evaluator ;
            EvaluatorFactory evaluatorFactory=new EvaluatorFactory(config);
            List<IExtendedFacts> usedFactSources = getUsedFactResources(query.getKgs());//getUsedFactResources(Arrays.asList("yago", "dbpedia"));
            List<ITextConnector> usedTextualResources = getUsedTextualResources(query.getTextualSources());//getUsedTextualResources(Arrays.asList("wiki", "bing"));
//            evaluator = evaluatorFactory.getEvaluator(facts, rules);
            evaluator = evaluatorFactory.getEvaluator(usedFactSources, usedTextualResources, rules);
            IQueryExplanations relation = evaluator.getExplanation(query.getIQuery());
            return  relation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ITextConnector> getUsedTextualResources(List<TextualSource> textualSources) {
        return Arrays.asList(new FactSpottingConnector(config));
    }
//
//    @Override
//    public IQueryExplanations check(Fact fact, Collection<IRule> ruleSet) {
//
//        Parser parser = new Parser();
//        try {
//            parser.parse(fact.getIRISQueryRepresenation());
//            IQuery query = parser.getQueries().get(0);
//            return check(query,ruleSet);
//
//        } catch (ParserException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


//    @Override
//    public IQueryExplanations check(Fact fact) {
//        return check(fact,new HashSet<>());
//
//    }

    private List<IExtendedFacts> getUsedFactResources(List<String> strings) {
        return facts;
    }


//    public void removeFactsFromKG(List<IQuery> queries) {
//        for (IQuery q:queries) {
//            for (ILiteral l:q.getLiterals()) {
//                facts.
//
//            }
//
//        }
//    }
}

