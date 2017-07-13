package extendedsldnf;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.custom_hash.TObjectIntCustomHashMap;
import utils.Enums;

import javax.swing.*;

/**
 * Created by gadelrab on 7/10/17.
 */
public class CostAccumulator implements Comparable<CostAccumulator>{


    int totalCost;


    TObjectIntMap<Enums.ActionType> individualCounts;

    static TObjectIntMap<Enums.ActionType> costMap=new TObjectIntCustomHashMap<>();
    static {
        costMap=new TObjectIntCustomHashMap<>();
        costMap.put(Enums.ActionType.RULE_EXPAND,1);
        costMap.put(Enums.ActionType.KG_VALID,1);
        costMap.put(Enums.ActionType.KG_BIND,1);
        costMap.put(Enums.ActionType.GREEDY_BIND,2);
        costMap.put(Enums.ActionType.TEXT_VALID,2);
        costMap.put(Enums.ActionType.TEXT_BIND,2);
        costMap.put(Enums.ActionType.UNCLASSIFEIED,0);
    }


    public CostAccumulator(){
        totalCost=0;

        individualCounts=new TObjectIntCustomHashMap<>();

        for (Enums.ActionType t: Enums.ActionType.values()) {
                individualCounts.put(t,0);
        }
    }

    public CostAccumulator(int totalCost, TObjectIntMap<Enums.ActionType> individualCounts){
        this.totalCost=totalCost;

        this.individualCounts=new TObjectIntCustomHashMap<>();
        this.individualCounts.putAll(individualCounts);

    }



    public synchronized void addCost(Enums.ActionType type){

        addCost(type,costMap.get(type));
    }

    public synchronized void addCost(Enums.ActionType type, int cost){
        individualCounts.adjustValue(type,1);
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
        return individualCounts.get(type);

    }
}
