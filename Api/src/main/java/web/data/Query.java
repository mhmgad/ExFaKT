package web.data;


import com.google.gson.Gson;

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


    int numOfRules=5;
    int numOfexplan=5;

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setKgs(List<String> kgs) {
        this.kgs = kgs;
    }



//    public void setTextWeight(List<String> textWeight) {
//        this.textWeight = textWeight;
//    }

    public void setNumOfRules(int numOfRules) {
        this.numOfRules = numOfRules;
    }

    public void setNumOfexplan(int numOfexplan) {
        this.numOfexplan = numOfexplan;
    }

    public List<String> getKgs() {
        return kgs;
    }


    public void setTextualSources(List<TextualSource> textualSources) {
        this.textualSources = textualSources;
    }

    public List<TextualSource> getTextualSources() {
        return textualSources;
    }

//    public List<String> getTextWeight() {
//        return textWeight;
//    }

    public int getNumOfRules() {
        return numOfRules;
    }

    public int getNumOfexplan() {
        return numOfexplan;
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

    public Query(String subject, String predicate, String object, String rules) {
        this(-1, (subject+" "+predicate+" "+object),subject,  predicate,  object,  rules);

    }

    public Query(int id, String name, String subject, String predicate, String object, String rules) {
        this.id=id;
        this.name=name;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.rules = rules;



    }

    public void addKg(String kgName){
        kgs.add(kgName);
    }

    public void addTextualSource(TextualSource textualSource){
        textualSources.add(textualSource);
    }

    public Query() {
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

    public int getId() {
        return id;
    }

    //    public List<IRule> getRulesAsIRIS(){
//        Parser parser=new Parser();
//
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








