package net.nikore.kafka.ws.modules;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ConsumerProperties {
  private static final String CONSUMER_PROPS_PATH = "config/consumer.properties";
  private static final Logger logger = LoggerFactory.getLogger(ProducerProeprties.class);

  private final Properties props;

  @Inject
  public ConsumerProperties() throws IOException {
    this.props = new Properties();
    props.load(new BufferedInputStream(new FileInputStream(CONSUMER_PROPS_PATH)));
    logger.info("Loaded the file properties: {}", props.toString());
  }

  public Properties getProps() {
    return props;
  }
}
