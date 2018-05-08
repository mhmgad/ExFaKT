package de.mpii.dataprocessing.util;

public class FactUtils {


    public static String getCleanPredicateName(String predicateName) {
        if(predicateName.startsWith("<"))
            predicateName= predicateName.replace("<","").replace(">","");


        predicateName=predicateName.replaceAll(":","_").replaceAll("\\.","_");
        return predicateName;
    }

    public static String getCleanName(String key) {
        if(key.startsWith("<"))
            key=key.replace("<","");
        if(key.endsWith(">"))
            key=key.replace(">","");
        key=key.replace(":"," ").replaceAll("^([a-z]{2,3})/","").replaceAll("(_\\(.+?\\))$"," ").trim().replace("_"," ").replaceAll("(?=\\p{Upper})"," ").replaceAll(" +", " ");
        return key;

    }
}
