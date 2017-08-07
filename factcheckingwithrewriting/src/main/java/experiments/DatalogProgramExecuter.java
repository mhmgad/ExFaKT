package experiments;

import config.Configuration;
import extendedsldnf.datastructure.IExtendedFacts;

import mpi.tools.javatools.util.FileUtils;
import org.apache.log4j.Logger;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.evaluation.stratifiedbottomup.StratifiedBottomUpEvaluationStrategyFactory;
import org.deri.iris.evaluation.stratifiedbottomup.seminaive.SemiNaiveEvaluatorFactory;
import org.deri.iris.storage.IRelation;
import utils.DataUtils;
import utils.StringUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by gadelrab on 5/31/17.
 */
public class DatalogProgramExecuter {

    Logger logger= Logger.getLogger(getClass());


    private  Configuration config;
    StratifiedBottomUpEvaluationStrategyFactory evaluationStrategyFactory;
    List<IQuery> queries;
    List<IRule> rules;
    IExtendedFacts facts;


    public DatalogProgramExecuter(List<String> factsFile, List<String> rulesFiles, List<String> queriesFiles) {
        config = Configuration.getInstance();
        facts=DataUtils.loadFacts(config,factsFile);
        logger.debug("loadedFacts "+facts.size());

        rules= DataUtils.loadRules(rulesFiles);
        logger.debug("loadedRules "+rules.size());


        evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new SemiNaiveEvaluatorFactory() );


            queries=DataUtils.loadQueries(config,queriesFiles);


    }

    public void execute(String outputfile){

        try {
            IEvaluationStrategy str=evaluationStrategyFactory.createEvaluator(facts,rules,config);
            BufferedWriter bw= FileUtils.getBufferedUTF8Writer(outputfile);
            for (IQuery q: queries) {
                List<IVariable> variableBindings = new ArrayList<IVariable>();
                IRelation relation =  str.evaluateQuery(q,variableBindings);



                // Output the variables.
                System.out.println(variableBindings);
// For performance reasons compute the relation size only once.
                int relationSize = relation.size();

                // Output each tuple in the relation, where the term at position i
                // corresponds to the variable at position i in the variable
                // bindings list.



                for (int i = 0; i < relationSize; i++) {
                    //System.out.println(relation.get(i));
                    bw.write("?- "+q.getLiterals().get(0).getAtom().getPredicate().toString()+relation.get(i).toString()+".");
                    bw.newLine();
                }

            }
    bw.close();


        } catch (EvaluationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        List<String> facts= StringUtils.asList(args[0]);
        System.out.println("facts: "+facts);
        List<String> rules=StringUtils.asList(args[1]);
        System.out.println("rules: "+rules);
        List<String> queries=StringUtils.asList(args[2]);
        System.out.println("queries: "+queries);
        String outputFile=args[3];


        DatalogProgramExecuter executer=new DatalogProgramExecuter(facts,rules,queries);

        executer.execute(outputFile);





    }








}
