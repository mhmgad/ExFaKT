package extendedsldnf;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import utils.Enums;

/**
 * Created by gadelrab on 7/10/17.
 */
public class CostAccumulator implements Comparable<CostAccumulator>{


    private  long startTime;
    private  long elapsedTime;
    int totalCost;
    double relativeCost;


    TObjectIntHashMap<Enums.ActionType> individualCounts;

    static TObjectIntMap<Enums.ActionType> costMap;
    static {
        costMap=new TObjectIntHashMap<>();
        costMap.put(Enums.ActionType.RULE_EXPAND,1);
        costMap.put(Enums.ActionType.KG_VALID,1);
        costMap.put(Enums.ActionType.KG_BIND,1);
        costMap.put(Enums.ActionType.GREEDY_BIND,2);
        costMap.put(Enums.ActionType.TEXT_VALID,2);
        costMap.put(Enums.ActionType.TEXT_BIND,2);
        costMap.put(Enums.ActionType.UNCLASSIFIED,0);
    }

    private CostAccumulator fullCost;
    private double relativeElapsedTime;


    public CostAccumulator(){
        totalCost=0;

        individualCounts=new TObjectIntHashMap<>();

        for (Enums.ActionType t: Enums.ActionType.values()) {
                individualCounts.put(t, 0);
        }
        startTime = System.currentTimeMillis();
        elapsedTime =-1;
    }

    public CostAccumulator(int totalCost, TObjectIntMap<Enums.ActionType> individualCounts,long startTime,long timeElapsed){
        this();
        this.totalCost=totalCost;
        this.individualCounts.putAll(individualCounts);
        this.startTime = startTime;
        this.elapsedTime =timeElapsed;
    }



    public synchronized void addCost(Enums.ActionType type){
        addCost(type,CostAccumulator.costMap.get(type));
    }

    public synchronized void addCost(Enums.ActionType type, int cost){
        individualCounts.adjustValue(type,1);
        totalCost+=cost;
    }


    @Override
    public synchronized CostAccumulator clone() {
        long endTime=System.currentTimeMillis();
        return new CostAccumulator(this.totalCost,this.individualCounts,startTime,endTime-startTime);
    }

    @Override
    public String toString() {
        return "CostAccumulator{" +
                "totalCost=" + totalCost +
                ", individualCounts=" + individualCounts +
                ",time="+ (elapsedTime / 1000.0)+" Sec"+
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

    public static void main(String[] args){
        CostAccumulator obj = new CostAccumulator();
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
    public double getElapsedTimeSec() {
        return elapsedTime/1000.0;
    }

    public double getRelativeCost() {
        return relativeCost;
    }

    public void setQueryFullCost(CostAccumulator fullCost){
        this.fullCost=fullCost;
        this.setRelativeCost(fullCost.getTotalCost());
        this.setRelativeElapsedTime(fullCost.getElapsedTime());
    }
    public void setRelativeCost(int totalQueryCost){
        this.relativeCost=(this.totalCost+0.0)/totalQueryCost;
    }

    public void setRelativeElapsedTime(long queryElapsedTime) {
        this.relativeElapsedTime = (this.elapsedTime+0.0)/queryElapsedTime;
    }

    public  double getRelativeElapsedTime() {
        return this.relativeElapsedTime;
    }
}
