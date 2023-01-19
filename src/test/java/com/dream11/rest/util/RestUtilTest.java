package com.dream11.rest.util;

import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class RestUtilTest {

  @Test
  void testListObjectToString() throws JsonProcessingException {
    // arrange
    List<Integer> integerList = Arrays.asList(1, 2, 3);
    // act
    String listToString = RestUtil.objectToString(integerList);
    // assert
    assertThat(listToString, Matchers.equalTo("[1,2,3]"));
  }

  @Test
  void testStringObjectToString() throws JsonProcessingException {
    // arrange
    String message = "MESSAGE";
    // assert
    assertThat(RestUtil.objectToString(message), Matchers.equalTo(message));
  }

  @Test
  void testJsonObjectToString() throws JsonProcessingException {
    // arrange
    JsonObject jsonObject = new JsonObject().put("key", "key");
    // act
    String jsonObjectString = RestUtil.objectToString(jsonObject);
    // assert
    assertThat(jsonObjectString, Matchers.equalTo(jsonObject.toString()));
    assertThat(RestUtil.objectToString(jsonObject), Matchers.equalTo(jsonObject.toString()));
  }

  @Test
  void testJsonArrayToString() throws JsonProcessingException {
    // arrange
    JsonArray jsonArray = new JsonArray().add(1).add(2);
    // act
    String jsonArrayString = RestUtil.objectToString(jsonArray);
    // assert
    assertThat(jsonArrayString, Matchers.equalTo(jsonArray.toString()));
  }
}
