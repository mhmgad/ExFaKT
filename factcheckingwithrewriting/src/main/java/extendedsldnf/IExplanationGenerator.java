package extendedsldnf;

import extendedsldnf.datastructure.IQueryExplanations;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IQuery;

/**
 * Created by gadelrab on 4/5/17.
 */
public interface IExplanationGenerator {

    /**
     * generate rewriting paths
     * @param query
     * @return
     */
    public IQueryExplanations getExplanation(IQuery query) throws EvaluationException;
}
