package extendedsldnf.datastructure;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/8/17.
 */

public class RewritingPath {

    List<ILiteral> literals=new LinkedList<>();
    List<ExtendedQueryWithSubstitution.Source> sources=new LinkedList<>();


    private Map<IVariable, ITerm> substitutions;
    private Map<IVariable, ExtendedQueryWithSubstitution.BindingSource> substitutionsSources;

    public RewritingPath() {
        this(new LinkedList<ILiteral>(),new LinkedList<ExtendedQueryWithSubstitution.Source>(),new HashMap<IVariable,ITerm>(),new HashMap<IVariable,ExtendedQueryWithSubstitution.BindingSource>());

    }

    public RewritingPath(List<ILiteral> literals, List<ExtendedQueryWithSubstitution.Source> sources, HashMap<IVariable, ITerm> substitutions, HashMap<IVariable, ExtendedQueryWithSubstitution.BindingSource> subtitutionsSources) {
        this.literals = literals;
        this.sources = sources;
        this.substitutions=substitutions;
        this.substitutionsSources=subtitutionsSources;
    }

    public void add(ILiteral selectedLiteral, ExtendedQueryWithSubstitution.Source source) {
        literals.add(0,selectedLiteral);
        sources.add(0,source);
    }

    @Override
    public String toString() {

        StringBuilder sb=new StringBuilder("RewritingPath{ literals:[");


        for (int i = 0; i < literals.size(); i++) {
            sb.append(literals.get(i) + "<" + sources.get(i) + ">");
            if(i!=literals.size()-1)
                sb.append(", ");
        }
        sb.append("], sources:");
        List<String> subSourcesRep=substitutions.keySet().stream().map(k-> '('+k.toString()+": "+substitutions.get(k).toString()+", "+substitutionsSources.get(k).toString()+')').collect(Collectors.toList());
        sb.append(subSourcesRep.toString());
        sb.append('}');
        return sb.toString();
    }

    public boolean isResolvedByRule() {
        return (!this.sources.isEmpty())&& this.sources.get(0)== ExtendedQueryWithSubstitution.Source.RULES;
    }

    public void setSubstitutions(Map<IVariable,ITerm> substitutions) {
        this.substitutions = substitutions;
    }

    public void setSubstitutionsSources(Map<IVariable,ExtendedQueryWithSubstitution.BindingSource> substitutionsSources) {
        this.substitutionsSources = substitutionsSources;
    }


}
