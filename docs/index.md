# vertx-rest
[![Continuous Integration](https://github.com/dream11/vertx-rest/actions/workflows/ci.yml/badge.svg)](https://github.com/dream11/vertx-rest/actions/workflows/ci.yml)
[![Code Coverage](https://codecov.io/gh/dream11/vertx-rest/branch/master/graph/badge.svg)](https://codecov.io/gh/dream11/vertx-rest)
![License](https://img.shields.io/badge/license-MIT-green.svg)

- [Overview](#overview)
- [Setup](#setup)
- [How to use](#usage)
  - [Rest Verticle](#create-rest-verticle)
  - [JAX-RS Routes](#create-a-rest-resource)
- [Validations](#validations)
- [Exception Handling](#exception-handling)
- [Dependency Injection](#dependency-injection)
- [Custom Providers](#custom-providers)
- [Timeouts](#timeouts)
- [Examples](#examples)
- [Swagger](#swagger-specification)
- [References](#references)

## Overview
- Abstraction over [resteasy-vertx](https://github.com/resteasy/resteasy/tree/main/server-adapters/resteasy-vertx) to simplify writing a
  vertx REST application based on JAX-RS annotations
- Provides a [backpressure](https://github.com/ReactiveX/RxJava/wiki/Backpressure) enabled HTTP server to drop requests on high load
- Exception mappers to standardise error response
- Timeout annotation for closing long-running requests
- Uses `rxjava3`

## Setup

Add the following dependency to the `dependencies` section of your build descriptor:

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

## Usage

### Create Rest Verticle
Create the application REST Verticle by extending `com.dream11.rest.AbstractRestVerticle` class.
The `AbstractRestVerticle` here does the following:
* Finds all the resources(i.e, classes annotated with `@Path`) in the package and adds an instance of each of the resource classes to
  the resteasy-deployment registry
* Adds all the `Filters` and `ExceptionMappers` and any other custom `Providers` to the resteasy-deployment
* Starts a backpressure enabled HTTP server and dispatches each request to the handler registered with the resteasy-deployment

```java   
public class RestVerticle extends AbstractRestVerticle {
    
  public RestVerticle() {
    super("com.dream11.package");
  }
  
  @Override
  protected ClassInjector getInjector() {
    // Add your implmentation of injector
    return null;
  }
}
```

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

## Validations
* Use all the jakarta constraints given [here](https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/constraints/package-summary.html)
* You can use `@TypeValidationError(code=<errorCode>, message=<errorMessage>)` on DTO parameters to send custom error code and message
  when parsing of parameter fails
* `@TypeValidationError` can also be used for `Integer`, `Long`, `Float` or `Double` types in `@HeaderParam`, `@QueryParam` etc. If you
  want to use `@TypeValidationError` on a parameter of type other than these types you can create a custom converter similar to `IntegerParamConverter` and a provider extending `ParamConverterProvider`. [Reference](https://blog.sebastian-daschner.com/entries/jaxrs-convert-params)

## Exception Handling
- Provides exceptions and exception mappers to standardize error response
- Implement `RestError` as an enum to specify error codes, messages and http status codes to be returned in response for your exceptions
  - Throw `RestException` with the restError to return error response in the following format
  ```json
  {
    "error": {
      "code": "UNKNOWN_EXCEPTION",
      "message": "Something went wrong",
      "cause": "Cannot connect to mysql database"
    } 
  }
  ```
- Refer to the package [`com.dream11.rest.exception.mapper`](src/main/java/com/dream11/rest/exception/mapper) for mappers provided by
  default
- Implement your own exception mappers if you need some other error response

## Timeouts
- Configure request timeout by annotating your JAX-RS resource classes with `@Timeout(value = <timeoutMillis>, httpStatusCode =
  <httpStausCode>)`
- Default timeout for each JAX-RS route is `20` seconds
- Since there is no official HTTP Status code for origin server timeout, it has been kept configurable. By default `500` status code
  is returned in case of timeouts.

## Dependency Injection
- Implement abstract method `getInjector` of `AbstractRestVerticle` to return an implementation of the `ClassInjector` interface
- This will be used for injecting instances of the REST resource classes
- Refer to [`com.dream11.rest.injector.GuiceInjector`](src/test/java/com/dream11/rest/injector/GuiceInjector.java) for an example

## Custom Providers
Annotate your provider classes with `@Provider` to automatically register them with resteasy deployment

## Examples
Please refer [tests](/src/test/java/com/dream11/rest) for an example application

## Swagger Specification
Follow [this](/docs/swagger/swagger-generation.md) doc to generate swagger specification.

## References

* [Swagger Maven Plugin](https://github.com/swagger-api/swagger-core/tree/master/modules/swagger-maven-plugin)
* [Resteasy Vertx](https://docs.jboss.org/resteasy/docs/3.1.0.Final/userguide/html/RESTEasy_Embedded_Container.html)
* [Hibernate Validations](https://hibernate.org/validator/documentation/getting-started/)
* [Jakarta Validation Constraints](https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/constraints/package-summary.html)
* [JAX-RS](https://docs.oracle.com/javaee/6/tutorial/doc/gilik.html)
* [Swagger Annotations](https://github.com/swagger-api/swagger-core/wiki/Annotations)
