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
package net.nikore.kafka.ws.producer;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.nikore.kafka.ws.messages.TextMessage;

public class KafkaProducer {
  private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

  //  private ProducerConfig producerConfig;
  private final Properties producerConfig;
  private org.apache.kafka.clients.producer.KafkaProducer<String, byte[]> producer;

  public KafkaProducer(Properties configProps) {
    this.producerConfig = configProps;
  }

  public void start() {
    this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(producerConfig, new StringSerializer(), new ByteArraySerializer());
  }

  public void stop() {
    producer.close();
  }

  public void send(TextMessage message) throws ExecutionException, InterruptedException {
    if (message.isKeyed()) {
      logger.info("topic: {} key: {} message: {}", message.getTopic(), message.getKey(), message.getMessage());
      send(message.getTopic(), message.getKey(), message.getMessage().getBytes(Charset.forName("UTF-8")));
    } else {
      send(message.getTopic(), message.getMessage().getBytes(Charset.forName("UTF-8")));
    }
  }

  public void send(String topic, byte[] message) throws ExecutionException, InterruptedException {
    producer.send(new ProducerRecord<>(topic, message)).get();
  }

  public void send(String topic, String key, byte[] message) throws ExecutionException, InterruptedException {
    producer.send(new ProducerRecord<>(topic, key, message)).get();
  }
}
