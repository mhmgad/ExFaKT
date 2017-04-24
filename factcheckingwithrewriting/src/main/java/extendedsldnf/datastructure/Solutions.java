package extendedsldnf.datastructure;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.storage.IRelation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gadelrab on 3/22/17.
 */
//TODO get red off the IRelation interface, It is useless but it is easier than implementing the whole thing form scratch
public class Solutions implements IRelation, IExplanation {

    /**
     * successful rewriting paths
     */
    Set<RewritingPath> rewritingPaths;


    public Solutions() {
        this(new ArrayList<>());
    }

    public Solutions(List<RewritingPath> rewritingPaths) {
        this.rewritingPaths = new LinkedHashSet<>(rewritingPaths) ;
    }

    @Override
    public boolean add(ITuple tuple) {
        //TODO Remove this
        System.out.println("Adding to Relation is not supported!");
        return false;
    }

    @Override
    public boolean addAll(IRelation relation) {
        //TODO Remove this
        System.out.println("Adding to Relation is not supported!");
        return false;
    }

    @Override
    public int size() {
        //TODO Remove this
        System.out.println("Size Of relation is not supported is not supported!");
        return 0;
    }

    @Override
    public ITuple get(int index) {
        //TODO Remove this
        System.out.println("Getting tuple is not supported!");
        return null;
    }

    @Override
    public boolean contains(ITuple tuple) {
        //TODO Remove this
        System.out.println("contains is not supported!");
        return false;
    }


    @Override
    public String toString() {
        return "Solutions{" +
                rewritingPaths +
                '}';
    }
}
