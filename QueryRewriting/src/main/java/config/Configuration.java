package config;

import de.mpii.factspotting.FactSpotterFactory;
import extendedsldnf.ExtendedSLDNFEvaluationStrategyFactory;
import utils.Enums;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static utils.StringUtils.asList;


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
    private static final String SUSPECTS_FROM_KG = "suspectsFromKG";
    public static final String FACT_SPOTTING_CONF = "factSpotting.config";
    private static final String EVALUATION_METHOD = "evalMethod";
    private static final String MAX_EXPLANATIONS="maxExplanations";
    private static final String MAX_RULE_NESTING="maxRuleNesting";


    private static Configuration instance;
    private static String configurationFile="fact_checking_rewriting.properties";
    private static boolean confFileAlreadySet= false;
    /**
     * indicates whether a file is in JAR or not. once filepath is changed this variable is turned to true.
     */
    private static boolean externalConfFile=false;
    /**
     * Extra configurations
     */
    private Properties extraProperties;
    private String spottingConfFile;
    private Enums.EvalMethod evaluationMethod= Enums.EvalMethod.SLD;
    private int maxExplanations=10;
    private int maxRuleNesting =3;

    public void setExtraProp(Properties extraProp) {
        this.extraProperties = extraProp;
    }

    public boolean isSuspectsFromKG() {
        return suspectsFromKG;
    }

    public void setSuspectsFromKG(boolean suspectsFromKG) {
        this.suspectsFromKG = suspectsFromKG;
    }

    public void setSpottingConfFile(String spottingConfFile) {
        this.spottingConfFile = spottingConfFile;
    }

    public String getSpottingConfFile() {
        return spottingConfFile;
    }

    public void setEvaluationMethod(Enums.EvalMethod evaluationMethod) {
        this.evaluationMethod = evaluationMethod;
    }

    public Enums.EvalMethod getEvaluationMethod() {
        return evaluationMethod;
    }

    public int getMaxExplanations() {
        return maxExplanations;
    }

    public int getMaxRuleNesting() {
        return maxRuleNesting;
    }

    public void setMaxExplanations(int maxExplanations) {
        this.maxExplanations = maxExplanations;
    }

    public void setMaxRuleNesting(int maxRuleNesting) {
        this.maxRuleNesting = maxRuleNesting;
    }


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
    private List<String> queriesFiles;

    /**
     * The format of the input fact, either RDF triples or IRIS facts
     */
    private FactsFormat factsFormat;

    /**
     * The allowed facts to check, either fully grounded or partially
     */
    private PartialBindingType partialBindingType;

    /**
     * suspected in the KG so remove them from KG
     */
    private boolean suspectsFromKG=false;



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
            conf.setRulesFiles(asList(prop.getProperty(RULES_FILES, "")));
            conf.setFactsFiles(asList(prop.getProperty(FACTS_FILES, "")));
            conf.setQueriesFiles(asList(prop.getProperty(QUERIES_FILES, "")));
            conf.setFactsFormat(FactsFormat.valueOf(prop.getProperty(FACTS_FORMAT, FactsFormat.IRIS.toString())));
            conf.setPartialBindingType(PartialBindingType.valueOf(prop.getProperty(PARTIAL_BINDING_TYPE, "NONE")));
            conf.setSuspectsFromKG(Boolean.parseBoolean(prop.getProperty(SUSPECTS_FROM_KG, "false")));
            conf.setSpottingConfFile(prop.getProperty(FACT_SPOTTING_CONF, null));
            conf.setEvaluationMethod(Enums.EvalMethod.valueOf(prop.getProperty(EVALUATION_METHOD, Enums.EvalMethod.SLD.name())));
            conf.setMaxRuleNesting(Integer.valueOf(prop.getProperty(MAX_RULE_NESTING, "" + conf.getMaxRuleNesting())));
            conf.setMaxExplanations(Integer.valueOf(prop.getProperty(MAX_EXPLANATIONS, "" + conf.getMaxExplanations())));
            conf.setExtraProp(prop);


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
        return fromFile(filename,!externalConfFile);
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
            externalConfFile=true;
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

    public void setQueriesFiles(List<String> queriesFiles) {
        this.queriesFiles = queriesFiles;
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

    public List<String> getQueriesFiles() {
        return queriesFiles;
    }

    public void setPartialBindingType(PartialBindingType partialBindingType) {
        this.partialBindingType = partialBindingType;
    }

    public PartialBindingType getPartialBindingType() {
        return partialBindingType;
    }

    public boolean hasProperty(String prop){
        return extraProperties.contains(prop);
    }

    public Object getProperty(String prop){
        return extraProperties.getProperty(prop,null);
    }

    public static String getConfigurationFile() {
        return configurationFile;
    }


    @Override
    public String toString() {
        return "Configuration{" +'\n'+
                "extraProperties=" + extraProperties +'\n'+
                ", spottingConfFile='" + spottingConfFile + '\'' +'\n'+
                ", evaluationMethod=" + evaluationMethod +'\n'+
                ", maxExplanations=" + maxExplanations +'\n'+
                ", maxRuleNesting=" + maxRuleNesting +'\n'+
                ", spottingMethod=" + spottingMethod +'\n'+
                ", rulesFiles=" + rulesFiles +'\n'+
                ", factsFiles=" + factsFiles +'\n'+
                ", queriesFiles=" + queriesFiles +'\n'+
                ", factsFormat=" + factsFormat +'\n'+
                ", partialBindingType=" + partialBindingType +'\n'+
                ", suspectsFromKG=" + suspectsFromKG +'\n'+
                '}';
    }
}
