package extendedsldnf.datastructure;

import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.topdown.QueryWithSubstitution;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gadelrab on 3/6/17.
 */
public class ExtendedQueryWithSubstitution extends QueryWithSubstitution{

    public Map<IVariable, BindingSource> getSubstitutionSources() {
        return substitutionSources;
    }

    public enum Source{BUILT_IN,FACT,TEXT,RULES,ORG}
    public enum BindingSource{FACT,TEXT,IN}

    IQuery referenceQuery;

    Map<IVariable, BindingSource> substitutionSources;

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Source source;

    public ExtendedQueryWithSubstitution(IQuery query, Map<IVariable, ITerm> substitution,Source source,Map<IVariable, BindingSource> substitutionSources) {
        this(query,substitution,source,query,substitutionSources);

    }

    public ExtendedQueryWithSubstitution(IQuery query, Map<IVariable, ITerm> substitution, Source source, IQuery referenceQuery, Map<IVariable, BindingSource> substitutionSources) {
        super(query,substitution);
        this.source=source;
        this.referenceQuery=referenceQuery;
        this.substitutionSources=substitutionSources;

    }



    public String toString() {
        return super.toString() + " | " + substitutionSources + " | " + source.toString();
    }

}
