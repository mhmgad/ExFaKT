package web.data;


import com.google.gson.Gson;
import extendedsldnf.datastructure.TextualSource;

import java.util.ArrayList;
import java.util.List;

public class Query /*extends QueryExplanations*/{

    String name;
    int id;

//    ExplanationsExtractor explanationsExtractor=ExplanationsExtractor.getInstance();



    String subject;
    String predicate;
    String object;
    String rules;

    List<String> kgs=new ArrayList<>();
    List<TextualSource> textualSources=new ArrayList<>();


    int numOfRules=3;
    int numOfExplan=5;

    public Query(String subject, String predicate, String object, String rules) {
        this(-1, (subject+" "+predicate+" "+object),new ArrayList<>(), new ArrayList<>(),subject,  predicate,  object,  rules);

    }

    public Query(int id, String name,List<String> kgs, List<TextualSource> textualSources,String subject, String predicate, String object, String rules) {
        this.id=id;
        this.name=name;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.rules = rules;
        this.kgs=kgs;
        this.textualSources=textualSources;


    }

    public Query() {
    }



//    public void setTextWeight(List<String> textWeight) {
//        this.textWeight = textWeight;
//    }

    public List<String> getKgs() {
        return kgs;
    }

    public void setKgs(List<String> kgs) {
        this.kgs = kgs;
    }

    public List<TextualSource> getTextualSources() {
        return textualSources;
    }

    public void setTextualSources(List<TextualSource> textualSources) {
        this.textualSources = textualSources;
    }

    public int getNumOfRules() {
        return numOfRules;
    }

//    public List<String> getTextWeight() {
//        return textWeight;
//    }

    public void setNumOfRules(int numOfRules) {
        this.numOfRules = numOfRules;
    }

    public int getNumOfExplan() {
        return numOfExplan;
    }

//    @Override
//    public String getQuery() {
////        return super.getQuery();
//        BinaryFact f=new BinaryFact(subject,predicate,object);
//        return f.getIRISQueryRepresenation();
//
//    }

//    public IQueryExplanations getQueryExplanations() {
//        return this;
//    }

    public void setNumOfExplan(int numOfexplan) {
        this.numOfExplan = numOfexplan;
    }

    public void addKg(String kgName){
        kgs.add(kgName);
    }

    public void addTextualSource(TextualSource textualSource){
        textualSources.add(textualSource);
    }

    @Override
    public String toString() {
        return "Query{" +
                "subject='" + subject + '\'' +
                ", predicate='" + predicate + '\'' +
                ", object='" + object + '\'' +
                ", rules='" + rules + '\'' +
                '}';
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String toJsonString(){
        return new Gson().toJson(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean hasTextualSource(String textualSource){
        return textualSources.stream().anyMatch(ts-> ts.getSourceName().equals(textualSource));
    }

    public boolean hasKg(String kg){
        return kgs.stream().anyMatch(k-> k.equals(kg));
    }

    public double getTextualSourceWeight(String textualSource){

//        if(hasTextualSource(textualSource))
            return textualSources.stream().filter(ts -> ts.getSourceName().equals(textualSource)).findFirst().orElse(new TextualSource("Dummy",0.1)).getWeight();

    }


    //    public List<IRule> getRulesAsIRIS(){
//        Parser parser=new Parser();
//number
//        try {
//            parser.parse(rules);
//        } catch (ParserException e) {
//            e.printStackTrace();
//        }
//
//        return parser.getRules();
//    }

//    public BinaryFact getAsFact(){
//        return new BinaryFact(subject,predicate,object);
//    }

//    public IQueryExplanations explain(){
//        return explanationsExtractor.check(getAsFact(),getRulesAsIRIS());
//    }

}








