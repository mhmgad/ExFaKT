package de.mpii.exfakt.dictionary.builder.extractors;

import basics.Fact;
import basics.FactComponent;
import basics.FactSource;
import de.mpii.exfakt.dictionary.data.Paraphrase;
import output.listner.OutputListener;

public class LabelsExtractorYago extends LabelsExtractor {



    public LabelsExtractorYago(String labelsFilePath, OutputListener outputListener, String language) {
        super(labelsFilePath,outputListener,language);
    }




    @Override
    protected void process() {
        try {

            for (Fact f : FactSource.from(getInputFilePath())){

                if(!FactComponent.getLanguageOfString(f.getObject()).equals(getLanguageCode()))
                    continue;

                String id=f.getSubject();
                String fullId=FactComponent.wikipediaURL(f.getSubject());
                String label= FactComponent.getString(f.getObject());
                String readableId=FactComponent.stripBrackets(f.getSubject());
                String predicate=f.getRelation();

                String type=(predicate.equals("skos:prefLabel"))? "label":"alis";
                Paraphrase pr=new Paraphrase( id,  fullId,  readableId, type,  label,predicate);
                output(pr);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}