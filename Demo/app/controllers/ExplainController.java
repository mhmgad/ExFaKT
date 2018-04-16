package controllers;

import client.explain.ExplanationExtractorClient;
import com.google.inject.Inject;
import extendedsldnf.datastructure.IQueryExplanations;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import views.html.*;
import web.data.Query;

import java.io.IOException;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class ExplainController extends Controller {



    private final FormFactory formFactory;
    private final ExplanationExtractorClient client;

    @Inject
    public ExplainController(final FormFactory formFactory,  ExplanationExtractorClient client ) {
        this.formFactory = formFactory;
        this.client=client;

    }


    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {

        Query exampleQ = new Query("<Albert_Einstein>", "<wasBornIn>", "<Germany>",  "wasBornIn(?x,?y):- birthPlace(?x,?z), in(?z,?y).\nwasBornIn(?x,?y):-  birthPlace(?x,?z), city_in(?z,?y).\n wasBornIn(?x,?y):-  wasBornIn(?x,?z), isA(?z,'city'), isLocatedIn(?z,?y), ?z!=?y.");

        return ok(index.render(exampleQ,null));
    }


    public Result explain() {
        Form<Query> qf=formFactory.form(Query.class).bindFromRequest();
        Query q=qf.get();
        System.out.println("explain "+q);
//        Query exampleQ = new Query("<Albert_Einstein>", "<wasBornIn>", "<Germany>", "wasBornIn(?x,?y):- birthPlace(?x,?z), in(?z,?y).\nwasBornIn(?x,?y):-  birthPlace(?x,?z), city_in(?z,?y).");

        IQueryExplanations queryExplanations= null;
        try {
            queryExplanations = client.getExplanations(q);
        } catch (IOException e) {
            return internalServerError("Error connecting to server!");
        }

        return ok(index.render(q,queryExplanations));
    }



}
