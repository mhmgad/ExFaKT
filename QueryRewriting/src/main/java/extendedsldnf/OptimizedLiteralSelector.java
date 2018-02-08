package extendedsldnf;

import com.google.common.base.Joiner;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.evaluation.topdown.ILiteralSelector;
import org.deri.iris.facts.IFacts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class OptimizedLiteralSelector implements ILiteralSelector {

    private Logger logger= LoggerFactory.getLogger(OptimizedLiteralSelector.class);

    IFacts factsSource;

    public OptimizedLiteralSelector(IFacts factsSource) {
        this.factsSource = factsSource;
    }

    Comparator<ILiteral> variablesSizeComparator =new Comparator<ILiteral>(){

        @Override
        public int compare(ILiteral o1, ILiteral o2) {
            return (o1.getAtom().getTuple().getVariables().size()- o2.getAtom().getTuple().getVariables().size());
        }

    };





    @Override
    public ILiteral select(List<ILiteral> list) {

        ILiteral selectedLiteral=null;
        List<ILiteral> sortedList;
        if( list.isEmpty())
            return null;
        else {
            // split built-in predicates based on number of variables
            sortedList = list.stream().filter(a -> (a.getAtom() instanceof IBuiltinAtom)).sorted(variablesSizeComparator).collect(Collectors.toList());
            if (sortedList.size() == list.size()) {// only built-in predicates
                selectedLiteral = sortedList.get(0);
            } else {
                // sort non-built-in predicates based on number of variables.
                sortedList = list.stream().filter(a -> !(a.getAtom() instanceof IBuiltinAtom)).sorted(variablesSizeComparator).collect(Collectors.toList());
                if (sortedList.get(0).getAtom().isGround()) {
                    selectedLiteral = sortedList.get(0);
                } else {
                    //Check upper bound instances mapping in KG
                    HashMap<ILiteral, Integer> upperboundInstances = new HashMap<>();
                    Comparator<ILiteral> dataSizeComparator = new Comparator<ILiteral>() {
                        @Override
                        public int compare(ILiteral o1, ILiteral o2) {
                            if (!upperboundInstances.containsKey(o1)) {
                                // binding size from KG
                                int bindingSize = FactsUtils.getMatchingFactCount(o1, factsSource);
                                // this will be applied only on non-grounded atoms, if atom cannot match any items
                                upperboundInstances.put(o1, bindingSize == 0 ? Integer.MIN_VALUE : bindingSize);
                            }
                            if (!upperboundInstances.containsKey(o2)) {
                                int bindingSize = FactsUtils.getMatchingFactCount(o2, factsSource);
                                upperboundInstances.put(o2, bindingSize == 0 ? Integer.MIN_VALUE : bindingSize);
                            }

                            return (upperboundInstances.get(o1) - upperboundInstances.get(o2));
                        }

                    };

                    // sort non-built-in predicates based on number of variables then upperbound of KG bindings
                    sortedList = list.stream().filter(a -> !(a.getAtom() instanceof IBuiltinAtom)).sorted(variablesSizeComparator.thenComparing(dataSizeComparator)).collect(Collectors.toList());
                    selectedLiteral = sortedList.get(0);


                }
            }
        }


            logger.debug(Joiner.on(", ").join(sortedList)+"\tsel: "+selectedLiteral);

            return selectedLiteral;

//            System.out.println( Joiner.on(", ").join(list)+"\t"+Joiner.on(", ").join(sortingList.stream().map(k->upperboundInstances.get(k)).collect(Collectors.toList())));
        }




    }
