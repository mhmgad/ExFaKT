//
//import com.google.inject.Provider;
//import io.ebean.EbeanServer;
//import io.ebean.EbeanServerFactory;
//import io.ebean.config.ServerConfig;
//
//
//public class EbeanServerProvider implements Provider<EbeanServer> {
//
//  @Override
//  public EbeanServer get() {
//    ServerConfig config = new ServerConfig();
//    config.setName("pg");
//    // load configuration from ebean.properties
//    config.loadFromProperties();
//    config.setDefaultServer(true);
//
//
//    return EbeanServerFactory.create(config);
//  }
//}