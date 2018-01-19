package utils;

import config.Configuration;
import extendedsldnf.datastructure.IExtendedFacts;
import extendedsldnf.facts.FactsLoaderFactory;
import extendedsldnf.facts.IFactsLoader;
import mpi.tools.javatools.util.FileUtils;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by gadelrab on 5/31/17.
 */
public class DataUtils {



    /**
     * Creates IFacts source according to the type indicated in the configuration file
     * @param conf
     * @return
     */
    public static IExtendedFacts loadFacts(Configuration conf) {
       return loadFacts(conf,conf.getFactsFiles());
    }

    /**
     * Creates IFacts source according to the type indicated in the configuration file
     * @param conf
     * @return
     */
    public static IExtendedFacts loadFacts(Configuration conf,List<String> factsFiles) {
        IFactsLoader factsLoader= FactsLoaderFactory.getLoader(conf);
        return factsLoader.loadFacts(factsFiles);
    }




    public static List<IRule> loadRules(List<String> rulesFilenames) {

        List<IRule> rules=new ArrayList<>();

        for (String filename:new HashSet<>(rulesFilenames)) {
            System.out.print("Loading rules from: "+filename);
            try {
                Parser parser=new Parser();
                BufferedReader fr = FileUtils.getBufferedUTF8Reader(filename);
                parser.parse(fr);
                rules.addAll(parser.getRules());
                System.out.println(" .... Done!");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(" .... Error loading rules!");
            }

        }
        return rules;

    }

    public static LinkedHashMap<IQuery, Integer> loadQueries(Configuration conf, List<String> queriesFiles) {
        // we need to match the queries to facts type
        IFactsLoader factsLoader= FactsLoaderFactory.getLoader(conf);
        return factsLoader.parseQueries(queriesFiles);
    }

    public static LinkedHashMap<IQuery, Integer> loadQueries(Configuration conf) {
        // we need to match the queries to facts type
        return loadQueries(conf,conf.getQueiesFiles());
    }

    public static void main(String[] args) {
        List<IRule> mRules=DataUtils.loadRules(Arrays.asList("/home/gadelrab/ideaProjects/RuleBasedFactChecking/rules.tst"));

    }
}
