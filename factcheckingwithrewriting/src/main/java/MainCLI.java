import config.Configuration;
import de.mpii.datastructures.Fact;
import extendedsldnf.datastructure.IQueryExplanations;
import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.cli.*;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.ParserException;
import utils.DataUtils;
import utils.Enums;
import utils.eval.ResultsEvaluator;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Created by gadelrab on 3/30/17.
 */
public class MainCLI {



    private DefaultParser parser;
    private Options options;

    private Option helpOp;
    private Option confFileOp;
    private Option outFileOp;
    private BufferedWriter outputFile;
    private BufferedWriter outputStatsFile;
    private BufferedWriter outputStatsSummaryFile;
    private Option rulesFilesOp;
    private Option queriesFilesOp;
    private Option factsFilesOp;
    private Option spottingConfFileOp;

    private BufferedWriter outputCostFile;
    private BufferedWriter outputCostSummaryFile;
    private Option evalMethodOp;
    private Option checkFactOp;


    public MainCLI() {
        options= new Options();
        parser = new DefaultParser();
    }

    public void defineOptions() {

        //help option
        helpOp = new Option("h", false, "Show Help");
        helpOp.setLongOpt("help");
        options.addOption(helpOp);

        //input file
        confFileOp = Option.builder("conf").longOpt("configurationFile").hasArg().desc("Input configuration File").argName("file").build();
        options.addOption(confFileOp);

        //rules file
        rulesFilesOp = Option.builder("r").longOpt("rulesFiles").hasArg().desc("Rules File").argName("file").build();
        options.addOption(rulesFilesOp);


        //query files
        queriesFilesOp = Option.builder("q").longOpt("queryFiles").hasArg().desc("Query File in IRIS format").argName("file").build();
        options.addOption(queriesFilesOp);


        //input file
        outFileOp = Option.builder("o").longOpt("outputFile").hasArg().desc("Output File").argName("file").build();
        options.addOption(outFileOp);

        //input file
        factsFilesOp = Option.builder("f").longOpt("factsFiles").hasArg().desc("Facts File").argName("file").build();
        options.addOption(factsFilesOp);


        //input file
        spottingConfFileOp = Option.builder("spotConf").longOpt("spottingConfigurationFile").hasArg().desc("Spotting configuration File").argName("file").build();
        options.addOption(spottingConfFileOp);


        //Evaluator Implementation
        evalMethodOp = Option.builder("eval").longOpt("EvaluationMethod").hasArg().desc("Evaluation Method").argName("method").build();
        options.addOption(evalMethodOp);


        // check facts or normal flow
        checkFactOp =Option.builder("cf").longOpt("ckeckFact").hasArg(false).desc( "Check correctness of a fact").build();
        options.addOption(checkFactOp);
    }

    public void run(CommandLine cmd) throws Exception{

        // set configuration file
        String configurationFile=cmd.getOptionValue(confFileOp.getOpt(),null);
        Configuration.setConfigurationFile(configurationFile);

        Configuration configuration=Configuration.getInstance();

        //set output
        String outputFilePath=cmd.getOptionValue(outFileOp.getOpt(),null);
        if(outputFilePath!=null){
            outputFile=FileUtils.getBufferedUTF8Writer(outputFilePath);
            outputStatsFile=FileUtils.getBufferedUTF8Writer(outputFilePath+".stats");
            outputStatsSummaryFile=FileUtils.getBufferedUTF8Writer(outputFilePath+".stats.summary");
            outputCostFile=FileUtils.getBufferedUTF8Writer(outputFilePath+".cost");
            outputCostSummaryFile=FileUtils.getBufferedUTF8Writer(outputFilePath+".cost.summary");
        }

        if(cmd.hasOption(rulesFilesOp.getOpt())) {
            configuration.setRulesFiles(asList(cmd.getOptionValue(rulesFilesOp.getOpt(),null)));

        }

        if(cmd.hasOption(queriesFilesOp.getOpt())) {
            configuration.setQueiesFiles(asList(cmd.getOptionValue(queriesFilesOp.getOpt(),null)));

        }

        if(cmd.hasOption(factsFilesOp.getOpt())) {
            configuration.setFactsFiles(asList(cmd.getOptionValue(factsFilesOp.getOpt(),null)));
        }

        if(cmd.hasOption(spottingConfFileOp.getOpt())) {
            configuration.setSpottingConfFile(cmd.getOptionValue(spottingConfFileOp.getOpt(),null));
        }

        if(cmd.hasOption(evalMethodOp.getOpt())) {
            configuration.setEvaluationMethod(Enums.EvalMethod.valueOf(cmd.getOptionValue(evalMethodOp.getOpt(), Enums.EvalMethod.SLD.name())));
        }

        if(cmd.hasOption(checkFactOp.getOpt())){
            checkFact(configuration);
        }
        else {
            extractExplanations( configuration);
        }


    }

    private void checkFact(Configuration configuration) throws EvaluationException, IOException, ParserException {

        Fact f=new Fact("diedIn", Arrays.asList("John F. Kennedy","Dallas"));

        List<IQuery> queries = DataUtils.loadQueries(configuration.getQueiesFiles());
        FactChecker fc=new FactChecker();

        List<FactChecker.CorrectnessInfo> correctnessInfos=queries.parallelStream().map(q->fc.checkCorrectness(q)).collect(Collectors.toList());

        for (FactChecker.CorrectnessInfo info:correctnessInfos) {
            String result="\n"+info+"\n";
            System.out.println(result);
            if(this.outputFile!=null)
                this.outputFile.write(result);

        }
        if(this.outputFile!=null)
            this.outputFile.close();

    }

    private CommandLine parse(String[] args) throws ParseException {
        return parser.parse(options,args);
    }

    public static void main(String[] args) throws Exception {

        MainCLI instance = new MainCLI();
        instance.defineOptions();
        instance.run(instance.parse(args));

//        Configuration configuration=Configuration.getInstance();



    }

    private void extractExplanations(Configuration configuration) throws EvaluationException, ParserException, IOException {
        ExplanationsExtractor rfc=new ExplanationsExtractor();
        List<IQuery> queries = DataUtils.loadQueries(configuration.getQueiesFiles());

        List<IQueryExplanations> explanations=queries.parallelStream().map(q->rfc.check(q)).collect(Collectors.toList());

//        Iterator<IExplanation> explansItr=explainations.iterator();
        for (IQueryExplanations explanation:explanations) {
            String result="\nQuery:\t"+explanation.getQuery()+"\n"+explanation+"\n";
            System.out.println(result);
            if(this.outputFile!=null)
                this.outputFile.write(result);

        }
        if(this.outputFile!=null)
            this.outputFile.close();


        System.out.println("Recall: "+explanations.stream().filter(exp-> !exp.isEmpty()).count()+"/"+(queries.size()));

        ResultsEvaluator evals=new ResultsEvaluator(explanations);

        System.out.println(evals.toString());

        if(this.outputFile!=null) {
            evals.dump(this.outputStatsFile);
            evals.dumpSummary(this.outputStatsSummaryFile);

        }

        if(this.outputCostFile!=null){
            evals.dumpCost(this.outputCostFile);
            evals.dumpCostSummary(this.outputCostSummaryFile);

        }
    }


}
