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
    private static final String INPUT_TYPE = "inputType";
    private static Configuration instance;
    private InputType inputType;


    enum InputType{RDF, IRIS}

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

    public Configuration(){
        super();
        super.evaluationStrategyFactory=new ExtendedSLDNFEvaluationStrategyFactory();
    }

    public static Configuration fromFile(String filename, boolean inResources) {

        Configuration conf = new Configuration();
        Properties prop = new Properties();
        InputStream input = null;

        try {
            // configuration file loaded in resoruces
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
            conf.setInputType(InputType.valueOf(prop.getProperty(INPUT_TYPE,InputType.IRIS.toString())));

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
            instance= Configuration.fromFile("fact_checking_rewriting.properities");

        return instance;
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

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    public InputType getInputType() {
        return inputType;
    }

    public FactSpotterFactory.SpottingMethod getSpottingMethod() {
        return spottingMethod;
    }

    public List<String> getQueiesFiles() {
        return queiesFiles;
    }
}
