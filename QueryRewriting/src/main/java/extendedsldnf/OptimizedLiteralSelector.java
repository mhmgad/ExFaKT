package extendedsldnf;

import com.google.common.base.Joiner;
import org.deri.iris.api.basics.ILiteral;
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

            for (ILiteral l : list) {
                List<Map<IVariable, ITerm>> varsMapList=new LinkedList<>();
                FactsUtils.getMatchingFacts(l,varsMapList,factsSource);
                upperboundInstances.put(l,varsMapList.size());


            }
            Comparator<ILiteral> dataSizeComparator =new Comparator<ILiteral>(){
                @Override
                public int compare(ILiteral o1, ILiteral o2) {
                    return (upperboundInstances.get(o1)- upperboundInstances.get(o2));
                }

            };


            List<ILiteral> sortingList=new ArrayList<>(list);
            sortingList.sort(variablesSizeComparator.thenComparing(dataSizeComparator));


            System.out.println( Joiner.on(", ").join(list)+"\t"+Joiner.on(", ").join(list.stream().map(k->upperboundInstances.get(k)).collect(Collectors.toList())));
            return list.get(0);
        }



    }
}
