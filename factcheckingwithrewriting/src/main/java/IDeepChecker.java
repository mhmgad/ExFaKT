import de.mpii.datastructures.Fact;
import de.mpii.datastructures.IFact;
import extendedsldnf.datastructure.IExplaination;
import org.deri.iris.api.basics.IQuery;

/**
 * Created by gadelrab on 3/22/17.
 */
public interface IDeepChecker<T extends IQuery> {


    public IExplaination check(T queryFact);

    IExplaination check(Fact fact);
}
