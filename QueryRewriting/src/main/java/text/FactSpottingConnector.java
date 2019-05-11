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
        this(conf.getSpottingMethod(),conf);


    }

    public FactSpottingConnector(FactSpotterFactory.SpottingMethod spottingMethod, Configuration conf) {
        String spottingConfFile = conf.getSpottingConfFile();
        logger.info("Set spotting config:"+ spottingConfFile);
        de.mpii.factspotting.config.Configuration.setConfigurationFile(spottingConfFile==null? null:spottingConfFile,conf.getSpottingConfFileClassLoader());
        logger.info("Used spotting config:"+ de.mpii.factspotting.config.Configuration.getConfigurationFile());
        spotter=FactSpotterFactory.create(spottingMethod);


    }


    @Override
    public FactSpottingResult queryText(ILiteral queryLiteral) throws Exception {
        logger.debug(queryLiteral.toString());
        //System.out.println("Query: "+queryLiteral.toString());
        Fact f= Converter.toFact(queryLiteral);

        ISpottedEvidence evidence=spotter.spot(f);
        return new FactSpottingResult(evidence,queryLiteral);



    }


}
