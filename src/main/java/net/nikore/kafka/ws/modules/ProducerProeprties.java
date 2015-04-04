package net.nikore.kafka.ws.modules;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ProducerProeprties {
  private static final Logger logger = LoggerFactory.getLogger(ProducerProeprties.class);
  private static final String PRODUCER_PROPS_PATH = "config/producer.properties";

  private final Properties props;

  @Inject
  public ProducerProeprties() throws IOException {
    this.props = new Properties();
    props.load(new BufferedInputStream(new FileInputStream(PRODUCER_PROPS_PATH)));
    logger.info("Loaded the file properties: {}", props.toString());
  }

  public Properties getProps() {
    return props;
  }
}
