package utils.json.adapters;

import com.google.gson.*;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.api.terms.IConstructedTerm;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.terms.TermFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

public class ITermJsonAdapter implements JsonSerializer<ITerm>,JsonDeserializer<ITerm> {


    private static Map<String, Class> map = new TreeMap<String, Class>();

    static {
        map.put(IVariable.class.getName(), IVariable.class);
        map.put(ITerm.class.getName(), ITerm.class);
        map.put(IStringTerm.class.getName(), IStringTerm.class);
        map.put(IConstructedTerm.class.getName(), IConstructedTerm.class);
    }
    @Override
    public ITerm deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        ITermFactory termF= TermFactory.getInstance();
//        System.out.println(jsonElement);

        ITerm t=null;
        String typeE = jsonElement.getAsJsonObject().get("type").getAsString();
        Class c = map.get(typeE);


        if(c==IVariable.class)
            t=termF.createVariable(jsonElement.getAsJsonObject().get("term").getAsString());
        else
        if(c==IStringTerm.class)
            t=termF.createString(jsonElement.getAsJsonObject().get("term").getAsString());





        return t;
    }

    @Override
    public JsonElement serialize(ITerm term, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o=new JsonObject();

        o.addProperty("type",term.getClass().getName());
        o.add("term",new JsonPrimitive(term.toString()));
        return o;
    }
}
