package utils.json.adapters;

import com.google.gson.*;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

import java.lang.reflect.Type;

public class IRuleAdapter implements JsonSerializer<IRule>,JsonDeserializer<IRule>{


    @Override
    public IRule deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Parser parser = new Parser();
        try {
            String query=jsonElement.getAsString().replaceAll("(?<=[<_\\w])\\'(?=[\\w_>])","");
            parser.parse(query);
            return parser.getRules().get(0);
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JsonElement serialize(IRule iRule, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(iRule.toString());
    }
}
