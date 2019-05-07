package checker;

import de.mpii.datastructures.Fact;
import extendedsldnf.datastructure.IQueryExplanations;
import extendedsldnf.datastructure.InputQuery;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;

import java.util.Collection;
import java.util.Set;

/**
 * Created by gadelrab on 3/22/17.
 */
//public interface IDeepChecker<T extends IQuery> {
//
//
//    public IQueryExplanations check(T queryFact);
//
//    public IQueryExplanations check(Fact fact);
//
//    public IQueryExplanations check(T queryFact,Collection<IRule> ruleSet);
//
//    public IQueryExplanations check(Fact fact,Collection<IRule> ruleSet);
//}

public interface IDeepChecker {


    public IQueryExplanations check(InputQuery queryFact);

//    public IQueryExplanations check(Fact fact);

    public IQueryExplanations check(InputQuery queryFact,Collection<IRule> ruleSet);

//    public IQueryExplanations check(Fact fact,Collection<IRule> ruleSet);
}