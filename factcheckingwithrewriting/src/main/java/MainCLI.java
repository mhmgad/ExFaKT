import config.Configuration;
import extendedsldnf.datastructure.IExplanation;
import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.cli.*;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;

import java.io.BufferedReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/30/17.
 */
public class MainCLI {



    private DefaultParser parser;
    private Options options;

    private Option helpOp;
    private Option confFileOp;


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
    }

    public void run(CommandLine cmd) throws Exception{

        // set configuration file
        String configurationFile=cmd.getOptionValue(confFileOp.getOpt(),null);
        Configuration.setConfigurationFile(configurationFile);



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
        List<IExplanation> explainations=new LinkedList<>();
        List<IQuery> queries=new LinkedList<>();
        if(!configuration.getQueiesFiles().isEmpty()){

            for (String filename:configuration.getQueiesFiles()) {
                BufferedReader br = FileUtils.getBufferedUTF8Reader(filename);

                Parser pr=new Parser();
                pr.parse(br);
                queries.addAll(pr.getQueries());
                explainations.addAll(processList(rfc,queries));

            }
        }

        Iterator<IExplanation> explansItr=explainations.iterator();
        for (IQuery query:queries) {
            System.out.println(query+":\t"+explansItr.next());

        }

    }

    private static List<IExplanation> processList(RuleBasedChecker rfc, List<IQuery> queries) {
        return queries.stream().map(q->rfc.check(q)).collect(Collectors.toList());
    }
}
