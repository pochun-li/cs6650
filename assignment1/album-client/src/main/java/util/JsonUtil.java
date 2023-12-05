package util;

import com.google.gson.Gson;

/**
 * json util
 */
public class JsonUtil {

    private static Gson instance = new Gson();

    private JsonUtil(){

    }

    public static String toJson(Object o){
        return instance.toJson(o);
    }
}
