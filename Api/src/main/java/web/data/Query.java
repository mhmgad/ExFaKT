package web.data;

import checker.ExplanationsExtractor;
import de.mpii.datastructures.BinaryFact;
import extendedsldnf.datastructure.IQueryExplanations;
import extendedsldnf.datastructure.QueryExplanations;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;


import java.util.List;

public class Query /*extends QueryExplanations*/{

    String id;

    ExplanationsExtractor explanationsExtractor=ExplanationsExtractor.getInstance();



    String subject;
    String predicate;
    String object;
    String rules;



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
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.rules = rules;

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

    public List<IRule> getRulesAsIRIS(){
        Parser parser=new Parser();

        try {
            parser.parse(rules);
        } catch (ParserException e) {
            e.printStackTrace();
        }

        return parser.getRules();
    }

    public BinaryFact getAsFact(){
        return new BinaryFact(subject,predicate,object);
    }

    public IQueryExplanations explain(){
        return explanationsExtractor.check(getAsFact(),getRulesAsIRIS());
    }

}








