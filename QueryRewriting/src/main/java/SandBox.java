import checker.ExplanationsExtractor;
import com.google.gson.Gson;
import config.Configuration;
import de.mpii.datastructures.BinaryFact;
import extendedsldnf.datastructure.IQueryExplanations;
import extendedsldnf.datastructure.QueryExplanations;
import mpi.tools.javatools.util.FileUtils;
//import text.ITextResult;
import utils.json.CustomGson;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SandBox {


    public static void main(String[] args) {



        Configuration.setConfigurationFile("/home/gadelrab/ideaProjects/RuleBasedFactChecking/QueryRewriting/src/main/resources/simple.properties");
        ExplanationsExtractor explanationsExtractor = ExplanationsExtractor.getInstance();

        IQueryExplanations res = explanationsExtractor.check(new BinaryFact("<Albert_Einstein>", "<wasBornIn>", "<Germany>"));


        Gson gson=new CustomGson().getGson();
        try {
            BufferedWriter outFile = FileUtils.getBufferedUTF8Writer("out.json");
            gson.toJson(res, outFile);
            outFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        IQueryExplanations res2=null;
        try {
             res2=gson.fromJson(FileUtils.getBufferedUTF8Reader("out.json"), QueryExplanations.class);

            System.out.println(res2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            BufferedWriter outFile = FileUtils.getBufferedUTF8Writer("out2.json");
            gson.toJson(res2, outFile);
            outFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




//        System.out.println(res);
    }
}
