package extendedsldnf;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.custom_hash.TObjectIntCustomHashMap;
import utils.Enums;

/**
 * Created by gadelrab on 7/10/17.
 */
public class CostAccumulator implements Comparable<CostAccumulator>{


    int totalCost;


    TObjectIntMap<String> individualCounts;

    static TObjectIntMap<String> costMap;



    public CostAccumulator(){
        totalCost=0;

        individualCounts=new TObjectIntCustomHashMap<>();

        for (Enums.ActionType t: Enums.ActionType.values()) {
            System.out.println(t);
            if(t!=null)
                individualCounts.put(t.name(),0);
        }


        costMap=new TObjectIntCustomHashMap<>();
        costMap.put(Enums.ActionType.RULE_EXPAND.name(),1);
        costMap.put(Enums.ActionType.KG_VALID.name(),1);
        costMap.put(Enums.ActionType.KG_BIND.name(),1);
        costMap.put(Enums.ActionType.GREEDY_BIND.name(),2);
        costMap.put(Enums.ActionType.TEXT_VALID.name(),2);
        costMap.put(Enums.ActionType.TEXT_BIND.name(),2);
        costMap.put(Enums.ActionType.UNCLASSIFEIED.name(),0);

        System.out.println("Action Costs: "+costMap);

    }

    public CostAccumulator(int totalCost, TObjectIntMap<String> individualCounts){
        this();
        this.individualCounts.putAll(individualCounts);

    }



    public synchronized void addCost(Enums.ActionType type){

        addCost(type,CostAccumulator.costMap.get(type));
    }

    public synchronized void addCost(Enums.ActionType type, int cost){
        individualCounts.adjustValue(type.name(),1);
        totalCost+=cost;
    }


    @Override
    public synchronized CostAccumulator clone() {
        return new CostAccumulator(this.totalCost,this.individualCounts);
    }

    @Override
    public String toString() {
        return "CostAccumulator{" +
                "totalCost=" + totalCost +
                ", individualCounts=" + individualCounts +
                '}';
    }

    @Override
    public int compareTo(CostAccumulator that) {
        return Integer.compare(this.totalCost,that.totalCost);
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getCount(Enums.ActionType type){
        return individualCounts.get(type.name());

    }
}
