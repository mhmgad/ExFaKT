package extras.data.process.wikidata;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonObjectFactory;
import org.wikidata.wdtk.datamodel.json.jackson.JsonSerializer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Extracts labels from wikidata dump
 */
public class LabelsExtractor implements EntityDocumentProcessor {

    /**
     *
     */


    final JsonSerializer jsonSerializer;

    /**
     * Object used to make simplified copies of Wikidata documents for
     * re-serialization in JSON.
     */
    final DatamodelConverter datamodelConverter;
    private final String outputFile;

    public LabelsExtractor(String outputFile) throws IOException {

    this.outputFile=outputFile;
    this.datamodelConverter = new DatamodelConverter(
            new JacksonObjectFactory());
    // Do not copy references at all:
    this.datamodelConverter.setOptionDeepCopyReferences(false);
    // Only copy English labels, descriptions, and aliases:
    this.datamodelConverter.setOptionLanguageFilter(Collections
            .singleton("en"));
    // Only copy statements of some properties:
//    Set<PropertyIdValue> propertyFilter = new HashSet<>();
//    propertyFilter.add(Datamodel.makeMonolingualTextValue("altLabel").makeWikidataPropertyIdValue("altLabel")); // image
//    propertyFilter.add(Datamodel.makeWikidataPropertyIdValue("label")); // occupation
//    propertyFilter.add(Datamodel.makeWikidataPropertyIdValue("prefLabel")); // birthdate

//    this.datamodelConverter.setOptionPropertyFilter(propertyFilter);
    // Do not copy any sitelinks:
    this.datamodelConverter.setOptionSiteLinkFilter(Collections
            .<String> emptySet());


    OutputStream outputStream = new GzipCompressorOutputStream(
            new BufferedOutputStream(
                    Helper
                            .openExampleFileOuputStream(outputFile)));
    this.jsonSerializer = new JsonSerializer(outputStream);

    this.jsonSerializer.open();

}



    public static void main(String[] args) throws IOException {
        Helper.configureLogging();

        if(args.length<2)
        {
            System.out.println("usage:\nsh extract_wiki_label.sh <data_file> <outputFile.gz>");
            System.exit(0);
        }

        String outputFile=args[1];
        String inputFile=args[0];

        LabelsExtractor jsonSerializationProcessor = new LabelsExtractor(outputFile);

        Helper
                .processEntitiesFromWikidataLocalDump(jsonSerializationProcessor,inputFile);
        jsonSerializationProcessor.close();
    }


    @Override
    public void processItemDocument(ItemDocument itemDocument) {
//        if (includeDocument(itemDocument)) {
//            this.jsonSerializer.processItemDocument(this.datamodelConverter
//                    .copy(itemDocument));
//        }
        //System.out.println(itemDocument.getItemId());
        this.jsonSerializer.processItemDocument(this.datamodelConverter
                    .copy(itemDocument));
    }

//    private boolean includeDocument(ItemDocument itemDocument) {
//
//
//    }

    @Override
    public void processPropertyDocument(PropertyDocument propertyDocument) {
        System.out.println(propertyDocument.getPropertyId());
        this.jsonSerializer.processPropertyDocument(this.datamodelConverter
                .copy(propertyDocument));
    }

    /**
     * Closes the output. Should be called after the JSON serialization was
     * finished.
     *
     * @throws IOException
     *             if there was a problem closing the output
     */
    public void close() throws IOException {
        System.out.println("Serialized "
                + this.jsonSerializer.getEntityDocumentCount()
                + " item documents to JSON file " + outputFile + ".");
        this.jsonSerializer.close();
    }
}
