package extendedsldnf.facts;

import config.Configuration;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.compiler.Parser;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;

import java.io.BufferedReader;
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
