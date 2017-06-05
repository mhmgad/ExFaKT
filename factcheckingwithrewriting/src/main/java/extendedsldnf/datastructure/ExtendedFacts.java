package extendedsldnf.datastructure;

import com.google.common.collect.Sets;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.basics.Literal;
import org.deri.iris.facts.Facts;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by gadelrab on 4/10/17.
 */
public class ExtendedFacts extends Facts implements IExtendedFacts{
    private Logger logger = LoggerFactory.getLogger(getClass());

//
//    SetMultimap<IPredicate,ITerm> entitiesMap;
    /**
     * Set of entities in the graph
     */
    Set<ITerm> entities;
//    Set<IRelation> possibleRelations;



    public ExtendedFacts(IRelationFactory relationFactory) {
        super(relationFactory);
        entities=new HashSet<>();
        fillEntities();
    }

    public ExtendedFacts(Map<IPredicate, IRelation> rawFacts, IRelationFactory relationFactory) {
        super(rawFacts, relationFactory);
//        entitiesMap= HashMultimap.create();
        entities=new HashSet<>();

//        possibleRelations=new HashSet<>();
        fillEntities();
        logger.debug("Entities in KG "+entities.size());
    }



    private void fillEntities() {



        //List<IRelation> predicatesFacts=getPredicates().stream().map(p->get(p)).collect(Collectors.toList());
        for (IPredicate predicate:getPredicates()) {
            IRelation predicateFacts=get(predicate);
            for (int i=0;i<predicateFacts.size();i++){
                ITuple t=predicateFacts.get(i);
                entities.addAll(t);
            }

        }



    }

    /**
     * All possible binding rather than the existing ones.
     * @param literal
     * @return
     */
    public IRelation getHypotheticalBindings(ILiteral literal){
        Set<List<ITerm>> permutations = getPermutations(literal);


        // create tuples and relations as entities combinations
        IBasicFactory basicsFact=BasicFactory.getInstance();
        IRelation facts = get(literal.getAtom().getPredicate());
        IRelation generatedRelation = mRelationFactory.createRelation();
        permutations.stream().map(l -> basicsFact.createTuple(l)).filter(t-> (!facts.contains(t))).forEach(t-> generatedRelation.add(t));

        return generatedRelation;


    }

    @Override
    public boolean addAll(IExtendedFacts facts) {

        facts.getPredicates().forEach(
                pred-> {


                    IRelation predicateFacts=facts.get(pred);
//                    logger.debug(predicateFacts.toString());
                    this.get(pred).addAll(predicateFacts);

                    // add entities to map
                    for (int i=0;i<predicateFacts.size();i++){
                        ITuple t=predicateFacts.get(i);
                        entities.addAll(t);
                    }

        });

        return true;
    }

//    @Override
//    public boolean deleteFact(ILiteral literal) {
//        IRelation predicateTuples = get(literal.getAtom().getPredicate());
//        if(predicateTuples==null)
//            return false;
//
//        ITuple tupleToDelete = literal.getAtom().getTuple();
//        if(!predicateTuples.contains(tupleToDelete))
//            return false;
//        else{
//            predicateTuples.
//        }
//
//    }

    /**
     * gets all possible combinations for a predicate
     * @param literal
     * @return
     */
    private Set<List<ITerm>> getPermutations(ILiteral literal) {

        List<Set<ITerm>> listsToRelations=new ArrayList<>();
        ITuple tuple=literal.getAtom().getTuple();
        for (ITerm arg:tuple) {
            Set<ITerm> set=new HashSet<>();
            if(arg.isGround()){

                set.add(arg);
                listsToRelations.add(set);
            }
            else
            {
                listsToRelations.add(entities);
            }


        }
//        logger.debug(literal+" "+listsToRelations);

        return Sets.cartesianProduct(listsToRelations);

    }





}
