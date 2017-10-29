package utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 5/21/17.
 */
public class StringUtils {


    public static List<String> asList(String property){
        List<String> files=new LinkedList<>();
        if(!property.trim().isEmpty())
            files= Arrays.asList(property.split(",")).stream().map(sf->sf.trim()).collect(Collectors.toList());
        return files;
    }

    public static String indent(String string){
        return "\t"+string.replaceAll("\n","\n\t");
    }

}
