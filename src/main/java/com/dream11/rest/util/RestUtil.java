package com.dream11.rest.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RestUtil {

  public static String objectToString(Object object) throws JsonProcessingException {
    ObjectMapper objectMapper = DatabindCodec.mapper();
    String str;
    if (object instanceof String) {
      str = (String) object;
    } else if (object instanceof JsonObject || object instanceof JsonArray) {
      str = String.valueOf(object);
    } else {
      str = objectMapper.writeValueAsString(object);
    }
    return str;
  }
}
