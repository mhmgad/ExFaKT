package extendedsldnf.datastructure;

/**
 * Created by gadelrab on 7/19/17.
 */
public class QueriesPoolFactory {



    public static AbstractQueriesPool getPool(AbstractQueriesPool.ComparisionMethod method){
        switch (method){

            case HEURISTIC:
                return new HeuristicBasedQueriesPool();

            case DFS:
            default:
                return new DFSQueriesPool();

        }

    }
}
