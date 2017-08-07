import de.mpii.datastructures.Fact;
import extendedsldnf.datastructure.IQueryExplanations;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.*;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FactChecker {


    public class CorrectnessInfo implements Comparable<CorrectnessInfo>{

        IQuery posQuery;
        IQuery negQuery;

        IQueryExplanations posExplanations;
        IQueryExplanations negExplanations;

        public CorrectnessInfo(IQuery posQuery, IQuery negQuery, IQueryExplanations posExplanations, IQueryExplanations negExplanations) {
            this.posQuery = posQuery;
            this.negQuery = negQuery;
            this.posExplanations = posExplanations;
            this.negExplanations = negExplanations;
        }

        public double getScore(){
        // different between positiveExplanations Size score and negative ones
            return  posExplanations.getScoreOnSize()-negExplanations.getScoreOnSize();
        }

        @Override
        public String toString() {
            return "CorrectnessInfo{" +
                    "posQuery=" + posQuery +
                    ", negQuery=" + negQuery +
                    ", correctnessScore="+getScore()+
                    ", posExplScore=" +  posExplanations.getScoreOnSize() + " ("+posExplanations.size()+")"+
                    ", negExplScore=" + -negExplanations.getScoreOnSize() + " ("+negExplanations.size()+")"+
                    '}';
        }


        @Override
        public int compareTo(CorrectnessInfo o) {
            int strDiff= this.posQuery.getLiterals().get(0).compareTo(o.posQuery.getLiterals().get(0));
            if(strDiff!=0)
                return strDiff;
            else
                return Double.compare(this.getScore(),o.getScore());

        }
    }


    ExplanationsExtractor extractor;

    public FactChecker() throws EvaluationException {
        this.extractor = new ExplanationsExtractor();
    }



    public CorrectnessInfo checkCorrectness(IQuery query){
        IQuery negQuery=negativeQuery(query);



        return new CorrectnessInfo(query,negQuery,extractor.check(query),extractor.check(negQuery));

    }



    private IQuery negativeQuery(IQuery query) {
        IBasicFactory factory = BasicFactory.getInstance();

        String negPredicateName = "not_" + query.getLiterals().get(0).getAtom().getPredicate();
        ITuple tuple = query.getLiterals().get(0).getAtom().getTuple();

        int arity=query.getLiterals().get(0).getAtom().getPredicate().getArity();
        IPredicate negPredicate = factory.createPredicate(negPredicateName, arity);
        IAtom negAtom=factory.createAtom(negPredicate,tuple);
        ILiteral negLiteral=factory.createLiteral(true,negAtom);
        IQuery negQuery= factory.createQuery(negLiteral);
        return negQuery;
    }



    public CorrectnessInfo checkCorrectness(Fact fact) {

        Parser parser = new Parser();
        try {
            parser.parse(fact.getIRISRepresenation());
            IQuery query = parser.getQueries().get(0);
            return checkCorrectness(query);

        } catch (ParserException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void main(String[] args) throws EvaluationException {
        Fact f=new Fact("diedIn",Arrays.asList("John F. Kennedy","Dallas"));
        FactChecker fc=new FactChecker();
        System.out.println(fc.checkCorrectness(f));
    }

}
