import datastructure.CorrectnessInfo;
import de.mpii.datastructures.Fact;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.*;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

import java.util.Arrays;

public class FactChecker {


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
////        Fact f=new Fact("diedIn",Arrays.asList("John F. Kennedy","Dallas"));
//        FactChecker fc=new FactChecker();
//        System.out.println(fc.checkCorrectness(f));
    }

}
