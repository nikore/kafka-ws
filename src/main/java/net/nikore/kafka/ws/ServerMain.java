package net.nikore.kafka.ws;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import net.nikore.kafka.ws.modules.KafkaWSModule;

public class ServerMain {
  private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

  private static final String SERVER_PROPS_PATH = "config/server.properties";


  public static void main(String... args) throws IOException {
    Properties props = new Properties();
    props.load(new BufferedInputStream(new FileInputStream(SERVER_PROPS_PATH)));

    logger.info("Loaded the file properties: {}", props.toString());

    Injector injector = Guice.createInjector(new KafkaWSModule(props));

    JettyServer server = injector.getInstance(JettyServer.class);

    server.run();


  }
}
