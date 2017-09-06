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

public class IVariableJsonAdapter implements JsonSerializer<ITerm>,JsonDeserializer<ITerm> {


    @Override
    public ITerm deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        ITermFactory termF= TermFactory.getInstance();
        System.out.println(jsonElement);


        String stringV = jsonElement.getAsString();

        if(stringV.startsWith("?"))
            stringV=stringV.substring(1);
            IVariable t=termF.createVariable(stringV);






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
