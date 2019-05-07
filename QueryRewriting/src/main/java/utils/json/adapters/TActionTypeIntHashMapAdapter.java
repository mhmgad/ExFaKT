package utils.json.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import gnu.trove.map.hash.TObjectIntHashMap;
import utils.Enums;
import utils.json.CustomGson;

import java.lang.reflect.Type;

public class TActionTypeIntHashMapAdapter implements JsonSerializer<TObjectIntHashMap<Enums.ActionType>>, JsonDeserializer<TObjectIntHashMap<Enums.ActionType>>{



    @Override
    public JsonElement serialize(TObjectIntHashMap tObjectIntMap, Type type, JsonSerializationContext jsonSerializationContext) {

        Gson gson=CustomGson.getInstance().getGson();
        JsonArray entriesJson=new JsonArray();
        for (Object key: tObjectIntMap.keys()) {
            JsonObject entry=new JsonObject();
            entry.add("key",gson.toJsonTree(key) );
            entry.add("value",gson.toJsonTree(tObjectIntMap.get(key)));
            entriesJson.add(entry);


        }

        return entriesJson;
    }

    @Override
    public TObjectIntHashMap<Enums.ActionType> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonArray jsonArray = jsonElement.getAsJsonArray();

        TObjectIntHashMap<Enums.ActionType> map=new TObjectIntHashMap<>();

        for (JsonElement jsonE: jsonArray) {
            JsonObject entry=jsonE.getAsJsonObject();
            map.put(Enums.ActionType.valueOf(entry.get("key").getAsString()),entry.get("value").getAsInt()) ;
        }


        return map;
    }
}
