package net.nikore.kafka.ws.json;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonFactory {

  private static final ObjectMapper mapper = new ObjectMapper();

  public static ObjectMapper getMapper() {
    return mapper;
  }
}
