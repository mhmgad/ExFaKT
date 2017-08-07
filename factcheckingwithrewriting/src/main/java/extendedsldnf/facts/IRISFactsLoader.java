package extendedsldnf.facts;

import config.Configuration;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import utils.DataUtils;

import java.io.BufferedReader;
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
    public List<IQuery> parseQueries(BufferedReader fileReader) {
        List<IQuery> queries=new LinkedList<>();
        Parser pr=new Parser();
        try {
            pr.parse(fileReader);
        } catch (ParserException e) {
            e.printStackTrace();
        }
        queries.addAll(pr.getQueries());
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


}
