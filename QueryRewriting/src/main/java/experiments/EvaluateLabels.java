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
import stats.Variance;
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

        boolean counted;
        public boolean isCounted(){
            return counted;
        }


        public double getAccuracy() {
            return accuracy;
        }

        public Group(String key, List<Integer> sortedLabels,List<Double> scores, double accuracy,boolean counted) {
            this.key = key;
            this.sortedLabels = sortedLabels;
            this.scores=scores;
            this.accuracy = accuracy;
            this.counted=counted;
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
    public static List<Group> evaluateRanking( Map<Record, Integer> data,boolean excludeDraws, double drawScore) {

        List<Group> groups=new ArrayList<>();
        Map<String, List<Record>> groupedCorrectness = data.keySet().stream().collect(Collectors.groupingBy(Record::getGroup, LinkedHashMap::new,Collectors.toList()));
        //System.out.println("groups :" + groupedCorrectness.keySet() + "\n" + groupedCorrectness.size());


        double accuracyTotal = 0;
        for (String key: groupedCorrectness.keySet()) {
            List<Record>  gInfo = groupedCorrectness.get(key);

            List<Integer> labels = gInfo.stream().sorted(Comparator.comparing(Record::getScore).reversed().thenComparing(Comparator.comparing(g->data.get(g)).reversed())).mapToInt(g -> data.get(g)).boxed().collect(Collectors.toList());
            List<Double> scores = gInfo.stream().sorted(Comparator.comparing(Record::getScore).reversed()).mapToDouble(Record::getScore).boxed().collect(Collectors.toList());


            boolean toCount = scores.stream().anyMatch(a -> a != 0);
            boolean isDraw=(scores.stream().distinct().count()==1);
            if(excludeDraws)
                toCount=!isDraw;



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

            if ((!excludeDraws) && isDraw && drawScore!=-1)
                accuracy=0.5;

            groups.add(new Group(key,labels,scores,accuracy,toCount));

//            System.out.println("acc: " + predictionsCount + "/" + groundTruthScore + "=" + accuracy);
            accuracyTotal += accuracy;

        }

        return groups;


    }

    public static double accuracy(List<Group> groups){
        return groups.stream().filter(Group::isCounted).mapToDouble(Group::getAccuracy).average().getAsDouble();
    }

    public static double recall(List<Group> groups){
        return groups.stream().filter(Group::isCounted).count()/(groups.size()+0.0);
    }

    public static LinkedHashMap<Record,Integer> loadFile(String filePath, int rankingColumn, int gtLabelColumn) throws Exception {
        LinkedHashMap<Record,Integer> data= new LinkedHashMap<>();

        BufferedReader br = FileUtils.getBufferedUTF8Reader(filePath);

        for(String l=br.readLine();l!=null;l=br.readLine()){

            if(l.trim().isEmpty())
                continue;
            System.out.println(l);
            String ps[]=l.trim().split("\t");
            if(!ps[rankingColumn].trim().matches("[\\-0-9.]*")) {
                System.out.println("not a number"+ps[rankingColumn]);
                continue;
            }

            Fact f=null;
            if(ps[0].startsWith("?-"))
            {
                String queryFixed=ps[0].replaceAll("(?<=[<_\\w])\\'(?=[\\w_>])","");
                Parser pr=new Parser();
                pr.parse(queryFixed);
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
    public static double var(List<Group> groups){
        System.out.println("Length: "+groups.stream().filter(Group::isCounted).mapToDouble(Group::getAccuracy).count());
        return Variance.varianceStreams(groups.stream().filter(Group::isCounted).mapToDouble(Group::getAccuracy).toArray());

    }


    public static void main(String[] args) throws Exception {

//        String inputFile="/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/fact_spotting_data/exper2/out/rules_heursitic.out.correctness";

//        String inputFile="/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/factchecking_exper/output/out_2018_05_21_011149/rules_text_bing.out.correctness";
//        String inputFile="/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/checking2/output/out_2018_05_21_205439/diedIn/rules_text_splitq.out.correctness";

//       String inputFile="/home/gadelrab/work/factcheck_exper/verbalizedQueries.txt.label"; //3 5
//        int rankingScoreColumn=3;
//        int labelColumn=5;
//        String inputFile="/local/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/checking2/output/out_2018_05_21_223919/rules_text_splitq.out.correctness";
//        String inputFile="/local/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/checking2/output/out_2018_05_21_231604/wasBornIn/rules_text_splitq.out.correctness";
//        String inputFile="/local/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/checking2/output/out_2018_05_22_040440/vicePresident/rules_text_splitq.out.correctness";
//         String inputFile="/local/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/checking2/output/out_2018_05_22_113435/isCitizenOf/rules_text_splitq.out.correctness";
//        String inputFile="/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/checking2/output/out_2018_05_22_215311/graduatedFrom/rules_text_splitq.out.correctness";

        String inputFile="/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/checking2/output/tocikm/collected.tsv";
//        String inputFile="/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/factchecking_exper/output/out_/rules_text_splitq.out.correctness";
//        String inputFile="data/exper2_results_voting.tsv";
//        int rankingScoreColumn=3;
//        int labelColumn=4;

//         String inputFile="/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/checking2/output/verbalizedQueries_op_all_label.tsv";
//        int rankingScoreColumn=3;
//        int labelColumn=5;

//        String inputFile="/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/explain/checking2/output/tocikm/graduatedFrom_1/rules_text_splitq.out.correctness";
//        int rankingScoreColumn=9;
//        int labelColumn=15;

//        String inputFile="/home/gadelrab/mpiRoot/GW/D5data-7/gadelrab/fact_spotting_data/exper2/out_norules/rules_heursitic.out.correctness";
//        int rankingScoreColumn=11;
//        int labelColumn=15;

        int rankingScoreColumn=9;
        int labelColumn=15;

        if(args.length>0)
        {
            inputFile=args[2];
            rankingScoreColumn=Integer.valueOf(args[0]);
            labelColumn=Integer.valueOf(args[1]);
        }
        String outputFile=inputFile+"."+rankingScoreColumn+"_"+ CorrectnessInfo.header[rankingScoreColumn]+".acc";

        LinkedHashMap<Record, Integer> data = loadFile(inputFile,rankingScoreColumn,labelColumn);
        List<Group> groups=evaluateRanking(data,false,-1);
        List<Group> groupsNoDraws=evaluateRanking(data,true,-1);
//        List<Group> groups05=evaluateRanking(data,false,0.5);

        double accuracy=accuracy(groups);
        System.out.println(Joiner.on("\n").join(groups).toString());
        System.out.println("Accuracy: "+accuracy+ " Recall: "+recall(groups));
        System.out.println("Accuracy no draws: "+accuracy(groupsNoDraws)+" Recall: "+recall(groupsNoDraws));
        System.out.println("Variance: "+var(groups));
//        System.out.println("Accuracy 0.5 for draws: "+accuracy(groups05)+" Recall: "+recall(groups05));
//        System.out.println();

        dumpGroups(groups,outputFile);
    }


}
