//package controllers;
//
//import com.google.inject.Inject;
//import extendedsldnf.datastructure.QueryExplanations;
//import io.ebean.Ebean;
//import io.ebean.EbeanServer;
//import data.Query;
//import org.jongo.Jongo;
//import org.jongo.MongoCollection;
//import play.api.Play;
//import play.data.FormFactory;
//import play.db.ebean.EbeanConfig;
//import play.mvc.Controller;
//import play.mvc.Result;
//import views.html.evaluation;
//
//public class Evaluation extends Controller {
//
////    @Inject
//    private final FormFactory formFactory;//=Play.current().injector().instanceOf(FormFactory.class);;
////    private final EbeanServer ebeanServer;
//
////    @Inject
////    private final Jongo jongo;//= Play.current().injector().instanceOf(Jongo.class);
//
//
//
//    @Inject
//    public Evaluation(final FormFactory formFactory, EbeanConfig ebeanConfig) {
////       this.jongo=jongo;
//        this.ebeanServer =Ebean.getServer(ebeanConfig.defaultServer());
//        this.formFactory = formFactory;
//    }
//
//
////    private MongoCollection explanations() {
////            return jongo.getCollection("explanations");
////    }
//
//
//
////    public  Query findByName(String query) {
////        return explanations().findOne("{query: #}", query).as(Query.class);
////    }
//
//    public Result index() {
//
//        ebeanServer.save(new models.QueryExplanations("test"));
//
////        Query query=findByName("?- actedWith('<Dan_Aykroyd>', '<Gene_Hackman>').");
//        Query exampleQ = new Query("<Albert_Einstein>", "<wasBornIn>", "<Germany>", "wasBornIn(?x,?y):- birthPlace(?x,?z), in(?z,?y).\nwasBornIn(?x,?y):-  birthPlace(?x,?z), city_in(?z,?y).");
////        return ok(evaluation.render(query,query.getQueryExplanations()));
//            return ok(evaluation.render(exampleQ,exampleQ.explain()));
//    }
//
//
//
//
//
//
//
//
//}
