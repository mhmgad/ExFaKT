package config;

import de.mpii.factspotting.FactSpotterFactory;
import extendedsldnf.ExtendedSLDNFEvaluationStrategyFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;



/**
 * This class extends the configuration of IRIS
 *
 * Created by gadelrab on 3/22/17.
 */
public class Configuration extends org.deri.iris.Configuration {


    private static final String SPOTTING_METHOD = "factSpotting.method";
    private static final String RULES_FILES = "rewriting.ruleFiles";
    private static final String FACTS_FILES = "factsSource.factsFiles";
    private static final String QUERIES_FILES = "queriesFile";
    private static final String FACTS_FORMAT = "factsFormat";
    private static final String PARTIAL_BINDING_TYPE = "partialBindingType";





    private static Configuration instance;
    private static String configurationFile="fact_checking_rewriting.properties";
    private static boolean confFileAlreadySet= false;


    public enum PartialBindingType {GREEDY,TEXT,NONE}

    public enum FactsFormat {RDF, IRIS;}

    //public enum TextCheckingMode{GROUNDED, PARTIAL, NONE, KG_BIND;}
    /**
     * FactSpotting Method
     */
    FactSpotterFactory.SpottingMethod spottingMethod= FactSpotterFactory.SpottingMethod.NONE;

    /**
     * Files containing rules
     */
    private List<String> rulesFiles;

    /**
     * Files containing facts
     */
    private List<String> factsFiles;
    /**
     * Files containing queries
     */
    private List<String> queiesFiles;

    /**
     * The format of the input fact, either RDF triples or IRIS facts
     */
    private FactsFormat factsFormat;

    /**
     * The allowed facts to check, either fully grounded or partially
     */
    private PartialBindingType partialBindingType;



    public Configuration(){
        super();
        super.evaluationStrategyFactory=new ExtendedSLDNFEvaluationStrategyFactory();
    }

    public static Configuration fromFile(String filename, boolean inResources) {

        Configuration conf = new Configuration();
        Properties prop = new Properties();
        InputStream input = null;

        try {
            // configuration file loaded in resources
            if (inResources) {
                input = de.mpii.factspotting.config.Configuration.class.getClassLoader().getResourceAsStream(filename);

            } else {
                // configuration file form user
                input = new FileInputStream(filename);
            }
            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                return conf;

            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value
            conf.setSpottingMethod(FactSpotterFactory.SpottingMethod.valueOf(prop.getProperty(SPOTTING_METHOD, "NONE")));
            conf.setRulesFiles(Arrays.asList(prop.getProperty(RULES_FILES, "").split(",")));
            conf.setFactsFiles(Arrays.asList(prop.getProperty(FACTS_FILES, "").split(",")));
            conf.setQueiesFiles(Arrays.asList(prop.getProperty(QUERIES_FILES, "").split(",")));
            conf.setFactsFormat(FactsFormat.valueOf(prop.getProperty(FACTS_FORMAT, FactsFormat.IRIS.toString())));
            conf.setPartialBindingType(PartialBindingType.valueOf(prop.getProperty(PARTIAL_BINDING_TYPE,"NONE")));


//                System.out.println(conf);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return conf;

    }

    public static Configuration fromFile(String filename){
        return fromFile(filename,true);
    }

    public synchronized static Configuration getInstance() {
        if(instance==null)
            instance= Configuration.fromFile(configurationFile);

        return instance;
    }




    public synchronized static boolean setConfigurationFile(String file){
        if(file==null||confFileAlreadySet)
            return false;
        else
        {
            configurationFile=file;
            confFileAlreadySet=true;
            return true;
        }
    }


    public void setSpottingMethod(FactSpotterFactory.SpottingMethod spottingMethod) {
        this.spottingMethod = spottingMethod;
    }

    public void setRulesFiles(List<String> rulesFiles) {
        this.rulesFiles = rulesFiles;
    }

    public List<String> getRulesFiles() {
        return rulesFiles;
    }

    public List<String> getFactsFiles() {
        return factsFiles;
    }

    public void setFactsFiles(List<String> factsFiles) {
        this.factsFiles = factsFiles;
    }

    public void setQueiesFiles(List<String> queiesFiles) {
        this.queiesFiles = queiesFiles;
    }

    public void setFactsFormat(FactsFormat factsFormat) {
        this.factsFormat = factsFormat;
    }

    public FactsFormat getFactsFormat() {
        return factsFormat;
    }

    public FactSpotterFactory.SpottingMethod getSpottingMethod() {
        return spottingMethod;
    }

    public List<String> getQueiesFiles() {
        return queiesFiles;
    }

    public void setPartialBindingType(PartialBindingType partialBindingType) {
        this.partialBindingType = partialBindingType;
    }

    public PartialBindingType getPartialBindingType() {
        return partialBindingType;
    }



}
