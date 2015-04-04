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
package net.nikore.kafka.ws.consumer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import net.nikore.kafka.ws.messages.TextMessage;

public class KafkaConsumer {
  private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

  private final ExecutorService executorService = Executors.newCachedThreadPool();

  private final Session session;
  private final ConsumerConfig consumerConfig;
  private final List<String> topics;
  private final Async remoteEndpoint;
  private final boolean messagesOnly;
  private ConsumerConnector connector;

  public KafkaConsumer(Properties configProps, final String topics, final Session session, final boolean messagesOnly) {
    this.remoteEndpoint = session.getAsyncRemote();
    this.consumerConfig = new ConsumerConfig(configProps);
    this.topics = Arrays.asList(topics.split(","));
    this.session = session;
    this.messagesOnly = messagesOnly;
  }

  public void start() {
    logger.info("Starting consumer for {}", session.getId());
    this.connector = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);

    Map<String, Integer> topicCountMap = new HashMap<>();
    for (String topic : topics) {
      topicCountMap.put(topic, 1);
    }
    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = connector.createMessageStreams(topicCountMap);

    for (String topic : topics) {
      logger.info("Adding stream for session {}, topic {}", session.getId(), topic);
      final List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
      for (KafkaStream<byte[], byte[]> stream : streams) {
        executorService.submit(new KafkaConsumerTask(stream, remoteEndpoint, session, messagesOnly));
      }
    }
  }

  public void stop() {
    logger.info("Stopping consumer for session {}", session.getId());
    connector.commitOffsets();
    try {
      Thread.sleep(5000);
    } catch (InterruptedException ie) {
      logger.error("Exception while waiting to shutdown consumer: {}", ie.getMessage());
    }
    if (connector != null) {
      logger.trace("Shutting down connector for session {}", session.getId());
      connector.shutdown();
    }
    if (executorService != null) {
      logger.trace("Shutting down executor for session {}", session.getId());
      executorService.shutdown();
    }
    logger.info("Stopped consumer for session {}", session.getId());
  }

  static public class KafkaConsumerTask implements Runnable {
    private final KafkaStream stream;
    private final Async remoteEndpoint;
    private final Session session;
    private final boolean messagesOnly;

    public KafkaConsumerTask(KafkaStream stream, Async remoteEndpoint, final Session session, final boolean messagesOnly) {
      this.stream = stream;
      this.remoteEndpoint = remoteEndpoint;
      this.session = session;
      this.messagesOnly = messagesOnly;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
      for (MessageAndMetadata<byte[], byte[]> messageAndMetadata : (Iterable<MessageAndMetadata<byte[], byte[]>>) stream) {
        String topic = messageAndMetadata.topic();
        byte[] key = messageAndMetadata.key();
        byte[] message = messageAndMetadata.message();
        sendText(topic, key, message);
      }
    }

    private void sendText(String topic, byte[] key, byte[] message) {
      String messageString = new String(message, Charset.forName("UTF-8"));
      String keyString = new String(key, Charset.forName("UTF-8"));
      logger.info("XXX Sending text message to remote endpoint: {} {}", topic, messageString);
      if (messagesOnly) {
        remoteEndpoint.sendObject(messageString);
      } else {
        remoteEndpoint.sendObject(new TextMessage(topic, keyString, messageString));
      }
    }

    private void closeSession(Exception e) {
      logger.debug("Consumer initiated close of session {}", session.getId());
      try {
        session.close(new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, e.getMessage()));
      } catch (IOException ioe) {
        logger.error("Error closing session: {}", ioe.getMessage());
      }
    }
  }
}
