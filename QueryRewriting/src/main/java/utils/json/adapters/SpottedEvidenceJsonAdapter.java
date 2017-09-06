package utils.json.adapters;

import com.google.gson.*;
import de.mpii.factspotting.text.TextEvidence;

import java.lang.reflect.Type;

public class SpottedEvidenceJsonAdapter implements JsonSerializer<TextEvidence>,JsonDeserializer<TextEvidence> {

    @Override
    public TextEvidence deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Gson gson=new Gson();
        return gson.fromJson(jsonElement,TextEvidence.class);
    }

    @Override
    public JsonElement serialize(TextEvidence iSpottedEvidence, Type type, JsonSerializationContext jsonSerializationContext) {
        Gson gson=new Gson();
        return gson.toJsonTree(iSpottedEvidence);
    }
}
