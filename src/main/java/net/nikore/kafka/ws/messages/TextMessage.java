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
package net.nikore.kafka.ws.messages;

import java.io.IOException;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.nikore.kafka.ws.json.JacksonFactory;

public class TextMessage {
  private String topic;
  private String key;
  private String message;

  public TextMessage(String topic, String message) {
    this.topic = topic;
    this.message = message;
  }

  public TextMessage(String topic, String key, String message) {
    this.topic = topic;
    this.key = key;
    this.message = message;
  }

  @JsonIgnore
  public boolean isKeyed() {
    return key != null && !key.isEmpty();
  }

  public String getTopic() {
    return topic;
  }

  public String getKey() {
    return key;
  }

  public String getMessage() {
    return message;
  }

  static public class TextMessageDecoder implements Decoder.Text<TextMessage> {
    private static final ObjectMapper jsonParser = JacksonFactory.getMapper();

    public TextMessageDecoder() {

    }

    @Override
    public TextMessage decode(String s) throws DecodeException {
      try {
        return jsonParser.readValue(s, TextMessage.class);
      } catch (IOException e) {
        throw new DecodeException("", e.getMessage(), e);
      }
    }

    @Override
    public boolean willDecode(String s) {
      return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
  }

  static public class TextMessageEncoder implements Encoder.Text<TextMessage> {
    private static final ObjectMapper jsonParser = JacksonFactory.getMapper();

    public TextMessageEncoder() {

    }

    @Override
    public String encode(TextMessage textMessage) throws EncodeException {
      try {
        return jsonParser.writeValueAsString(textMessage);
      } catch (JsonProcessingException e) {
        throw new EncodeException(textMessage, "json error", e);
      }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
  }
}