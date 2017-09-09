package controllers;

import com.google.inject.Inject;
import extendedsldnf.datastructure.IQueryExplanations;
import models.Query;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class Explanations extends Controller {



//    ExplanationsExtractor explanationsExtractor=ExplanationsExtractor.getInstance();

    private final FormFactory formFactory;




    @Inject
    public Explanations(final FormFactory formFactory) {
        this.formFactory = formFactory;


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


        return ok(index.render(new Query("<Albert_Einstein>","<birthPlace>","<Ulm>","")));



    }

    public Result explain(){

    Form<Query> qf=formFactory.form(Query.class).bindFromRequest();
    Query q=qf.get();
        System.out.println("explain "+q);
//        System.out.println(new Query(qf.field("subject").getValue().get().toString(),qf.field("predicate").getValue().get().toString(),qf.field("object").getValue().get().toString(),qf.field("rules").getValue().get().toString()));

        IQueryExplanations explanations = q.explain();
        System.out.println(explanations);
        return ok(explans.render(q,explanations));
    }

}
