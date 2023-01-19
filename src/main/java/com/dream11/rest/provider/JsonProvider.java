package com.dream11.rest.provider;

import com.dream11.rest.annotation.TypeValidationError;
import com.dream11.rest.exception.RestException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.vertx.core.json.jackson.DatabindCodec;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

@Provider
@Consumes({"application/json", "application/*+json", "text/json"})
@Produces({"application/json", "application/*+json", "text/json"})
public class JsonProvider extends ResteasyJackson2Provider {

  public JsonProvider() {
    this.setMapper(DatabindCodec.mapper());
  }

  @SneakyThrows
  @Override
  public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                         MultivaluedMap<String, String> httpHeaders, InputStream entityStream) {
    try {
      return super.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
    } catch (JsonMappingException e) {
      List<JsonMappingException.Reference> referenceList = e.getPath();
      if (!referenceList.isEmpty()) {
        String fieldName = referenceList.get(0).getFieldName();
        if (fieldName != null) {
          TypeValidationError typeValidationError = type.getDeclaredField(fieldName).getAnnotation(TypeValidationError.class);
          if (typeValidationError != null) {
            throw new RestException(typeValidationError.code(), typeValidationError.message(),
                typeValidationError.httpStatusCode(), e);
          }
        }
      }
      throw new RestException("INVALID_REQUEST", e.getMessage(), HttpStatus.SC_BAD_REQUEST, e);
    } catch (IOException e) {
      throw new RestException("INVALID_REQUEST", e.getMessage(), HttpStatus.SC_BAD_REQUEST, e);
    }
  }
}
