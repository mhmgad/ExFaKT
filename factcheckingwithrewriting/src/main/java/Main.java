import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Fact;
import extendedsldnf.datastructure.IExplaination;
import mpi.tools.javatools.util.FileUtils;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/30/17.
 */
public class Main {


    public static void main(String[] args) throws EvaluationException, FileNotFoundException, ParserException {
        Configuration configuration=Configuration.getInstance();

        RuleBasedChecker rfc=new RuleBasedChecker();
        List<IExplaination> explainations=new LinkedList<>();
        List<IQuery> queries=new LinkedList<>();
        if(!configuration.getQueiesFiles().isEmpty()){

            for (String filename:configuration.getQueiesFiles()) {
                BufferedReader br = FileUtils.getBufferedUTF8Reader(filename);

                Parser pr=new Parser();
                pr.parse(br);
                queries.addAll(pr.getQueries());
                explainations.addAll(processList(rfc,queries));

            }
        }

        Iterator<IExplaination> explansItr=explainations.iterator();
        for (IQuery query:queries) {
            System.out.println(query+":\t"+explansItr.next());

        }

    }

    private static List<IExplaination> processList(RuleBasedChecker rfc, List<IQuery> queries) {
        return queries.parallelStream().map(q->rfc.check(q)).collect(Collectors.toList());
    }
}
