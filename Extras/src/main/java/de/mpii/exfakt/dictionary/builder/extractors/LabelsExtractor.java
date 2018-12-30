package de.mpii.exfakt.dictionary.builder.extractors;

import de.mpii.exfakt.dictionary.data.Paraphrase;
import output.listner.OutputListener;

import java.io.IOException;
import java.util.Set;

public abstract class LabelsExtractor {

    public static enum KG{YAGO,WIKI}

    private OutputListener outputListener;
    private String inputFilePath;
    private  String languageCode;

    public LabelsExtractor(String inputFilePath, OutputListener outputListener, String languageCode) {
        this.languageCode=languageCode;
        this.inputFilePath =inputFilePath;
        this.outputListener=outputListener;
    }


    protected void output(Set<Paraphrase> paraphrases) throws IOException {
        outputListener.out(paraphrases);
    }

    protected void output(Paraphrase paraphrase) throws IOException {
        outputListener.out(paraphrase);
    }

    /**
     * Closes the output. Should be called after the JSON serialization was
     * finished.
     *
     * @throws IOException
     *             if there was a problem closing the output
     */
    protected void close()  {
        outputListener.close();
    }


    /**
     * The main entrance method, will call the method responsible for the processing and then close the output listener
     */
    public void run() {
        this.process();
        this.close();
    }

    public OutputListener getOutputListener() {
        return outputListener;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * loops over the facts or Entities to extract paraphrases
     */
    protected abstract void process();

}
