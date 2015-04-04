package net.nikore.kafka.ws.rest;

import java.util.Properties;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.I0Itec.zkclient.ZkClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;

import kafka.admin.AdminUtils;
import net.nikore.kafka.ws.json.JsonMessage;

@Singleton
@Path("/api/create")
@Produces(MediaType.APPLICATION_JSON)
public class RESTCreatKafkaTopic {
  private final ZkClient zkClient;
  private final ObjectMapper mapper;

  @Inject
  public RESTCreatKafkaTopic(ZkClient zkClient, ObjectMapper mapper) {
    this.zkClient = zkClient;
    this.mapper = mapper;
  }

  @GET
  public String get(@QueryParam("topic") String topic) throws JsonProcessingException {
    AdminUtils.createTopic(zkClient, topic, 1, 1, new Properties());

    return mapper.writeValueAsString(new JsonMessage("ok", "created topic " + topic));
  }
}
