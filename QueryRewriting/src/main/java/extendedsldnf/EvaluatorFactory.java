package extendedsldnf;

import config.Configuration;
import extendedsldnf.datastructure.AbstractQueriesPool;
import extendedsldnf.datastructure.IExtendedFacts;
import org.deri.iris.api.basics.IRule;
import text.FactSpottingConnector;
import text.ITextConnector;
import utils.Enums;

import java.util.List;

/**
 * Created by gadelrab on 7/16/17.
 */
public class EvaluatorFactory {


    private final Configuration config;

    public EvaluatorFactory(Configuration config) {
    this.config=config;
    }


//    public ExtendedSLDNFEvaluator getEvaluator(IExtendedFacts facts, List<IRule> rules){
//        return getEvaluator(config.getEvaluationMethod(),facts,rules);
//    }


    public RecSLDEvaluator getEvaluator(List<IExtendedFacts> facts, List<ITextConnector> uncertainResources,List<IRule> rules) {
//       return getEvaluator(config.getEvaluationMethod(),facts,rules,new FactSpottingConnector(config),config.getPartialBindingType(),config.isSuspectsFromKG() );
        return getEvaluator(config.getEvaluationMethod(),facts,rules,uncertainResources,config.getPartialBindingType(),config.isSuspectsFromKG() );

    }

    public RecSLDEvaluator getEvaluator(Enums.EvalMethod evalMethod, List<IExtendedFacts> facts, List<IRule> rules, List<ITextConnector> factSpottingConnectors, Configuration.PartialBindingType partialBindingType, boolean suspectsFromKG) {
        switch (evalMethod){

            case SLD_ITR:
                return new ItrSLDEvaluator(facts, rules,factSpottingConnectors,partialBindingType,suspectsFromKG, AbstractQueriesPool.ComparisionMethod.DFS,config.getMaxExplanations(),config.getMaxRuleNesting() );
            case HEURISTIC:
                return new ItrSLDEvaluator(facts, rules,factSpottingConnectors,partialBindingType,suspectsFromKG, AbstractQueriesPool.ComparisionMethod.HEURISTIC,config.getMaxExplanations(),config.getMaxRuleNesting()  );
            case SLD:
            default:
                return new RecSLDEvaluator(facts, rules,factSpottingConnectors,partialBindingType,suspectsFromKG,config.getMaxExplanations(),config.getMaxRuleNesting() );

        }

    }
}
