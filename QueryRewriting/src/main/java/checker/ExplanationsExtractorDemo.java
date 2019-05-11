package checker;

import config.Configuration;
import de.mpii.factspotting.FactSpotterFactory;
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
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/22/17.
 */
@Singleton
public class ExplanationsExtractorDemo implements IDeepChecker/*<InputQuery>*/ {


    private static final ExplanationsExtractorDemo explanationsExtractor=new ExplanationsExtractorDemo();

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
    private HashMap<String,IExtendedFacts> factSources;


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


    /**
     * textual sources interface
     */
    private HashMap<TextualSource,ITextConnector> textualSource=new HashMap<>();




    private ExplanationsExtractorDemo() {
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
        //TODO create loader for several KGS (done but to be refactored)
        factSources.put("yago",DataUtils.loadFacts(config,config.getKgsDirectory()+ File.separator+"yago.tsv"));








// Init evaluation Strategy .. useful if we are going to optimize the program
//        if (config.programOptmimisers.size() > 0)
//            evaluationStrategy = new OptimisedProgramStrategyAdaptor(facts,
//                    defaultRules, config);
//        else
//            evaluationStrategy = config.evaluationStrategyFactory
//                    .createEvaluator(facts, defaultRules, config);


        System.out.println("Extractor initialized!");

    }

    public static synchronized ExplanationsExtractorDemo getInstance(){
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
            evaluator = evaluatorFactory.getEvaluator(usedFactSources, usedTextualResources, rules, query.getMaxExplanations(),query.getMaxRules());
            IQueryExplanations relation = evaluator.getExplanation(query.getIQuery());
            return  relation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ITextConnector> getUsedTextualResources(List<TextualSource> textualSources) {
        List<ITextConnector> connectors=new ArrayList<>();

        if(textualSources.isEmpty()){
            connectors.add(new FactSpottingConnector(FactSpotterFactory.SpottingMethod.NONE,config));
        }
        else
        {
            textualSources.forEach(ts-> connectors.add(new FactSpottingConnector(FactSpotterFactory.SpottingMethod.valueOf(ts.getSourceName().toUpperCase()),config)));
        }
        return connectors;
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

    private List<IExtendedFacts> getUsedFactResources(List<String> kgs) {
        return kgs.stream().map(s-> factSources.get(s)).collect(Collectors.toList());

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

