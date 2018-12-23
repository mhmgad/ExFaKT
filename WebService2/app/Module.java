import checker.ExplanationsExtractor;
import com.google.inject.AbstractModule;
import config.Configuration;
//import io.ebean.EbeanServer;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    @Override
    public void configure() {
        // Use the system clock as the default implementation of Clock
//        bind(Clock.class).toInstance(Clock.systemDefaultZone());
        // Ask Guice to create an instance of ApplicationTimer when the
        // application starts.
//        bind(ApplicationTimer.class).asEagerSingleton();
        // Set AtomicCounter as the implementation for Counter.
//        bind(Counter.class).to(AtomicCounter.class);

//        Configuration.setConfigurationFile("/GW/D5data-7/gadelrab/demo_data/fact_checking_rewriting.properties.bac_may");
//
//        bind(Configuration.class).toInstance(Configuration.getInstance());
//
//
       bind(ExplanationsExtractor.class).toInstance(ExplanationsExtractor.getInstance());

        // bind the provider as eager singleton
//        bind(EbeanServer.class).toProvider(EbeanServerProvider.class).asEagerSingleton();
    }


}