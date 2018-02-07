package extendedsldnf;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.topdown.ILiteralSelector;
import org.deri.iris.facts.IFacts;

import java.util.*;
import java.util.stream.Collectors;

public class OptimizedLiteralSelector implements ILiteralSelector {

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
        if( list.isEmpty())
            return null;
        else{
            //Check upper bound instances mapping in KG
            HashMap<ILiteral,Integer> upperboundInstances= new HashMap<>();

//            for (ILiteral l : list) {
//
//                // for debugging
////                if(varsMapList.size()>100)
////                    System.out.println("["+getClass().getName()+"] "+l.toString()+"\t"+varsMapList.size());
//                upperboundInstances.put(l,);
//
//
//            }


            Comparator<ILiteral> dataSizeComparator =new Comparator<ILiteral>(){
                @Override
                public int compare(ILiteral o1, ILiteral o2) {
                    if(!upperboundInstances.containsKey(o1)) {
                        upperboundInstances.put(o1,FactsUtils.getMatchingFactCount(o1,factsSource));
                    }
                    if(!upperboundInstances.containsKey(o2)) {
                        upperboundInstances.put(o2,FactsUtils.getMatchingFactCount(o2,factsSource));
                    }
                    return (upperboundInstances.get(o1)- upperboundInstances.get(o2));
                }

            };


            List<ILiteral> builtInList=list.stream().filter(a-> (a.getAtom() instanceof IBuiltinAtom)).collect(Collectors.toList());
            List<ILiteral> sortingList=list.stream().filter(a-> !(a.getAtom() instanceof IBuiltinAtom)).sorted(variablesSizeComparator.thenComparing(dataSizeComparator)).collect(Collectors.toList());
            //sortingList.sort();
            sortingList.addAll(builtInList);

//            System.out.println( Joiner.on(", ").join(list)+"\t"+Joiner.on(", ").join(sortingList.stream().map(k->upperboundInstances.get(k)).collect(Collectors.toList())));
            return sortingList.get(0);
        }



    }
}
