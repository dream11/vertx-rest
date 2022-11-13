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
The vert-rest library enables to create a vertx REST application based on JAX-RS annotations. A number of Request and Response Filters, 
Exception Mappers and features like back-pressure, validations, timeout, logging are provided.

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
   compile 'com.dream11:vertx-rest:x.y.z'
  }
```

Note: Replace `x.y.z` above with one of the [released versions](https://github.com/dream11/vertx-rest/releases)

### Create Rest Verticle:
The REST application is deployed as a verticle.
Create the Application REST Verticle by extending `com.dream11.rest.AbstractRestVerticle` class.
The `AbstractRestVerticle` here does the following:
* Finds all the resources(i.e, classes annotated with `@Path`) and adds an instance of each of the resource classes to the resteasy-deployment registry.
* Adds all the Filters and ExceptionMappers and any other custom Providers(Middle-wares) to the resteasy-deployment.
* Starts a http-server and dispatches each request to the handler registered with the resteasy-deployment.

Example:

```java   
package com.dream11.rest.verticle;

import com.dream11.rest.AbstractRestVerticle;
import com.dream11.rest.Constants;
import com.dream11.rest.injector.AppContext;
import io.reactivex.Completable;
import lombok.SneakyThrows;
import lombok.experimental.NonFinal;

public class RestVerticle extends AbstractRestVerticle {
    
    public RestVerticle() {
        super(Constants.TEST_PACKAGE_NAME);
    }
    
    @Override
    protected ClassInjector getInjector() {
      // Add your implmentation of injector
      return null;
    }
}
```
#### Injection

Implement abstract method `getInjector` of `AbstractRestVerticle` to return an implementation of the `ClassInjector` interface. This will 
be used for injecting instances of the REST resource classes, and any other types which may need to be injected.
Refer to [`com.dream11.rest.injector.GuiceInjector`](src/test/java/com/dream11/rest/injector/GuiceInjector.java) for an example.

### Create a Rest Resource

```java
@Path("/test")
public class TestRoute {

  @GET
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiResponse(content = @Content(schema = @Schema(implementation = String.class)))
  public CompletionStage<String> test() {
    return Single.just("Hello World")
            .toCompletionStage();
  }
}
```
Note: 
* The return type of the resource method(`test()` in the above example) should ideally be a Java class based on the expected response schema. In case of exceptions, `CompletionStage<Throwable>` can be returned directly. If the exception needs to be handled, handle it using the ExceptionMapper described in the section [Exception Handling](#exception-handling).
* The `@ApiResponse` is only required for creating swagger specification file.
* Resteasy context variables can be injected using `@Context` annotations
* (Refer [this](https://github.com/swagger-api/swagger-core/wiki/Annotations) for more annotations)

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

Please refer to the package `com.dream11.rest` inside `src/main/test/java` for an example application

## References

* [Swagger Maven Plugin](https://github.com/swagger-api/swagger-core/tree/master/modules/swagger-maven-plugin)
* [Resteasy Vertx](https://docs.jboss.org/resteasy/docs/3.1.0.Final/userguide/html/RESTEasy_Embedded_Container.html)
* [Hibernate Validations](https://hibernate.org/validator/documentation/getting-started/)
* [Jakarta Validation Constraints](https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/constraints/package-summary.html)
* [JAX-RS](https://docs.oracle.com/javaee/6/tutorial/doc/gilik.html)
         
