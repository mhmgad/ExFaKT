package utils;

import java.util.Arrays;
import java.util.List;

public class RulesSyntaxChecker {



    public static void main(String[] args) {
        if (args.length==0){
            System.out.println("specify one or more file seperated with space");
        }
        else {

            List files = Arrays.asList(args);

            System.out.println("Check rules in files: " + files);

            DataUtils.loadRules(files);
        }

    }

}
