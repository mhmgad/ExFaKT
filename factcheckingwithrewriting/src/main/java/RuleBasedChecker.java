import config.Configuration;
import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Fact;
import de.mpii.factspotting.IFactSpotter;
import extendedsldnf.ExtendedSLDNFEvaluator;
import extendedsldnf.datastructure.IExplanation;
import mpi.tools.javatools.util.FileUtils;
import org.deri.iris.ConfigurationThreadLocalStorage;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.facts.Facts;
import org.deri.iris.facts.FactsWithExternalData;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import text.FactSpottingConnector;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gadelrab on 3/22/17.
 */
public class RuleBasedChecker implements IDeepChecker<IQuery>{


    private IEvaluationStrategy evaluationStrategy;
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The engine used to spot facts
     */
    private IFactSpotter<BinaryFact> factIFactSpotter;


    /**
     * Rewriting rules (iris rules)
     *
     */
    private List<IRule> rules;

    /**
     * Facts  (iris facts)
     */
    private Map<IPredicate,IRelation> factsMap;
    private IFacts facts;


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
    ExtendedSLDNFEvaluator evaluator ;





    public RuleBasedChecker() throws EvaluationException {
        // load Config
        config= Configuration.getInstance();
        // Store the configuration object against the current thread.
        ConfigurationThreadLocalStorage.setConfiguration(config);


        // Load Rules
        rules=loadRules(config.getRulesFiles());

        if (logger.isDebugEnabled()) {
            logger.debug("IRIS knowledge-base init");
            logger.debug("========================");

            for (IRule rule : rules) {
                logger.debug(rule.toString());
            }

            logger.debug("------------------------");
        }

        //Create KB
        //knowledgeBase= KnowledgeBaseFactory.createKnowledgeBase(factsMap,rules,config);


        // Load facts
        facts=loadFacts(config);
        // Checks if there external data sources
        if (config.externalDataSources.size() > 0)
            facts = new FactsWithExternalData(facts,
                    config.externalDataSources);



// Init evalution Strategy .. usefull if we are going to optimize the program
//        if (config.programOptmimisers.size() > 0)
//            evaluationStrategy = new OptimisedProgramStrategyAdaptor(facts,
//                    rules, config);
//        else
//            evaluationStrategy = config.evaluationStrategyFactory
//                    .createEvaluator(facts, rules, config);


        evaluator = new ExtendedSLDNFEvaluator( facts, rules,new FactSpottingConnector(config),config.getPartialBindingType() );


    }


    /**
     * Creates IFacts source according to the type indicated in the configuration file
     * @param conf
     * @return
     */
    private IFacts loadFacts(Configuration conf) {
        return new Facts(loadFactsFromFiles(conf.getFactsFiles()),conf.relationFactory);
    }


    private HashMap<IPredicate, IRelation> loadFactsFromFiles(List<String> factsFiles) {

        HashMap<IPredicate, IRelation> factMap=new HashMap<>();
        for (String filename : factsFiles) {

            try {
            BufferedReader fr = FileUtils.getBufferedUTF8Reader(filename);
                Parser parser=new Parser();
                parser.parse(fr);

                // Retrieve the facts and put all of them in factMap

                factMap.putAll(parser.getFacts());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return factMap;
    }

    private List<IRule> loadRules(List<String> rulesFilenames) {
        List<IRule> rules=new ArrayList<>();

        for (String filename:rulesFilenames) {
            try {
                Parser parser=new Parser();
                BufferedReader fr = FileUtils.getBufferedUTF8Reader(filename);
                parser.parse(fr);
                rules.addAll(parser.getRules());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
      return rules;

    }


    @Override
    public IExplanation check(IQuery query) {
        try {

           // return (IExplaination) evaluationStrategy.evaluateQuery(query,null);

            IExplanation relation = evaluator.getExplanation(query);
            return  relation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public IExplanation check(Fact fact) {

        Parser parser = new Parser();
        try {
            parser.parse(fact.getIRISRepresenation());
            IQuery query = parser.getQueries().get(0);
            return check(query);

        } catch (ParserException e) {
            e.printStackTrace();
        }
        return null;

    }





}

