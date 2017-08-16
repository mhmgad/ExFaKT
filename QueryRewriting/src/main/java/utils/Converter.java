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

import java.util.List;
import java.util.stream.Collectors;

import static org.deri.iris.factory.Factory.BASIC;

/**
 * Created by gadelrab on 3/22/17.
 */
public class Converter {


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
}
