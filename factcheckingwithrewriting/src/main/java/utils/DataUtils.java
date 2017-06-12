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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        IFactsLoader factsLoader= FactsLoaderFactory.getLoader(conf);
        return factsLoader.loadFacts(conf.getFactsFiles());
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


    public static List<IQuery> loadQueries(List<String> queiesFiles) throws FileNotFoundException, ParserException {

        List<IQuery> queries=new LinkedList<>();

        for (String filename:queiesFiles) {
            BufferedReader br = FileUtils.getBufferedUTF8Reader(filename);

            Parser pr=new Parser();
            pr.parse(br);
            queries.addAll(pr.getQueries());
        }


        return queries;
    }
}
