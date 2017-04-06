package extendedsldnf.rules;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;

/**
 * Created by gadelrab on 4/5/17.
 */
public class SimpleRuleSelector implements IRulesSelector{

    @Override
    public IRule getNextRule(ILiteral literal) {
        return null;
    }
}
