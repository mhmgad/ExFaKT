package extendedsldnf.facts;

import config.Configuration;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;

import static config.Configuration.FactsFormat.RDF;

/**
 * Created by gadelrab on 4/12/17.
 */
public class FactsLoaderFactory {


  public static IFactsLoader getLoader(Configuration configuration){
        return getLoader(configuration.getFactsFormat(),configuration.relationFactory);

  }

    public static IFactsLoader getLoader(Configuration.FactsFormat factsFormat, IRelationFactory relationFactory){

        switch (factsFormat){
            case RDF:
                return  RDFFactsLoader.getInstance(relationFactory);
            case IRIS:
            default:
                return IRISFactsLoader.getInstance(relationFactory);
        }


    }










}
