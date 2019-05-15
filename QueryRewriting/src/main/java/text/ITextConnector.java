package text;

import de.mpii.factspotting.ISpottedEvidence;
import org.deri.iris.api.basics.ILiteral;

/**
 * Created by gadelrab on 3/31/17.
 */
public interface ITextConnector {


    FactSpottingResult queryText(ILiteral queryLiteral) throws Exception;

    double getWeight();

    void setWeight(double weight);

}
