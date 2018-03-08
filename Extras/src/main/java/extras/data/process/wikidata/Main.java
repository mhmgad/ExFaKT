package extras.data.process.wikidata;


import com.google.common.base.Joiner;
import org.apache.commons.cli.*;
import output.listner.OutputListener;
import output.writers.AbstractOutputChannel;
import output.writers.ELasticSearchOutputWriter;
import output.writers.FileOutputWriter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    private DefaultParser parser;
    private Options options;
    private Option helpOp;
    private Option dumpFileOp;
    private Option outFileOp;
    private Option outTypeOp;
    private Option elasticSearchOp;
    private Option deleteIndexOp;


    public Main() {
        options= new Options();
        parser = new DefaultParser();
    }

    public void defineOptions() {

        //help option
        helpOp = new Option("h", false, "Show Help");
        helpOp.setLongOpt("help");
        options.addOption(helpOp);

        //input file
        dumpFileOp = Option.builder("i").required().longOpt("input").hasArg().desc("Wikidata Dump file (Check wiki-toolkit for supported dumps)").argName("file").build();
        options.addOption(dumpFileOp);

        //output file
        outFileOp = Option.builder("o").longOpt("outputFile").hasArg().desc("Output File").argName("file").build();
        options.addOption(outFileOp);

        //output file type
        outTypeOp = Option.builder("outTypes").longOpt("output formats").hasArg().desc("Output File formats coma separated, supported Types:"+ Joiner.on(',').join(Arrays.stream(AbstractOutputChannel.OutputFormat.values()).map(m->m.getFileExtension()).collect(Collectors.toList()))).argName("type").build();
        options.addOption(outTypeOp);

        //output to elastic
        elasticSearchOp = Option.builder("elastic").longOpt("elastic_search").hasArg().desc("Output to elastic").argName("serverUrl").build();
        options.addOption(elasticSearchOp);

//        //output to elastic
        deleteIndexOp = Option.builder("delIndex").longOpt("delete index").hasArg().desc("Delete current elastic index if exists").hasArg(false).build();
        options.addOption(deleteIndexOp);


    }

    private CommandLine parse(String[] args) throws ParseException {
        return parser.parse(options,args);
    }


    public void run(CommandLine cmd) throws Exception {



        if (cmd.hasOption(helpOp.getOpt())) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("get_labels.sh", options);
            System.exit(0);
        }


        String inputDump=cmd.getOptionValue(dumpFileOp.getOpt(), null);

        OutputListener<Paraphrase> outputListener = new OutputListener<Paraphrase>();



        if(cmd.hasOption(outFileOp.getOpt())){
            String baseFileName=cmd.getOptionValue(outFileOp.getOpt(), null);
            Set<AbstractOutputChannel.OutputFormat> formats=new HashSet<>();
            formats.add(AbstractOutputChannel.OutputFormat.TEXT);

            // Output formats
            if(cmd.hasOption(outTypeOp.getOpt())){
                String types=cmd.getOptionValue(outTypeOp.getOpt());
                formats=Arrays.stream(types.split(",")).map(t->t.trim().toUpperCase()).map(AbstractOutputChannel.OutputFormat::valueOf).collect(Collectors.toSet());
            }
            formats.forEach( format-> outputListener.registerWriter(new FileOutputWriter(baseFileName,format)));
        }

        if(cmd.hasOption(elasticSearchOp.getOpt())){
            boolean reIndex=cmd.hasOption(deleteIndexOp.getOpt());
            outputListener.registerWriter(new ELasticSearchOutputWriter(cmd.getOptionValue(elasticSearchOp.getOpt()),"paraphrases",reIndex,"paraphrase"));



        }

        if(outputListener.size()==0){
            System.out.println("Warning: No output writers are specified");

        }


        Helper.configureLogging();
        LabelsExtractor extractor=new LabelsExtractor(inputDump,outputListener);
        extractor.run();


    }

    public static void main(String[] args) throws Exception {

        Main instance = new Main();
        instance.defineOptions();
        instance.run(instance.parse(args));

    }
}
