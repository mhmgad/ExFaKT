package extendedsldnf.facts;

import extendedsldnf.datastructure.ExtendedFacts;
import extendedsldnf.datastructure.IExtendedFacts;
import mpi.tools.javatools.util.FileUtils;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import org.elasticsearch.common.lucene.search.Queries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by gadelrab on 4/11/17.
 */
public abstract class IFactsLoader {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public IRelationFactory relationFactory;

    public IFactsLoader(IRelationFactory relationFactory){
        this.relationFactory=relationFactory;
    }

    public IExtendedFacts loadFacts(File file) {
        try {
            return loadFacts(FileUtils.getBufferedUTF8Reader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public IExtendedFacts loadFacts(BufferedReader bufferedUTF8Reader) {

        return new ExtendedFacts(parseFacts(bufferedUTF8Reader),relationFactory);

    }


    public IExtendedFacts loadFacts(String file) {
        try {
            return  loadFacts(FileUtils.getBufferedUTF8Reader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return  null;
    }


    public IExtendedFacts loadFacts(FileInputStream file) {
        return loadFacts(FileUtils.getBufferedUTF8Reader(file));
    }


    public IExtendedFacts loadFacts(List<String> filePaths) {


         ExtendedFacts all=new ExtendedFacts(relationFactory);

        for (String filePath:new HashSet<>(filePaths)) {
            IExtendedFacts  facts=loadFacts(filePath);
            all.addAll(facts);
        }

//        logger.debug("all fact"+ all.toString());

        return all;

    }

    public LinkedHashMap<IQuery, Integer> parseQueries(List<String> filePaths) {
        LinkedHashMap<IQuery, Integer> queries=new LinkedHashMap<>();

        for (String filePath:filePaths) {
//            queries.addAll( parseQueries(filePath));
            queries.putAll( parseQueries(filePath));
        }
        return queries;
    }

    public LinkedHashMap<IQuery, Integer> parseQueries(String file) {
        try {
            return  parseQueries(FileUtils.getBufferedUTF8Reader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return  null;
    }


    public abstract LinkedHashMap<IQuery, Integer> parseQueries(BufferedReader fileReader);

    public abstract Map<IPredicate, IRelation> parseFacts(BufferedReader fileReader);

    public LinkedHashMap<IQuery, Integer> parseQueries(FileInputStream file) {
        return parseQueries(FileUtils.getBufferedUTF8Reader(file));
    }

    public IRelationFactory getRelationFactory() {
        return relationFactory;
    }

    public void setRelationFactory(IRelationFactory relationFactory) {
        this.relationFactory = relationFactory;
    }
}
