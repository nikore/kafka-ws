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
