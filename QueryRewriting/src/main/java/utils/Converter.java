package utils;

import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Fact;
import org.apache.lucene.util.QueryBuilder;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.basics.Atom;
import org.deri.iris.basics.Literal;
import org.deri.iris.compiler.Parser;

import java.util.List;
import java.util.stream.Collectors;

import static org.deri.iris.factory.Factory.BASIC;

/**
 * Created by gadelrab on 3/22/17.
 */
public class Converter {

    public static BinaryFact toFact(IQuery query){
        try {
            return toFact(query.getLiterals().get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static BinaryFact toFact(ILiteral queryLiteral) throws Exception {

        String predicate=queryLiteral.getAtom().getPredicate().getPredicateSymbol();
        ITuple tuple = queryLiteral.getAtom().getTuple();
//        List<String> args=tuple.stream().map(arg->arg.isGround()? ((String)arg.getValue()):arg.toString()).collect(Collectors.toList());
        if(tuple.size()==2){
            List<String> args=tuple.stream().map(arg->arg.isGround()? ((String)arg.getValue()):"").collect(Collectors.toList());
            return new BinaryFact(args.get(0),predicate,args.get(1));
        }
        else
        {
            throw new Exception("Only Binary predicates are supported");
        }

    }

    public static IQuery toIQuery(BinaryFact fact) {
        Parser parser = new Parser();
        try {
            parser.parse(fact.getIRISQueryRepresenation());
            IQuery query = parser.getQueries().get(0);
            return  query;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
