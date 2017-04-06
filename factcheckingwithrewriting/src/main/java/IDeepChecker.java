import de.mpii.datastructures.Fact;
import extendedsldnf.datastructure.IExplanation;
import org.deri.iris.api.basics.IQuery;

/**
 * Created by gadelrab on 3/22/17.
 */
public interface IDeepChecker<T extends IQuery> {


    public IExplanation check(T queryFact);

    IExplanation check(Fact fact);
}
