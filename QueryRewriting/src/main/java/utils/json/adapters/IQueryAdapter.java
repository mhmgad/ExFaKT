package utils.json.adapters;

import com.google.gson.*;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

import java.lang.reflect.Type;

public class IQueryAdapter implements JsonSerializer<IQuery>,JsonDeserializer<IQuery>{


    @Override
    public IQuery deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Parser parser = new Parser();
        try {
            parser.parse(jsonElement.getAsString());
            return parser.getQueries().get(0);
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JsonElement serialize(IQuery iQuery, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(iQuery.toString());
    }
}
