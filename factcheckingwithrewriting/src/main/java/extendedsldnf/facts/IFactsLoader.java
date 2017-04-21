package extendedsldnf.facts;

import extendedsldnf.datastructure.ExtendedFacts;
import extendedsldnf.datastructure.IExtendedFacts;
import mpi.tools.javatools.util.FileUtils;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.compiler.Parser;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Created by gadelrab on 4/11/17.
 */
public abstract class IFactsLoader {

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

        for (String filePath:filePaths) {
            IExtendedFacts facts=loadFacts(filePath);
            facts.getPredicates().forEach(pred-> all.get(pred).addAll(facts.get(pred)));
        }

        return all;

    }

    public abstract Map<IPredicate, IRelation> parseFacts(BufferedReader filereader);


    public IRelationFactory getRelationFactory() {
        return relationFactory;
    }

    public void setRelationFactory(IRelationFactory relationFactory) {
        this.relationFactory = relationFactory;
    }
}
