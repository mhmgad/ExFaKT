package text;

import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;

import java.util.List;
import java.util.Map;

/**
 * Created by gadelrab on 3/31/17.
 */
public interface ITextResult extends  IWeightedObject{




    public boolean found();

    public List<Map<IVariable, ITerm>> getVaribalesMappings();

    public String readable();

}
