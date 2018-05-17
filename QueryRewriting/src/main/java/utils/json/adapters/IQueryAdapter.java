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
            String query = jsonElement.getAsString();
            String queryFixed=query.replaceAll("(?<=[<_\\w])\\'(?=[\\w_>])","");
            if(!queryFixed.equals(query))
                System.out.println(query+ " => "+queryFixed);
            parser.parse(queryFixed);
            return parser.getQueries().get(0);
        } catch (ParserException e) {
            System.out.println(jsonElement.getAsString());

            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JsonElement serialize(IQuery iQuery, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(iQuery.toString());
    }


    public static void main(String[] args) {
        System.out.println("<'tes'Dt'gt'_s'>'".replaceAll("(?<=[<_\\w])\\'(?=[\\w_>])",""));
    }
}
