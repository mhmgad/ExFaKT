package controllers;

import checker.ExplanationsExtractor;
import checker.FactChecker;
import config.Configuration;
import de.mpii.datastructures.BinaryFact;
import extendedsldnf.datastructure.Explanation;
import extendedsldnf.datastructure.IQueryExplanations;
import play.api.data.Form;
import play.mvc.*;

import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class Explanations extends Controller {


    ExplanationsExtractor explanationsExtractor=ExplanationsExtractor.getInstance();

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

        return ok(index.render("Your new application is ready."));



    }

    public Result explain(){

        System.out.println("Heeere explain");
        return ok(index.render("Submitted"));
    }

}
