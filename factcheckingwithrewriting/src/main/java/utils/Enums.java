package utils;

import java.util.Comparator;

/**
 * Created by gadelrab on 4/27/17.
 */
public class Enums {
    public static enum ActionType {BUILT_IN(0), ORG(0) , KG_VALID(10), TEXT_VALID(15),  KG_BIND(20), TEXT_BIND(25), GREEDY_BIND(27), RULE_EXPAND(30), UNCLASSIFEIED(100);

        private int priority;

        ActionType(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }

        public static Comparator<ActionType> priorityComparator =new Comparator<ActionType>()
        {
            public int compare(ActionType o1, ActionType o2)
            {
                return o1.getPriority()-o2.getPriority();
            }
        };
    }


    public static enum EvalMethod{SLD,SLD_ITR,HEURISTIC};




    //public enum BindingSource{FACT,TEXT, GREEDY}

}
