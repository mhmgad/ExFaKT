import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Fact;
import de.mpii.factspotting.IFactSpotter;
import extendedsldnf.datastructure.IExplaination;
import mpi.tools.javatools.util.FileUtils;
import org.deri.iris.EvaluationException;
import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.storage.IRelation;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gadelrab on 3/22/17.
 */
public class RuleBasedChecker implements IDeepChecker<IQuery>{


    /**
     * The engine used to spot facts
     */
    IFactSpotter<BinaryFact> factIFactSpotter;


    /**
     * Rewriting rules (iris rules)
     *
     */
    List<IRule> rules;

    /**
     * Facts  (iris rules)
     */
    Map<IPredicate,IRelation> factsMap;

    /**
     * FactChecking with Rewriting configuration
     */
    Configuration config;


    /**
     *KB (Where reasoning is performed... according to IRIS implementation)
     * Knowledge base objects hides the process of applying reasoners on the facts
     * It should be removed later
     */
    IKnowledgeBase knowledgeBase;





    public RuleBasedChecker() throws EvaluationException {
        // load Config
        config=Configuration.getInstance();
        // Load Rules
        rules=loadRules(config.getRulesFiles());
        // Load facts
        factsMap=loadFacts(config.getFactsFiles());

        //Create KB
        knowledgeBase= KnowledgeBaseFactory.createKnowledgeBase(factsMap,rules,config);


    }


    private HashMap<IPredicate, IRelation> loadFacts(List<String> factsFiles) {

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
    public IExplaination check(IQuery query) {
        try {

            return (IExplaination) knowledgeBase.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public IExplaination check(Fact fact) {

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

