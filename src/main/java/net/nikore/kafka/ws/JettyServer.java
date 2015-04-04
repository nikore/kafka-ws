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
package net.nikore.kafka.ws;

import java.util.Properties;

import javax.inject.Named;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.GuiceFilter;

import net.nikore.kafka.ws.endpoint.KafkaWebsocketEndpoint;
import net.nikore.kafka.ws.modules.ConsumerProperties;
import net.nikore.kafka.ws.modules.ProducerProeprties;

public class JettyServer {
  private static final Logger logger = LoggerFactory.getLogger(JettyServer.class);

  private final Integer port;
  private final String contextPath;
  private final Properties consumerProps;
  private final Properties producerProps;

  @Inject
  public JettyServer(@Named("kafka.ws.server.port") Integer port, @Named("kafka.ws.server.context") String contextPath,
                     ConsumerProperties consumerProperties, ProducerProeprties producerProeprties) {
    this.port = port;
    this.contextPath = contextPath;
    this.consumerProps = consumerProperties.getProps();
    this.producerProps = producerProeprties.getProps();
  }


  public void run() {
    try {
      Server server = new Server();
      ServerConnector connector = new ServerConnector(server);

      connector.setPort(port);

      server.addConnector(connector);

      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
      context.setContextPath(contextPath);
      context.addFilter(GuiceFilter.class, "/api/*", null);
      server.setHandler(context);

      ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(context);
      KafkaWebsocketEndpoint.Configurator.setKafkaProps(consumerProps, producerProps);
      wsContainer.addEndpoint(KafkaWebsocketEndpoint.class);

      server.start();
      server.join();
    } catch (Exception e) {
      logger.error("Failed to start the server: ", e);
    }
  }
}
