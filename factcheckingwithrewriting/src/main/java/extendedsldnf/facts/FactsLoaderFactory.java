package extendedsldnf.facts;

import config.Configuration;

/**
 * Created by gadelrab on 4/12/17.
 */
public class FactsLoaderFactory {


  public static IFactsLoader getLoader(Configuration configuration){

      switch (configuration.getFactsFormat()){
          case RDF:
              return  RDFFactsLoader.getInstance(configuration);
          case IRIS:
          default:
              return IRISFactsLoader.getInstance(configuration);
      }


  }


}
