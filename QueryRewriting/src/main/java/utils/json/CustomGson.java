package utils.json;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.mpii.factspotting.ISpottedEvidence;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import utils.Enums;
import utils.json.adapters.*;

import java.lang.reflect.Type;

public class CustomGson {

    private static CustomGson gsonInstance=new CustomGson();



    Gson gson;

    public CustomGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
//        Type typeOfObject = new TypeToken<Enums.ActionType>() { }.getType();
//        this.gson=gsonBuilder.registerTypeAdapter(ILiteral.class, new ILiteralAdapter()).registerTypeAdapter(ISpottedEvidence.class,new ISpottedEvidence.JsonSpottedEvidenceAdapter()).create();
        this.gson=gsonBuilder
                .registerTypeAdapter(ILiteral.class, new ILiteralAdapter())
                .registerTypeAdapter(ISpottedEvidence.class,new SpottedEvidenceJsonAdapter())
                .registerTypeAdapter(IRule.class,new IRuleAdapter())
                .registerTypeAdapter(IQuery.class,new IQueryAdapter())
                .registerTypeAdapter(TObjectIntHashMap.class,new TActionTypeIntHashMapAdapter())
                .registerTypeHierarchyAdapter(ITerm.class,new ITermJsonAdapter())
                .registerTypeHierarchyAdapter(IVariable.class,new IVariableJsonAdapter())
                .create();

        this.gson=gsonBuilder.create();
    }

    public Gson getGson() {
        return gson;
    }

    public static CustomGson getInstance() {
        return gsonInstance;
    }
}
