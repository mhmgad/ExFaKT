package text;

import de.mpii.factspotting.ISpottedEvidence;
import org.deri.iris.api.basics.ILiteral;

/**
 * Created by gadelrab on 3/31/17.
 */
public interface ITextConnector {


    public ITextResult queryText(ILiteral queryLiteral);

}
