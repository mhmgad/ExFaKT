package extendedsldnf;

import extendedsldnf.datastructure.IExplanation;
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
    public IExplanation getExplanation(IQuery query) throws EvaluationException;
}
