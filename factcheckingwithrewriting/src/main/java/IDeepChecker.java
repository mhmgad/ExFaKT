import de.mpii.datastructures.Fact;
import extendedsldnf.datastructure.IQueryExplanations;
import org.deri.iris.api.basics.IQuery;

/**
 * Created by gadelrab on 3/22/17.
 */
public interface IDeepChecker<T extends IQuery> {


    public IQueryExplanations check(T queryFact);

    IQueryExplanations check(Fact fact);
}
