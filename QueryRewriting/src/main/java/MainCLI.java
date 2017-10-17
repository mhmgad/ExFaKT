import checker.ExplanationsExtractor;
import checker.FactChecker;
import config.Configuration;
import datastructure.CorrectnessInfo;
import extendedsldnf.datastructure.IQueryExplanations;
import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.cli.*;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IQuery;
import utils.DataUtils;
import utils.Enums;
import utils.eval.ResultsEvaluator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
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
    private BufferedWriter outputCorrectnessFile;
    private Option maxExplanationsOp;
    private Option maxRulesNestingOp;


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
        checkFactOp =Option.builder("cf").longOpt("checkFact").hasArg(false).desc( "Check correctness of a fact").build();
        options.addOption(checkFactOp);

        // Max explanations
        maxExplanationsOp =Option.builder("maxExp").longOpt("max-explanations").hasArg(true).argName("explans").desc( "Maximum number of explanations per query").build();
        options.addOption(maxExplanationsOp);

        // Max explanations
        maxRulesNestingOp =Option.builder("maxR").longOpt("max-rules").hasArg(true).argName("rules").desc( "Maximum number of nested rules per explanation").build();
        options.addOption(maxRulesNestingOp);


    }

    public void run(CommandLine cmd) throws Exception{

        // set configuration file
        String configurationFile=cmd.getOptionValue(confFileOp.getOpt(),null);
        Configuration.setConfigurationFile(configurationFile);

        Configuration configuration=Configuration.getInstance();

        if(cmd.hasOption(helpOp.getOpt())){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "check_facts.sh", options );
            System.exit(0);
        }

        //set output
        String outputFilePath = cmd.getOptionValue(outFileOp.getOpt(), null);
        if(outputFilePath!=null){
            outputFile=FileUtils.getBufferedUTF8Writer(outputFilePath);
            outputStatsFile=FileUtils.getBufferedUTF8Writer(outputFilePath+".stats");
            outputStatsSummaryFile=FileUtils.getBufferedUTF8Writer(outputFilePath+".stats.summary");
            outputCostFile=FileUtils.getBufferedUTF8Writer(outputFilePath+".cost");
            outputCostSummaryFile=FileUtils.getBufferedUTF8Writer(outputFilePath+".cost.summary");
            if(cmd.hasOption(checkFactOp.getOpt())){
                outputCorrectnessFile=FileUtils.getBufferedUTF8Writer(outputFilePath+".correctness");
            }

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

        if(cmd.hasOption(maxExplanationsOp.getOpt())) {
            configuration.setMaxExplanations(Integer.valueOf(cmd.getOptionValue(maxExplanationsOp.getOpt(), "5")));
        }

        if(cmd.hasOption(maxRulesNestingOp.getOpt())) {
            configuration.setMaxRuleNesting(Integer.valueOf(cmd.getOptionValue(maxRulesNestingOp.getOpt(), "5")));
        }

        List<IQueryExplanations> explanations;
        if(cmd.hasOption(checkFactOp.getOpt())){
            explanations=checkFact(configuration);
        }
        else {
            explanations=extractExplanations(configuration);

        }




    }

    private List<IQueryExplanations> checkFact(Configuration configuration) throws EvaluationException, IOException {

//        Fact f=new Fact("diedIn", Arrays.asList("John F. Kennedy","Dallas"));

        LinkedHashMap<IQuery, Integer> queries = DataUtils.loadQueries(configuration);
        FactChecker fc=new FactChecker();

        List<CorrectnessInfo> correctnessInfos=queries.keySet().parallelStream().map(q->fc.checkCorrectness(q,queries.get(q))).collect(Collectors.toList());
        List<IQueryExplanations> explanations=correctnessInfos.stream().map(CorrectnessInfo::getPosNegExplanations).flatMap(List::stream).collect(Collectors.toList());




        outputExplanations(explanations);
        evaluateResults(explanations,correctnessInfos,queries);



        return explanations;

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

    private List<IQueryExplanations> extractExplanations(Configuration configuration) throws EvaluationException, IOException {
        ExplanationsExtractor rfc=new ExplanationsExtractor();
        LinkedHashMap<IQuery, Integer> queries = DataUtils.loadQueries(configuration);

        List<IQueryExplanations> explanations=queries.keySet().parallelStream().map(q->rfc.check(q)).collect(Collectors.toList());

//        Iterator<IExplanation> explansItr=explainations.iterator();

        outputExplanations(explanations);
        evaluateResults(explanations,null,queries);

        return explanations;
    }

    private void outputExplanations(List<IQueryExplanations> explanations) throws IOException {
        for (IQueryExplanations explanation:explanations) {
            String result="\nQuery:\t"+explanation.getQuery()+"\n"+explanation+"\n";
            System.out.println(result);
            if(this.outputFile!=null)
                this.outputFile.write(result);

        }
        if(this.outputFile!=null)
            this.outputFile.close();




        System.out.println("Recall: "+explanations.stream().filter(exp-> !exp.isEmpty()).count()+"/"+(explanations.size()));
    }

    private void evaluateResults(List<IQueryExplanations> explanations,List<CorrectnessInfo> correctnessInfo,LinkedHashMap<IQuery, Integer> groundTruth) {


        ResultsEvaluator evals=new ResultsEvaluator(explanations,correctnessInfo);

        System.out.println(evals.toString());

        if(this.outputFile!=null) {
            evals.dump(this.outputStatsFile);
            evals.dumpSummary(this.outputStatsSummaryFile);

        }

        if(this.outputCostFile!=null){
            evals.dumpCost(this.outputCostFile);
            evals.dumpCostSummary(this.outputCostSummaryFile);

        }

        if(this.outputCorrectnessFile!=null){

            evals.dumpCorrectnessInfo(outputCorrectnessFile);

//            System.out.println("Accuracy: "+evals.evaluateRanking(groundTruth));

        }


    }


}
