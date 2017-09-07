package controllers;

import checker.ExplanationsExtractor;

import com.google.inject.Inject;
import models.Query;


import org.webjars.play.WebJarsUtil;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

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

        Form<Query> form=formFactory.form(Query.class);
        return ok(index.render(form));



    }

    public Result explain(){

    Form<Query> qf=formFactory.form(Query.class).bindFromRequest();

        System.out.println(qf.get());
        System.out.println(new Query(qf.field("subject").getValue().get().toString(),qf.field("predicate").getValue().get().toString(),qf.field("object").getValue().get().toString()));
        System.out.println("Heeere explain");
        return ok(index.render(qf));
    }

}
