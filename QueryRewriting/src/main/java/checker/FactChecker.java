package checker;

import datastructure.CorrectnessInfo;
import de.mpii.datastructures.Fact;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.*;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class FactChecker {


    ExplanationsExtractor extractor;
    private final static FactChecker instance=new FactChecker();

    @Inject
    public FactChecker()  {
            this.extractor = ExplanationsExtractor.getInstance();
    }



    public CorrectnessInfo checkCorrectness(IQuery query, int groundTruthLabel){
        IQuery negQuery=negativeQuery(query);



        return new CorrectnessInfo(query,negQuery,extractor.check(query),extractor.check(negQuery),groundTruthLabel);

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



    public CorrectnessInfo checkCorrectness(Fact fact,int groundTruthLabel) {

        Parser parser = new Parser();
        try {
            parser.parse(fact.getIRISQueryRepresenation());
            IQuery query = parser.getQueries().get(0);
            return checkCorrectness(query, groundTruthLabel);

        } catch (ParserException e) {
            e.printStackTrace();
        }
        return null;

    }


    public synchronized static FactChecker getInstance(){
        return instance;
    }


    public static void main(String[] args) throws EvaluationException {
////        Fact f=new Fact("diedIn",Arrays.asList("John F. Kennedy","Dallas"));
//        checker.FactChecker fc=new checker.FactChecker();
//        System.out.println(fc.checkCorrectness(f));
    }

}
