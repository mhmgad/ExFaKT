package utils.json.adapters;

import com.google.gson.*;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

import java.lang.reflect.Type;

public class ILiteralAdapter implements  JsonSerializer<ILiteral>,JsonDeserializer<ILiteral> {


//    @Override
//    public ILiteral createInstance(Type type) {
//        IBasicFactory basicF = BasicFactory.getInstance();
//        return basicF.createLiteral(true,basicF.createPredicate("s",2), basicF.createTuple(null,null));
//    }
//

    @Override
    public ILiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String literalasQuery="?- "+jsonElement.getAsString()+".";
        Parser parser = new Parser();
        try {
            parser.parse(literalasQuery);
            return parser.getQueries().get(0).getLiterals().get(0);
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JsonElement serialize(ILiteral iLiteral, Type type, JsonSerializationContext jsonSerializationContext) {


        return new JsonPrimitive(iLiteral.toString());
    }
}
