package de.mpii.exfakt.dictionary.builder.extractors;

//import de.mpii.exfakt.dictionary.builder.extractors.wikidata.Helper;

import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonObjectFactory;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;
import output.listner.OutputListener;
import web.data.Paraphrase;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Extracts labels from wikidata dump
 */
public class LabelsExtractorWikidata extends LabelsExtractor implements EntityDocumentProcessor {

    /**
     *
     */


//    final JsonSerializer jsonSerializer;

    /**
     * Object used to make simplified copies of Wikidata documents for
     * re-serialization in JSON.
     */
    final DatamodelConverter datamodelConverter;

//    private final BufferedWriter outputWriter;

    public LabelsExtractorWikidata(String inputDump, OutputListener outputListener, String languageCode) throws IOException {
        super(inputDump,outputListener,languageCode);
    this.datamodelConverter = new DatamodelConverter(
            new JacksonObjectFactory());
    // Do not copy references at all:
    this.datamodelConverter.setOptionDeepCopyReferences(false);
    // Only copy English labels, descriptions, and aliases:
        Set<String> languages = Collections.singleton(languageCode);


    this.datamodelConverter.setOptionLanguageFilter(languages);
    // Only copy statements of some properties:
    Set<PropertyIdValue> propertyFilter = new HashSet<>();
//    propertyFilter.add(Datamodel.makeMonolingualTextValue("altLabel").makeWikidataPropertyIdValue("altLabel")); // image
//    propertyFilter.add(Datamodel.makeWikidataPropertyIdValue("label")); // occupation
//    propertyFilter.add(Datamodel.makeWikidataPropertyIdValue("prefLabel")); // birthdate

    this.datamodelConverter.setOptionPropertyFilter(propertyFilter);
    // Do not copy any sitelinks:
    this.datamodelConverter.setOptionSiteLinkFilter(Collections.<String> emptySet());
//         outputWriter=new BufferedWriter(new FileWriter(outputFile));

//    OutputStream outputStream = new GzipCompressorOutputStream(
//            new BufferedOutputStream(
//                    new FileOutputStream(outputFile)));
//    this.jsonSerializer = new JsonSerializer(outputStream);
//
//    this.jsonSerializer.open();

}







    @Override
    public void processItemDocument(ItemDocument itemDocument) {

    }



    @Override
    public void processPropertyDocument(PropertyDocument propertyDocument) {
        System.out.println(propertyDocument.getPropertyId());
//        this.jsonSerializer.processPropertyDocument(this.datamodelConverter
//                .copy(propertyDocument));
        propertyDocument= this.datamodelConverter.copy(propertyDocument);

        Map<String, MonolingualTextValue> labels = propertyDocument.getLabels();
        if(labels.size()>1)
            System.out.println(propertyDocument.getPropertyId()+" "+labels);

        String id=propertyDocument.getEntityId().getId();
        String fullId=propertyDocument.getEntityId().getSiteIri()+id;
        String label= labels.get(getLanguageCode()).getText();
        String readableId=id+"_"+label.replaceAll(" ","_");
        Set<Paraphrase> paraphrases=new HashSet<>();

        paraphrases.add(new Paraphrase(id,fullId,readableId,"label",label, "rdfs:label"));

        if(propertyDocument.getAliases()!=null&& propertyDocument.getAliases().size()>0 && propertyDocument.getAliases().containsKey(getLanguageCode())&&propertyDocument.getAliases().get(getLanguageCode())!=null) {

            for (MonolingualTextValue alias : propertyDocument.getAliases().get(getLanguageCode())) {
                paraphrases.add(new Paraphrase(id, fullId, readableId, "alias", alias.getText(),"alias"));
            }
        }

        try {
            output(paraphrases);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void processEntitiesFromWikidataLocalDump(
            EntityDocumentProcessor entityDocumentProcessor,String dumpFileName) {

        DumpProcessingController dumpProcessingController = new DumpProcessingController(
                "wikidatawiki");

        dumpProcessingController.registerEntityDocumentProcessor(
                entityDocumentProcessor, null, true);
        dumpProcessingController.setOfflineMode(true);
        // Select local file (meta-data will be guessed):
        System.out.println();
        System.out
                .println("Processing a local dump file");
        System.out
                .println("(meta-data like the date is guessed from the file name):");
        MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(dumpFileName);

        System.out.println(mwDumpFile.getDumpContentType()+" "+mwDumpFile.getPath()+" "+mwDumpFile.getDateStamp());


        dumpProcessingController.processDump(mwDumpFile);


    }


    @Override
    public void process() {
        processEntitiesFromWikidataLocalDump(this, getInputFilePath());
    }
}
