package controllers;

import checker.ExplanationsExtractor;
import checker.ExplanationsExtractorDemo;
import com.google.gson.Gson;
import com.google.inject.Inject;
import de.mpii.datastructures.BinaryFact;
import extendedsldnf.datastructure.InputQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import web.data.Query;
import extendedsldnf.datastructure.IQueryExplanations;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import utils.json.CustomGson;
import views.html.*;

import java.util.List;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class Explanations extends Controller {



    private final ExplanationsExtractorDemo explanationsExtractor;

    private final FormFactory formFactory;




    @Inject
    public Explanations(final FormFactory formFactory,ExplanationsExtractorDemo explanationsExtractor ) {
        this.formFactory = formFactory;
        this.explanationsExtractor=explanationsExtractor;

    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {


//        System.out.println(Configuration.getInstance().getSpottingConfFile());
//
//        IQueryExplanations res = explanationsExtractor.check(new BinaryFact("Albert Einstein", "was_born_in", "ulm"));
        Query exampleQ = new Query("<Albert_Einstein>", "<wasBornIn>", "<Germany>", "wasBornIn(?x,?y):- birthPlace(?x,?z), in(?z,?y).\nwasBornIn(?x,?y):-  birthPlace(?x,?z), city_in(?z,?y).");

        return ok(index.render(exampleQ,null));



    }

    public Result explain(){

        Form<Query> qf=formFactory.form(Query.class).bindFromRequest();
        Query q=qf.get();
        System.out.println("explain "+q);
//        System.out.println(new Query(qf.field("subject").getValue().get().toString(),qf.field("predicate").getValue().get().toString(),qf.field("object").getValue().get().toString(),qf.field("rules").getValue().get().toString()));

        // parseRulesAs




        Parser parser=new Parser();

        List<IRule> ruleList=null;
        try {

            parser.parse(q.getRules());
            ruleList=parser.getRules();
        } catch (ParserException e) {
            e.printStackTrace();
        }

        InputQuery inputQuery=new InputQuery(new BinaryFact(q.getSubject(),q.getPredicate(),q.getObject()),0,0);

        inputQuery.setTextualSources(q.getTextualSources());
        inputQuery.setKgs(q.getKgs());
        inputQuery.setMaxExplanations(q.getNumOfExplan());
        inputQuery.setMaxRules(q.getNumOfRules());



        IQueryExplanations explanations = explanationsExtractor.check( inputQuery,ruleList);
        System.out.println(explanations);
        Gson gson=CustomGson.getInstance().getGson();

        return ok(gson.toJson(explanations));

//        return ok(index.render(q,explanations));
    }

}
