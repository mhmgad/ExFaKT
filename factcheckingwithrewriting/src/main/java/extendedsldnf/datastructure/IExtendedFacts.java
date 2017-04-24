package extendedsldnf.datastructure;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;

/**
 * Created by gadelrab on 4/10/17.
 */
public interface IExtendedFacts extends IFacts {


    public IRelation getHypotheticalBindings(ILiteral literal);

    public boolean addAll(IExtendedFacts facts);
}
