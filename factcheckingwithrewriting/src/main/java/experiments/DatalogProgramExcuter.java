package experiments;

import config.Configuration;
import extendedsldnf.datastructure.IExtendedFacts;

import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.collections4.ListUtils;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.evaluation.IEvaluationStrategy;
import org.deri.iris.evaluation.IEvaluationStrategyFactory;
import org.deri.iris.evaluation.stratifiedbottomup.StratifiedBottomUpEvaluationStrategy;
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
public class DatalogProgramExcuter {


    private  Configuration config;
    StratifiedBottomUpEvaluationStrategyFactory evaluationStrategyFactory;
    List<IQuery> queries;
    List<IRule> rules;
    IExtendedFacts facts;


    public DatalogProgramExcuter(List<String> factsFile,List<String> rulesFiles,List<String> queriesFiles) {
        config = Configuration.getInstance();
        facts=DataUtils.loadFacts(config);
        rules= DataUtils.loadRules(rulesFiles);
        evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new SemiNaiveEvaluatorFactory() );

        try {
            queries=DataUtils.loadQueries(queriesFiles);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }

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
                    System.out.println(relation.get(i));
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
        List<String> rules=StringUtils.asList(args[1]);
        List<String> queries=StringUtils.asList(args[2]);

        String outputFile=args[3];


        DatalogProgramExcuter executer=new DatalogProgramExcuter(facts,rules,queries);

        executer.execute(outputFile);





    }








}
