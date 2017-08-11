package experiments;

import com.google.common.base.Joiner;
import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.lang.StringUtils;

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

        public double getAccuracy() {
            return accuracy;
        }

        public Group(String key, List<Integer> sortedLabels, double accuracy) {
            this.key = key;
            this.sortedLabels = sortedLabels;
            this.accuracy = accuracy;
        }

        public String toString(){
            return key+"\t"+sortedLabels+"\t"+accuracy;
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

            groups.add(new Group(key,labels,accuracy));

//            System.out.println("acc: " + predictionsCount + "/" + groundTruthScore + "=" + accuracy);
            accuracyTotal += accuracy;

        }

        return groups;


    }

    public static double accuracy(List<Group> groups){
        return groups.stream().mapToDouble(Group::getAccuracy).average().getAsDouble();
    }


    public static LinkedHashMap<Record,Integer> loadFile(String filePath, int rankingColumn, int gtLabelColumn) throws IOException {
        LinkedHashMap<Record,Integer> data= new LinkedHashMap<>();

        BufferedReader br = FileUtils.getBufferedUTF8Reader(filePath);

        for(String l=br.readLine();l!=null;l=br.readLine()){
            if(l.trim().isEmpty())
                continue;

            String ps[]=l.trim().split("\t");
            if(!StringUtils.isNumeric(ps[rankingColumn]))
                continue;

            data.put(new Record(ps[0]+"\t"+ps[1]+"\t"+ps[2],Double.parseDouble(ps[rankingColumn])), Integer.valueOf(ps[gtLabelColumn]));


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

    public static void main(String[] args) throws IOException {

        String inputFile="verbalizedQueries.txt.label";

        int rankingScoreColumn=3;
        int labelColumn=5;

        String outputFile=inputFile+"."+rankingScoreColumn+".acc";

        if(args.length>0)
        {
            inputFile=args[2];
            rankingScoreColumn=Integer.valueOf(args[0]);
            labelColumn=Integer.valueOf(args[1]);
        }


        LinkedHashMap<Record, Integer> data = loadFile(inputFile,rankingScoreColumn,labelColumn);
        List<Group> groups=evaluateRanking(data);
        double accuracy=accuracy(groups);
        System.out.println(Joiner.on("\n").join(groups).toString());
        System.out.println("Accuracy: "+accuracy);
        dumpGroups(groups,outputFile);
    }


}
