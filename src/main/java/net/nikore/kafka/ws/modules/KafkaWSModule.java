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

import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import kafka.utils.ZKStringSerializer$;
import net.nikore.kafka.ws.json.JacksonFactory;
import net.nikore.kafka.ws.producer.KafkaProducer;
import net.nikore.kafka.ws.rest.RESTCreatKafkaTopic;
import net.nikore.kafka.ws.rest.RESTKafkaProducer;

public class KafkaWSModule extends JerseyServletModule {
  private final Properties props;


  public KafkaWSModule(Properties props) {
    this.props = props;
  }


  @Override
  protected void configureServlets() {
    Names.bindProperties(binder(), props);
    bind(ConsumerProperties.class);
    bind(ProducerProeprties.class);
    bind(RESTKafkaProducer.class);
    bind(RESTCreatKafkaTopic.class);
    serve("/*").with(GuiceContainer.class);
  }

  @Provides
  @Singleton
  public KafkaProducer getKakfaProducer(ProducerProeprties producerProeprties) {
    KafkaProducer producer = new KafkaProducer(producerProeprties.getProps());
    producer.start();
    return producer;
  }

  @Provides
  @Singleton
  ObjectMapper provideObjectMapper() {
    return JacksonFactory.getMapper();
  }

  @Provides
  @Singleton
  ZkClient provideZKClient(ConsumerProperties probs) {
    String zkHosts = probs.getProps().getProperty("zookeeper.connect");
    return new ZkClient(zkHosts, 10000, 10000, ZKStringSerializer$.MODULE$);
  }
}
