/**
 * Copyright (C) 2015 Matt Christiansen (matt@nikore.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
