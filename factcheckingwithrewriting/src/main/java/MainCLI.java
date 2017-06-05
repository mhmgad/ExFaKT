import config.Configuration;
import extendedsldnf.datastructure.IQueryExplanations;
import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.cli.*;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import utils.DataUtils;
import utils.eval.ResultsEvaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
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

    }

    private CommandLine parse(String[] args) throws ParseException {
        return parser.parse(options,args);
    }

    public static void main(String[] args) throws Exception {

        MainCLI instance = new MainCLI();
        instance.defineOptions();
        instance.run(instance.parse(args));

        Configuration configuration=Configuration.getInstance();

        RuleBasedChecker rfc=new RuleBasedChecker();
        List<IQueryExplanations> explanations=new LinkedList<>();
        List<IQuery> queries = DataUtils.loadQueries(configuration.getQueiesFiles());

        explanations.addAll(processList(rfc,queries));

//        Iterator<IExplanation> explansItr=explainations.iterator();
        for (IQueryExplanations explanation:explanations) {
            String result="\nQuery:\t"+explanation.getQuery()+"\n"+explanation+"\n";
            System.out.println(result);
            if(instance.outputFile!=null)
                instance.outputFile.write(result);

        }
        if(instance.outputFile!=null)
            instance.outputFile.close();


        System.out.println("Recall: "+explanations.stream().filter(exp-> !exp.isEmpty()).count()+"/"+(queries.size()));

        ResultsEvaluator evals=new ResultsEvaluator(explanations);

        System.out.println(evals.toString());

        if(instance.outputFile!=null) {
            evals.dump(instance.outputStatsFile);
            evals.dumpSummary(instance.outputStatsSummaryFile);

        }




    }



    private static List<IQueryExplanations> processList(RuleBasedChecker rfc, List<IQuery> queries) {
        return queries.stream().map(q->rfc.check(q)).collect(Collectors.toList());
    }
}
