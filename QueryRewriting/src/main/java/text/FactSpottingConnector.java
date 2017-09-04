package text;

import config.Configuration;
import de.mpii.datastructures.Fact;
import de.mpii.datastructures.IFact;
import de.mpii.factspotting.FactSpotterFactory;
import de.mpii.factspotting.IFactSpotter;
import de.mpii.factspotting.ISpottedEvidence;
import org.deri.iris.api.basics.ILiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Converter;

/**
 * Created by gadelrab on 3/31/17.
 */
public class FactSpottingConnector<T extends IFact> implements ITextConnector{


    private IFactSpotter spotter;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public FactSpottingConnector(Configuration conf) {
        String spottingConfFile = conf.getSpottingConfFile();
        de.mpii.factspotting.config.Configuration.setConfigurationFile(spottingConfFile==null? null:spottingConfFile);
        spotter=FactSpotterFactory.create(conf.getSpottingMethod());

    }


    @Override
    public ITextResult queryText(ILiteral queryLiteral) throws Exception {
        logger.debug(queryLiteral.toString());
        //System.out.println("Query: "+queryLiteral.toString());
        Fact f= Converter.toFact(queryLiteral);

        ISpottedEvidence evidence=spotter.spot(f);
        return new FactSpottingResult(evidence,queryLiteral);



    }


}
