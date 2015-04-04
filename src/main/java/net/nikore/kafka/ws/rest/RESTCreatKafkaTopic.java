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
