package extendedsldnf.facts;

import config.Configuration;
import de.mpii.utils.FactUtils;
import extendedsldnf.datastructure.InputQuery;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import org.deri.iris.terms.TermFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by gadelrab on 4/12/17.
 */
public class RDFFactsLoader extends IFactsLoader{



    static RDFFactsLoader instance;
    
    public RDFFactsLoader(IRelationFactory relationFactory) {
        super(relationFactory);
    }

    @Override
    public Map<IPredicate, IRelation> parseFacts(BufferedReader fileReader) {
        //TODO parse the file check if it is implemented in RDF-IRIS-reasoner

        Map<IPredicate, IRelation> factsMap=new HashMap<>();

        try {
            IBasicFactory basicFactory = BasicFactory.getInstance();
            ITermFactory termFactory= TermFactory.getInstance();
            IRelationFactory relationFactory=getRelationFactory();
            IRelation relation=null;

            for(String line=fileReader.readLine();line!=null;line=fileReader.readLine()){

                if(line==null || line.isEmpty())
                    continue;

                String[] parts=line.split("\t");

                // Create the predicate
                String cleanPredicateName= FactUtils.getCleanPredicateName(parts[1]);
                IPredicate predicate= basicFactory.createPredicate(cleanPredicateName,2);

                // arguments
                ITerm term1= termFactory.createString(parts[0]);
                ITerm term2= termFactory.createString(parts[2]);
                ITuple tuple= basicFactory.createTuple(term1,term2);

                // Add fact to map
                if(!factsMap.containsKey(predicate)){
                    relation=relationFactory.createRelation();
                    factsMap.put(predicate,relation);

                }else{
                    relation= factsMap.get(predicate);
                }

                relation.add(tuple);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return factsMap;
    }



    @Override
    public LinkedHashMap<InputQuery, Integer> parseQueries(BufferedReader fileReader) {
        //TODO parse the file check if it is implemented in RDF-IRIS-reasoner

        LinkedHashMap<InputQuery, Integer> queries=new LinkedHashMap<>();

        try {
            IBasicFactory basicFactory = BasicFactory.getInstance();
            ITermFactory termFactory= TermFactory.getInstance();
            IRelationFactory relationFactory=getRelationFactory();
            IRelation relation=null;

            int i=0;
            for(String line=fileReader.readLine();line!=null;line=fileReader.readLine()){

                if(line==null || line.isEmpty())
                    continue;

                i++;
                String[] parts=line.split("\t");

                // Create the predicate
                String cleanPredicateName= FactUtils.getCleanPredicateName(parts[1]);
                IPredicate predicate= basicFactory.createPredicate(cleanPredicateName,2);

                // arguments
                ITerm term1= parts[0].startsWith("?")? termFactory.createVariable(parts[0].substring(1)):termFactory.createString(parts[0]);
                ITerm term2= parts[2].startsWith("?")? termFactory.createVariable(parts[2].substring(1)):termFactory.createString(parts[2]);
                ITuple tuple= basicFactory.createTuple(term1,term2);

                IAtom atom = basicFactory.createAtom(predicate, tuple);


                ILiteral literal = basicFactory.createLiteral(true, atom);

                Integer label= (parts.length>3)? Integer.valueOf(parts[3]) :1;


                queries.put(new InputQuery(basicFactory.createQuery(literal),i,label),label);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  queries;
    }




    public static RDFFactsLoader getInstance(Configuration configuration) {
        return getInstance(configuration.relationFactory);
    }

    public static RDFFactsLoader getInstance(IRelationFactory relationFactory) {
        if(instance==null){
            instance=new RDFFactsLoader(relationFactory);
        }
        return instance;
    }
}
