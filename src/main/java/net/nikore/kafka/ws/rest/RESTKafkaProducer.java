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
package net.nikore.kafka.ws.rest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.nikore.kafka.ws.json.JsonMessage;
import net.nikore.kafka.ws.messages.TextMessage;
import net.nikore.kafka.ws.producer.KafkaProducer;

@Singleton
@Path("/api/send")
@Produces(MediaType.APPLICATION_JSON)
public class RESTKafkaProducer {
  private final KafkaProducer producer;
  private final ObjectMapper mapper;

  @Inject
  public RESTKafkaProducer(KafkaProducer producer, ObjectMapper mapper) {
    this.producer = producer;
    this.mapper = mapper;
  }

  @POST
  public String post(@FormParam("topic") String topic,
                     @FormParam("key") String key,
                     @FormParam("message") String message) throws Exception {

    producer.send(new TextMessage(topic, key, message));
    return mapper.writeValueAsString(new JsonMessage("ok", "message received"));
  }

  @GET
  public String get(@QueryParam("topic") String topic,
                    @QueryParam("key") String key,
                    @QueryParam("message") String message) throws Exception {

    producer.send(new TextMessage(topic, key, message));
    return mapper.writeValueAsString(new JsonMessage("ok", "message received"));
  }
}
