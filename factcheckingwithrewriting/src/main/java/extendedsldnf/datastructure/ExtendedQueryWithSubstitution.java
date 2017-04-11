package extendedsldnf.datastructure;

import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.evaluation.topdown.QueryWithSubstitution;

import java.util.Map;

/**
 * Created by gadelrab on 3/6/17.
 */
public class ExtendedQueryWithSubstitution extends QueryWithSubstitution{

    public Map<IVariable, BindingSource> getSubstitutionSources() {
        return substitutionSources;
    }

    public enum ExpansionMethod {BUILT_IN,FACT,TEXT,RULES,ORG,ENTITIES_COMB}
    public enum BindingSource{FACT,TEXT,ENTITIES_COMB, IN}

    IQuery referenceQuery;

    Map<IVariable, BindingSource> substitutionSources;

    public ExpansionMethod getSource() {
        return source;
    }

    public void setSource(ExpansionMethod source) {
        this.source = source;
    }

    public ExpansionMethod source;

    public ExtendedQueryWithSubstitution(IQuery query, Map<IVariable, ITerm> substitution, ExpansionMethod source, Map<IVariable, BindingSource> substitutionSources) {
        this(query,substitution,source,query,substitutionSources);

    }

    public ExtendedQueryWithSubstitution(IQuery query, Map<IVariable, ITerm> substitution, ExpansionMethod source, IQuery referenceQuery, Map<IVariable, BindingSource> substitutionSources) {
        super(query,substitution);
        this.source=source;
        this.referenceQuery=referenceQuery;
        this.substitutionSources=substitutionSources;

    }



    public String toString() {
        return super.toString() + " | " + substitutionSources + " | " + source.toString();
    }

}
