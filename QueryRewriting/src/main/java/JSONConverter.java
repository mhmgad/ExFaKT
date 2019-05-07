import checker.ExplanationsExtractor;
import com.google.gson.Gson;
import config.Configuration;
import de.mpii.datastructures.BinaryFact;
import extendedsldnf.datastructure.Explanation;
import extendedsldnf.datastructure.IQueryExplanations;
import extendedsldnf.datastructure.QueryExplanations;
import mpi.tools.javatools.util.FileUtils;
//import text.ITextResult;
import output.listner.OutputListener;
import output.writers.AbstractOutputChannel;
import output.writers.FileOutputWriter;
import utils.json.CustomGson;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts from JSON to any other supported format
 */
public class JSONConverter {



    public static void main(String[] args) {



    String inputFile=args[0];
    AbstractOutputChannel.OutputFormat outputType=AbstractOutputChannel.OutputFormat.valueOf(args[1].toUpperCase());



    String outputFile=inputFile.replaceAll(".json","");

        OutputListener<QueryExplanations> outputListener=new OutputListener<>();
        outputListener.registerWriter(new FileOutputWriter(outputFile,outputType));

        System.out.println(inputFile);

//
        Gson gson=new CustomGson().getGson();
//
        try {

            String data=FileUtils.getFileContent(FileUtils.getBufferedUTF8Reader(inputFile));
//            BufferedWriter bw=FileUtils.getBufferedUTF8Writer(outputFile);


            data=data.replaceAll("\\}[\\s]*\\{","\\} SPLIT_MARK \\{");
            List<String> records= Arrays.asList(data.split("SPLIT_MARK"));

            List<QueryExplanations> queryExplanationsList=records.stream().map(rec->gson.fromJson(rec.trim(), QueryExplanations.class)).collect(Collectors.toList());


            for (QueryExplanations queryExp:queryExplanationsList) {

                outputListener.out(queryExp);

            }

            outputListener.close();
            System.out.println("input Size="+records.size());
            System.out.println("output Size="+queryExplanationsList.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
