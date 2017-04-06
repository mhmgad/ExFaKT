package text;

import com.google.common.collect.Sets;
import de.mpii.factspotting.ISpottedEvidence;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.terms.TermFactory;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by gadelrab on 3/31/17.
 */
public class FactSpottingResult implements ITextResult{



    ILiteral query;
    ISpottedEvidence evidence;

    public FactSpottingResult(ISpottedEvidence evidence,ILiteral query) {
        this.evidence = evidence;
        this.query=query;
    }

    @Override
    public boolean found() {
        return evidence.isSupporting();
    }

    @Override
    public List<Map<IVariable, ITerm>> getVaribalesMappings() {
        List<Map<IVariable, ITerm>> variableMappings=new LinkedList<>();

        // No variables at all .. so empty mapping
        if(query.getAtom().isGround()){
            variableMappings.add(new HashMap<>());
        }

        List<IVariable> variables = new ArrayList<>(query.getAtom().getTuple().getVariables());

        // Only one variable is missing.
        // TODO Distribute constants over vars
        if(variables.size()>0) {
            List<String> entities = evidence.getEntities();
            variableMappings=createMaps(variables,entities);
        }

        return variableMappings;
    }

    private List<Map<IVariable, ITerm>>  createMaps(List<IVariable> variables, List<String> entities) {
        List<Map<IVariable, ITerm>> variableMappings=new LinkedList<>();
        Set<String> entitiesSet=new HashSet<>(entities);

        // sets to generate combinations
        List<Set<String>> sets= new LinkedList<>();
        for (int i = 0; i < variables.size(); i++) {
            sets.add(entitiesSet);
        }

        Set<List<String>> combinations = Sets.cartesianProduct(sets);
        ITermFactory termFactory = TermFactory.getInstance();
        for(List<String> comb:combinations) {
            Map<IVariable, ITerm> variableMapping = new HashMap<>();
            IntStream.range(0, variables.size()).forEach(i -> variableMapping.put(variables.get(i), termFactory.createString(comb.get(i))));
        }
            return variableMappings;
    }

    @Override
    public double getWeight() {
        return 0;
    }

    @Override
    public double getCost() {
        return 0;
    }
}
