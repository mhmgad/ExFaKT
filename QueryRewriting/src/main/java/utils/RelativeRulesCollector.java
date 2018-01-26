package utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.stanford.nlp.util.Sets;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class RelativeRulesCollector {

    public static void main(String[] args) throws IOException {

        String targetPredicate=args[0];
        Multimap<String,IRule> rulesMaps= HashMultimap.create();

        int maxLevel=Integer.valueOf(args[1]);
        List<IRule> rules=DataUtils.loadRules(StringUtils.asList(args[2]));

        for (IRule rule:rules) {
            if(isInvalidRule(rule)){
                continue;
            }

            String predicate=rule.getHead().get(0).getAtom().getPredicate().toString();
            rulesMaps.put(predicate,rule);

        }

        List<IRule> selectedRules=getDependentRules(targetPredicate,maxLevel,rulesMaps);
        System.out.println("Total Rules: "+selectedRules.size());

        BufferedWriter bw=new BufferedWriter(new FileWriter(new File(targetPredicate+"_rules.iris")));
        for (IRule rule:selectedRules) {
            bw.write(rule.toString());
            bw.newLine();

        }
        bw.close();


    }

    private static boolean isInvalidRule(IRule rule) {
        // head exits in body
        ILiteral headLiteral=rule.getHead().get(0);
        for (ILiteral bodyLiteral:rule.getBody()) {
            if(bodyLiteral.equals(headLiteral))
                return true;

        }

        return false;
    }

    private static List<IRule> getDependentRules(String targetPredicate, int maxLevel, Multimap<String,IRule> rulesMaps) {
        SortedSet<IRule> outRules=new <IRule>TreeSortedSet(Comparator.comparing(IRule::toString));
        LinkedHashMap<String,Integer> toProcessPredicates=new LinkedHashMap();
        toProcessPredicates.put(targetPredicate, 1);
        SortedSet<String> donePredicates=new TreeSortedSet<>();
        while(!toProcessPredicates.isEmpty()){
            String predicate=toProcessPredicates.keySet().iterator().next();
            int level=toProcessPredicates.get(predicate);
            toProcessPredicates.remove(predicate);
            donePredicates.add(predicate);

            if(level>maxLevel) {
            continue;
            }

                // Get rules with head == predicate
                Collection<IRule> targetRules = rulesMaps.get(predicate);
                outRules.addAll(targetRules);
                System.out.println(predicate + " " + targetRules.size());

                Set<String> bodyPredicates = getBodyPredicates(targetRules);
                Set<String> newPredicates = Sets.diff(bodyPredicates, donePredicates);
                newPredicates.forEach(p -> toProcessPredicates.put(p, (level + 1)));


//            toProcessPredicates.addAll();

        }

        return new LinkedList<>(outRules);
    }

    private static Set<String> getBodyPredicates(Collection<IRule> targetRules) {
        Set<String> predicates=new HashSet<>();
        for (IRule rule:targetRules ) {
            predicates.addAll( rule.getBody().stream().map(ILiteral::getAtom).map(IAtom::getPredicate).map(Object::toString).collect(Collectors.toSet()));

        }

        return predicates;
    }
}

