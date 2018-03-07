package extras.data.process.wikidata;

import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonObjectFactory;
import output.listner.OutputListener;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Extracts labels from wikidata dump
 */
public class LabelsExtractor implements EntityDocumentProcessor {

    /**
     *
     */


//    final JsonSerializer jsonSerializer;

    /**
     * Object used to make simplified copies of Wikidata documents for
     * re-serialization in JSON.
     */
    final DatamodelConverter datamodelConverter;
    private final OutputListener outputListener;
    private final String inputDump;
//    private final BufferedWriter outputWriter;

    public LabelsExtractor(String inputDump, OutputListener outputListener) throws IOException {


        this.inputDump=inputDump;
    this.outputListener=outputListener;
    this.datamodelConverter = new DatamodelConverter(
            new JacksonObjectFactory());
    // Do not copy references at all:
    this.datamodelConverter.setOptionDeepCopyReferences(false);
    // Only copy English labels, descriptions, and aliases:
        Set<String> languages = Collections.singleton("en*");

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





//    public static void main(String[] args) throws IOException {
//        Helper.configureLogging();
//
//        if(args.length<2)
//        {
//            System.out.println("usage:\nsh extract_wiki_label.sh <data_file> <outputFile.gz>");
//            System.exit(0);
//        }
//
//        String outputFile=args[1];
//        String inputFile=args[0];
//
//        LabelsExtractor jsonSerializationProcessor = new LabelsExtractor(outputFile);
//
//        Helper
//                .processEntitiesFromWikidataLocalDump(jsonSerializationProcessor,inputFile);
//        jsonSerializationProcessor.close();
//    }


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
        String fullId=propertyDocument.getEntityId().getSiteIri();
        String label= labels.get("en").getText();
        String readableId=id+"_"+label;
        Set<Paraphrase> paraphrases=new HashSet<>();

        paraphrases.add(new Paraphrase(id,fullId,readableId,"label",label));

        if(propertyDocument.getAliases()!=null&& propertyDocument.getAliases().size()>0 && propertyDocument.getAliases().containsKey("en")&&propertyDocument.getAliases().get("en")!=null) {

            for (MonolingualTextValue alias : propertyDocument.getAliases().get("en")) {
                paraphrases.add(new Paraphrase(id, fullId, readableId, "alias", alias.getText()));
            }
        }

        try {
            output(paraphrases);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void output(Set<Paraphrase> paraphrases) throws IOException {

    outputListener.out(paraphrases);

    }

    /**
     * Closes the output. Should be called after the JSON serialization was
     * finished.
     *
     * @throws IOException
     *             if there was a problem closing the output
     */
    public void close()  {
        outputListener.close();
    }

    public void run() {
        Helper.processEntitiesFromWikidataLocalDump(this,inputDump);
        this.close();
    }
}
