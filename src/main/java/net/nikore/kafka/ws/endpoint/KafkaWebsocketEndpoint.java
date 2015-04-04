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
package net.nikore.kafka.ws.endpoint;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import net.nikore.kafka.ws.consumer.KafkaConsumer;
import net.nikore.kafka.ws.messages.TextMessage;
import net.nikore.kafka.ws.producer.KafkaProducer;

@ServerEndpoint(
  value = "/v2/broker/",
  decoders = {TextMessage.TextMessageDecoder.class},
  encoders = {TextMessage.TextMessageEncoder.class},
  configurator = KafkaWebsocketEndpoint.Configurator.class
)
public class KafkaWebsocketEndpoint {
  private static final Logger logger = LoggerFactory.getLogger(KafkaWebsocketEndpoint.class);

  private KafkaConsumer consumer = null;

  @Inject
  public KafkaWebsocketEndpoint() {

  }

  private Map<String, String> getQueryMap(String query) {
    return ImmutableMap.copyOf(
      Stream.of(query != null ? query.split("&") : new String[0])
        .map(s -> s.split("="))
        .collect(Collectors.toMap(s -> s[0], s -> s[1]))
    );
  }

  private KafkaProducer producer() {
    return Configurator.getProducer();
  }

  @OnOpen
  @SuppressWarnings("unchecked")
  public void onOpen(final Session session) {
    String groupId;
    String topics;

    Properties sessionProps = (Properties) Configurator.getConsumerProps().clone();
    Map<String, String> queryParams = getQueryMap(session.getQueryString());
    if (queryParams.containsKey("group.id")) {
      groupId = queryParams.get("group.id");
    } else {
      groupId = sessionProps.getProperty("group.id") + "-" +
        session.getId() + "-" +
        String.valueOf(System.currentTimeMillis());
    }
    sessionProps.setProperty("group.id", groupId);

    boolean messageOnly = false;

    if (queryParams.containsKey("messagesOnly")) {
      messageOnly = true;
    }

    logger.debug("Opening new session {}", session.getId());
    if (queryParams.containsKey("topics")) {
      topics = queryParams.get("topics");
      logger.debug("Session {} topics are {}", session.getId(), topics);
      consumer = new KafkaConsumer(sessionProps, topics, session, messageOnly);
      consumer.start();
    }
  }

  @OnClose
  public void onClose(final Session session) {
    if (consumer != null) {
      consumer.stop();
    }
  }

  @OnMessage
  public void onMessage(final TextMessage message, final Session session) throws ExecutionException, InterruptedException {
    logger.info("Received text message: topic - {}; key - {}; message - {}",
      message.getTopic(), message.getKey(), message.getMessage());
    producer().send(message);
  }

  private void closeSession(Session session, CloseReason reason) {
    try {
      session.close(reason);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static class Configurator extends ServerEndpointConfig.Configurator {
    private static Properties consumerProps;
    private static Properties producerProps;
    private static KafkaProducer producer = null;

    public static void setKafkaProps(Properties consumerProps, Properties producerProps) {
      Configurator.consumerProps = consumerProps;
      Configurator.producerProps = producerProps;
    }

    public static Properties getConsumerProps() {
      return Configurator.consumerProps;
    }

    public static Properties getProducerProps() {
      return Configurator.producerProps;
    }

    public static KafkaProducer getProducer() {
      if (producer == null) {
        producer = new KafkaProducer(producerProps);
        producer.start();
      }
      return producer;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
      T endpoint = super.getEndpointInstance(endpointClass);

      if (endpoint instanceof KafkaWebsocketEndpoint) {
        return endpoint;
      }
      throw new InstantiationException(
        MessageFormat.format("Expected instanceof \"{0}\". Got instanceof \"{1}\".",
          KafkaWebsocketEndpoint.class, endpoint.getClass())
      );
    }
  }
}