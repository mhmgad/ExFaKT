package utils.json;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.deri.iris.api.basics.ILiteral;
import utils.json.adapters.ILiteralAdapter;

public class CustomGson {

    private static CustomGson gsonInstance=new CustomGson();



    Gson gson;

    public CustomGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson=gsonBuilder.registerTypeAdapter(ILiteral.class, new ILiteralAdapter()).create();
//        this.gson=gsonBuilder.create();
    }

    public Gson getGson() {
        return gson;
    }

    public static CustomGson getInstance() {
        return gsonInstance;
    }
}
