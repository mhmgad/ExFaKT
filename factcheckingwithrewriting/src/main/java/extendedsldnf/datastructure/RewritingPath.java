package extendedsldnf.datastructure;

import com.google.api.client.util.Sets;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/8/17.
 */

public class RewritingPath {

    List<ILiteral> literals=new LinkedList<>();
    List<ExtendedQueryWithSubstitution.ExpansionMethod> sources=new LinkedList<>();


    private Map<IVariable, ITerm> substitutions;
    private Map<IVariable, ExtendedQueryWithSubstitution.BindingSource> substitutionsSources;

    public RewritingPath() {
        this(new LinkedList<ILiteral>(),new LinkedList<ExtendedQueryWithSubstitution.ExpansionMethod>(),new HashMap<IVariable,ITerm>(),new HashMap<IVariable,ExtendedQueryWithSubstitution.BindingSource>());

    }

    public RewritingPath(List<ILiteral> literals, List<ExtendedQueryWithSubstitution.ExpansionMethod> sources, HashMap<IVariable, ITerm> substitutions, HashMap<IVariable, ExtendedQueryWithSubstitution.BindingSource> subtitutionsSources) {
        this.literals = literals;
        this.sources = sources;
        this.substitutions=substitutions;
        this.substitutionsSources=subtitutionsSources;
    }

    public void add(ILiteral selectedLiteral, ExtendedQueryWithSubstitution.ExpansionMethod source) {
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
        return (!this.sources.isEmpty())&& this.sources.get(0)== ExtendedQueryWithSubstitution.ExpansionMethod.RULES;
    }

    public void setSubstitutions(Map<IVariable,ITerm> substitutions) {
        this.substitutions = substitutions;
    }

    public void setSubstitutionsSources(Map<IVariable,ExtendedQueryWithSubstitution.BindingSource> substitutionsSources) {
        this.substitutionsSources = substitutionsSources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RewritingPath)) return false;
        RewritingPath that = (RewritingPath) o;
        return Objects.equals(literals, that.literals) &&
                Objects.equals(substitutions, that.substitutions); //&&
//         Objects.equals(substitutionsSources, that.substitutionsSources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literals, substitutions);//, substitutionsSources);
    }
}
