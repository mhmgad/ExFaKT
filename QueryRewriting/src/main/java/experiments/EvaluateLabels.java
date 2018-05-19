package experiments;

import com.google.common.base.Joiner;
import datastructure.CorrectnessInfo;
import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Fact;
import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import utils.Converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EvaluateLabels {


    static class Group{
        String key;
        List<Integer> sortedLabels;
        double accuracy;
        List<Double> scores;

        public double getAccuracy() {
            return accuracy;
        }

        public Group(String key, List<Integer> sortedLabels,List<Double> scores, double accuracy) {
            this.key = key;
            this.sortedLabels = sortedLabels;
            this.scores=scores;
            this.accuracy = accuracy;
        }

        public String toString(){
            return key+"\t"+accuracy+"\t"+sortedLabels+"\t"+scores;
        }




    }


static class Record {

    private String group;
    private double score;
    private String key;



    public Record(String key, double score) {
        this.key = key;
        this.score = score;
        String parts[]=key.split("\t");
        this.group= Joiner.on("\t").join(Arrays.asList(parts[0],parts[1]));
    }

    public String getGroup() {

        return group;
    }


    public double getScore() {
        return score;
    }

    public String key() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record)) return false;
        Record hasGroups = (Record) o;
        return Objects.equals(key, hasGroups.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}

    /**
     * Average accuracy through groups as described in Ndapa's paper
     * TODO check if accuracy is averaged
     *
     * @param
     * @return
     */
    public static List<Group> evaluateRanking( Map<Record, Integer> data) {

        List<Group> groups=new ArrayList<>();
        Map<String, List<Record>> groupedCorrectness = data.keySet().stream().collect(Collectors.groupingBy(Record::getGroup, LinkedHashMap::new,Collectors.toList()));
        //System.out.println("groups :" + groupedCorrectness.keySet() + "\n" + groupedCorrectness.size());


        double accuracyTotal = 0;
        for (String key: groupedCorrectness.keySet()) {
            List<Record>  gInfo = groupedCorrectness.get(key);

            List<Integer> labels = gInfo.stream().sorted(Comparator.comparing(Record::getScore).reversed()).mapToInt(g -> data.get(g)).boxed().collect(Collectors.toList());
            List<Double> scores = gInfo.stream().sorted(Comparator.comparing(Record::getScore).reversed()).mapToDouble(Record::getScore).boxed().collect(Collectors.toList());
//            System.out.print(gInfo.get(0).getGroup() + ": ");
//            System.out.print(labels);

            long trueAlter = labels.stream().filter(l -> l.intValue() == 1).count();
            long falseAlter = labels.stream().filter(l -> l.intValue() == 0).count();
            long groundTruthScore = trueAlter * falseAlter;


            long predictionsCount = 0;
            //(τ(fi)=T:τ(fj)=F) (β(fi) > β(fj))
            for (int i = 0; i < labels.size(); i++) {
                if (labels.get(i) == 1) {
                    long zeroCount = labels.stream().skip(i).filter(l -> l.intValue() == 0).count();
                    predictionsCount += zeroCount;
                }

            }
            double accuracy = (0.0 + predictionsCount) / groundTruthScore;

            groups.add(new Group(key,labels,scores,accuracy));

//            System.out.println("acc: " + predictionsCount + "/" + groundTruthScore + "=" + accuracy);
            accuracyTotal += accuracy;

        }

        return groups;


    }

    public static double accuracy(List<Group> groups){
        return groups.stream().mapToDouble(Group::getAccuracy).average().getAsDouble();
    }


    public static LinkedHashMap<Record,Integer> loadFile(String filePath, int rankingColumn, int gtLabelColumn) throws Exception {
        LinkedHashMap<Record,Integer> data= new LinkedHashMap<>();

        BufferedReader br = FileUtils.getBufferedUTF8Reader(filePath);

        for(String l=br.readLine();l!=null;l=br.readLine()){
            if(l.trim().isEmpty())
                continue;

            String ps[]=l.trim().split("\t");
            if(!ps[rankingColumn].trim().matches("[\\-0-9.]*")) {
                System.out.println("not a number"+ps[rankingColumn]);
                continue;
            }

            Fact f=null;
            if(ps[0].startsWith("?-"))
            {Parser pr=new Parser();
                pr.parse(ps[0]);
                List<IQuery> qs=pr.getQueries();
                f= Converter.toFact(qs.get(0).getLiterals().get(0));
            }
            else
            {
                f=new BinaryFact(ps[0],ps[1],ps[2]);
            }


            data.put(new Record(f.toSearchableString(),Double.parseDouble(ps[rankingColumn])), Integer.valueOf(ps[gtLabelColumn]));


        }
        br.close();
        return data;

    }


    public static void dumpGroups(List<Group> groups,String fileName) throws IOException {
        BufferedWriter bw = FileUtils.getBufferedUTF8Writer(fileName);
        bw.write(Joiner.on("\n").join(groups).toString());
        bw.write("\n");
        bw.write("Avg. Accuracy"+"\t"+accuracy(groups));
        bw.write("\n");

        bw.close();


    }

    public static void main(String[] args) throws Exception {

//        String inputFile="/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/fact_spotting_data/exper2/out/rules_heursitic.out.correctness";

        String inputFile="/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/factchecking_exper/output/out_/rules_text_splitq.out.correctness";

        int rankingScoreColumn=7;
        int labelColumn=15;



        if(args.length>0)
        {
            inputFile=args[2];
            rankingScoreColumn=Integer.valueOf(args[0]);
            labelColumn=Integer.valueOf(args[1]);
        }
        String outputFile=inputFile+"."+rankingScoreColumn+"_"+ CorrectnessInfo.header[rankingScoreColumn]+".acc";

        LinkedHashMap<Record, Integer> data = loadFile(inputFile,rankingScoreColumn,labelColumn);
        List<Group> groups=evaluateRanking(data);
        double accuracy=accuracy(groups);
        System.out.println(Joiner.on("\n").join(groups).toString());
        System.out.println("Accuracy: "+accuracy);
        dumpGroups(groups,outputFile);
    }


}
