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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JSON2Readable{


    public static void main(String[] args) {

    String inputFile=args[0];
    String outputFile=inputFile.replaceAll(".json",".txt");

//        Configuration.setConfigurationFile("/home/gadelrab/ideaProjects/RuleBasedFactChecking/QueryRewriting/src/main/resources/simple.properties");
//        ExplanationsExtractor explanationsExtractor = ExplanationsExtractor.getInstance();
//
//        IQueryExplanations res = explanationsExtractor.check(new BinaryFact("<Albert_Einstein>", "<wasBornIn>", "<Germany>"));


        Gson gson=new CustomGson().getGson();
//        try {
//            BufferedWriter outFile = FileUtils.getBufferedUTF8Writer("out.json");
//            gson.toJson(res, outFile);
//            outFile.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        IQueryExplanations res2=null;
        try {

            String data=FileUtils.getFileContent(FileUtils.getBufferedUTF8Reader(inputFile));
            BufferedWriter bw=FileUtils.getBufferedUTF8Writer(outputFile);


            data=data.replaceAll("\\}[\\s]*\\{","\\} SPLIT_MARK \\{");
            System.out.println(data);
            List<String> records= Arrays.asList(data.split("SPLIT_MARK"));

            List<QueryExplanations> queryExplanationsList=records.stream().map(rec->gson.fromJson(rec.trim(), QueryExplanations.class)).collect(Collectors.toList());


            for (QueryExplanations queryExp:queryExplanationsList) {
//                System.out.println(queryExp.getReadableString());
                bw.write(queryExp.getReadableString()+"\n");

            }

            bw.close();
            System.out.println(records.size());
//            System.out.println(res2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            BufferedWriter outFile = FileUtils.getBufferedUTF8Writer("out2.json");
//            gson.toJson(res2, outFile);
//            outFile.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }




//        System.out.println(res);
    }
}
