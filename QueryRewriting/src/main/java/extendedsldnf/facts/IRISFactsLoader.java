package extendedsldnf.facts;

import config.Configuration;
import mpi.tools.javatools.util.FileUtils;
import org.apache.xpath.operations.Bool;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import org.deri.iris.storage.simple.SimpleRelationFactory;
import utils.DataUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by gadelrab on 4/11/17.
 */
public class IRISFactsLoader extends IFactsLoader{


    static IRISFactsLoader instance;




    IRISFactsLoader(IRelationFactory relationFactory) {
        super(relationFactory);
        //this.relationFactory = relationFactory;
    }

    @Override
    public LinkedHashMap<IQuery, Integer> parseQueries(BufferedReader fileReader) {
        LinkedHashMap<IQuery, Integer> queries=new LinkedHashMap<>();


        try {
            for (String line=fileReader.readLine();line!=null;line=fileReader.readLine()) {
                if(line.isEmpty())
                    continue;
                String[] queryAndLabel=line.trim().split("\t");
                Parser pr=new Parser();
                pr.parse(queryAndLabel[0]);
                Integer label= (queryAndLabel.length>1)? Integer.valueOf(queryAndLabel[1]) :1;
                pr.getQueries().forEach(q-> queries.put(q,label));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return queries;
    }


    public Map<IPredicate, IRelation> parseFacts(BufferedReader filereader) {
        Parser parser=new Parser();
        try {
            parser.parse(filereader);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return parser.getFacts();

    }


    public static IRISFactsLoader getInstance(Configuration configuration) {
        return getInstance(configuration.relationFactory);
    }

    public static IRISFactsLoader getInstance(IRelationFactory relationFactory) {
        if(instance==null){
            instance=new IRISFactsLoader(relationFactory);
        }
        return instance;
    }

    public static void main(String[] args) throws FileNotFoundException {
        IRISFactsLoader in = getInstance(new SimpleRelationFactory());
        BufferedReader fileReader = FileUtils.getBufferedUTF8Reader("testQueries.iris");
        System.out.println(in.parseQueries(fileReader));


    }


}
