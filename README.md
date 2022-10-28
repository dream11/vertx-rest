# vertx-rest
[![Continuous Integration](https://github.com/dream11/vertx-rest/actions/workflows/ci.yml/badge.svg)](https://github.com/dream11/rest/actions/workflows/ci.yml)
[![Code Coverage](https://codecov.io/gh/dream11/vertx-rest/branch/master/graph/badge.svg)](https://codecov.io/gh/dream11/vertx-rest)
![License](https://img.shields.io/badge/license-MIT-green.svg)

- [Overview](#overview)
- [How to use](#usage)
  - [Rest Verticle](#create-rest-verticle)
    - [Injection](#injection)
  - [JAX-RS Routes](#create-a-rest-resource)
    - [Validations](#validations)
    - [Exception Handling](#exception-handling)
    - [Swagger](#swagger-specification)
    - [Timeouts](#timeouts)
    - [Example Usage](#example-usage)
- [References](#references)

## Overview
The vert-rest library aims to make it simple to create a REST application using vertx. A number of Request and Response Filters, Exception Mappers and features like back-pressure, validations, timeout, logging are provided.

It internally uses [resteasy-vertx](https://github.com/resteasy/resteasy/tree/main/server-adapters/resteasy-vertx)
## Usage

Add the following dependency to the *dependencies* section of your build descriptor:

- Maven (in your `pom.xml`):
```xml
  <dependency>
    <groupId>com.dream11</groupId>
    <artifactId>vertx-rest</artifactId>
    <version>x.y.z</version>
  </dependency>
```

- Gradle (in your `build.gradle` file):
```
  dependencies {
   compile 'com.dream11:vertx-aerospike-client:x.y.z'
  }
```

Note: Replace `x.y.z` above with one of the [released versions](https://github.com/dream11/vertx-rest/releases)

### Create Rest Verticle:
The REST application is deployed as a verticle.
Create the Application REST Verticle by extending `com.dream11.rest.AbstractRestVerticle` class.
The `AbstractRestVerticle` here does the following:
* Finds all the resources(i.e, classes annotated with `@Path`) and adds an instance of each of the resource classes to the resteasy-deployment registry.
* Adds all the Filters and ExceptionMappers and any other custom Providers(Middle-wares) to the resteasy-deployment.
* Starts an http-server and dispatches each request to the handler registered with the resteasy-deployment.

Example:

```java   
package com.dream11.rest.app.verticle;

import com.dream11.rest.AbstractRestVerticle;
import com.dream11.rest.app.Constants;
import com.dream11.rest.app.inject.AppContext;
import io.d11.aerospike.client.AerospikeConnectOptions;
import io.d11.reactivex.aerospike.client.AerospikeClient;
import io.reactivex.Completable;
import lombok.SneakyThrows;
import lombok.experimental.NonFinal;

public class RestVerticle extends AbstractRestVerticle {
    @NonFinal
    private static AerospikeClient aerospikeClient;
    
    public RestVerticle() {
        super(Constants.TEST_PACKAGE_NAME, AppContext.getContextInstance());
    }

    @Override
    public Completable rxStart() {
        AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
                .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
                .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
        aerospikeClient = AerospikeClient.create(vertx, connectOptions);
        vertx.getOrCreateContext().put(AerospikeClient.class.getName(), aerospikeClient);
        return super.rxStart();
    }

    @Override
    public Completable rxStop() {
        aerospikeClient.getDelegate().close();
        return super.rxStop();
    }
}
```
#### Injection

Note that the constructor of  `AbstractRestVerticle` requires an Implementation of the `ClassInjector` interface. This will be used for injecting instances of the REST resource classes, and any other types which may need to be injected.
Refer to [`com.dream11.rest.app.inject.AppContext`](src/test/java/com/dream11/rest/app/inject/AppContext.java) for an example.

### Create a Rest Resource

```java
package com.dream11.rest.app.routes;

import com.dream11.rest.annotation.Timeout;
import com.dream11.rest.app.dao.HealthCheckDao;
import com.dream11.rest.app.dto.HealthCheckResponseDTO;
import com.dream11.rest.util.CompletableFutureUtils;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

@Slf4j
@Path("/healthcheck")
@Timeout(value = 20000, httpStatusCode = 500)
public class HealthCheckRoute {
  @Inject
  HealthCheckDao healthCheckDao;

  @GET
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiResponse(content = @Content(schema = @Schema(implementation = HealthCheckResponseDTO.class)))
  public CompletionStage<HealthCheckResponseDTO> healthcheck() {
    return HealthCheckResponseDTO.asyncResponseDtoFromMap(ImmutableMap.of(
                    "aerospike", healthCheckDao.aerospikeHealthCheck()
            ))
            .to(CompletableFutureUtils::fromSingle);
  }
}
```
Note: 
* The return type of the resource method(`healthcheck()` in the above example) should ideally be a Java class based on the expected response schema. In case of exceptions, `CompletionStage<Throwable>` can be returned directly. If the exception needs to be handled, handle it using the ExceptionMapper described in the section [Exception Handling](#exception-handling).
* The `@ApiResponse` is only required for creating swagger specification file.
(Refer [this](https://github.com/swagger-api/swagger-core/wiki/Annotations) for more annotations)

#### Validations

* Use all the constraints given [here](https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/constraints/package-summary.html)
* You can use `@TypeValidationError(code=<errorCode>, message=<errorMessage>)` on DTO parameters to send custom error code and message
  when parsing of parameter fails.
* `@TypeValidationError` can also be used for `Integer`, `Long`, `Float` or `Double` types in `@HeaderParam`, `@QueryParam` etc. If you
  want to use `@TypeValidationError` on a parameter of type other than these types you can create a custom converter similar to `IntegerParamConverter` and a provider extending `ParamConverterProvider`. [Reference](https://blog.sebastian-daschner.com/entries/jaxrs-convert-params)

#### Exception Handling
Each exception of a class, let's say `ExampleException`, can be handled(or default handling can be over-ridden ) by implementing an `ExceptionMapper<ExampleException>` and annotating it with `@Provider`. If an ExceptionMapper is not implemented for a class of exceptions, The `GenericExceptionMapper` will be used.  
(Refer to the package [`com.dream11.rest.exception.mapper`](src/main/java/com/dream11/rest/exception/mapper) for mappers provided by default)

Note:
* The REST resource method doesn't need to handle exceptions. ExceptionMappers should be used instead. The reason behind this is that if the resource method handles the exception and returns a response object containing an error message, but not the exception itself, the http status code will be that of an OK response(200 by default). 

#### Swagger Specification
Follow [this](docs/swagger/swagger-generation.md) doc to generate swagger specification.

#### Timeouts

* Default timeout for each JAX-RS route is `20` seconds
* You can change the timeout of a particular JAX-RS resource by `@Timeout(value = <timeoutMillis>, httpStatusCode = <httpStausCode>)` annotation on the respective class or method
* Note: Since there is no official HTTP Status code for origin server timeout, it has been kept configurable. By default, 500 status code is returned in case of timeouts.

#### Example Usage

Please refer to the package `com.dream11.rest.app` inside `src/main/test/java` for an example Application with a simple `/healthcheck` API

## References

* [Swagger Maven Plugin](https://github.com/swagger-api/swagger-core/tree/master/modules/swagger-maven-plugin)
* [Resteasy Vertx](https://docs.jboss.org/resteasy/docs/3.1.0.Final/userguide/html/RESTEasy_Embedded_Container.html)
* [Hibernate Validations](https://hibernate.org/validator/documentation/getting-started/)
* [Jakarta Validation Constraints](https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/constraints/package-summary.html)
* [JAX-RS](https://docs.oracle.com/javaee/6/tutorial/doc/gilik.html)
         
