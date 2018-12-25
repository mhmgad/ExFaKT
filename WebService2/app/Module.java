import checker.ExplanationsExtractor;
import com.google.inject.AbstractModule;
import config.Configuration;
import play.Environment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
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

    String propFileName="webservice.properties";

    @Override
    public void configure() {
        // Use the system clock as the default implementation of Clock
//        bind(Clock.class).toInstance(Clock.systemDefaultZone());
        // Ask Guice to create an instance of ApplicationTimer when the
        // application starts.
//        bind(ApplicationTimer.class).asEagerSingleton();
        // Set AtomicCounter as the implementation for Counter.
//        bind(Counter.class).to(AtomicCounter.class);

//        Configuration.setConfigurationFile("/GW/D5data-7/gadelrab/demo_data/exfakt_local.properties.bac_may");
//
//        bind(Configuration.class).toInstance(Configuration.getInstance());


        InputStream inputStream = Environment.simple().resourceAsStream(propFileName);

        Properties prop=new Properties();

        try {
            if (inputStream != null) {

                prop.load(inputStream);

            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String engineConfFile=prop.getProperty("exfakt_conf");

        String engineConfFilePath=Environment.simple().getFile(engineConfFile).getAbsolutePath();
        System.out.println("ExFaKT Configuration: "+engineConfFilePath);


        Configuration.setConfigurationFile(engineConfFile,Environment.simple().classLoader());




        String spottingConfFile=prop.getProperty("spotting_conf");
        String spottingConfFilePath=Environment.simple().getFile(spottingConfFile).getAbsolutePath();
        System.out.println("Spotting Configuration: "+spottingConfFilePath);


        Configuration.getInstance().setSpottingConfFile(engineConfFile,Environment.simple().classLoader());
        // Bind configuration instance
        bind(Configuration.class).toInstance(Configuration.getInstance());

        //bind extractor instance
       bind(ExplanationsExtractor.class).toInstance(ExplanationsExtractor.getInstance());

        // bind the provider as eager singleton
//        bind(EbeanServer.class).toProvider(EbeanServerProvider.class).asEagerSingleton();
    }


}
