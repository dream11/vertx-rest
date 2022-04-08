# vertx-rest

- [Installation](#installation)
- [How to use](#how-to-use)
  - [Rest Verticle](#create-rest-verticle)
  - [JAX-RS Routes](#create-a-rest-resource)
    - [Validations](#validations)
    - [Swagger](#create-swagger-specificationui)
    - [Timeouts](#timeouts)
- [References](#references)

## Usage

Add the following dependency to the *dependencies* section of your build descriptor:

- Maven (in your `pom.xml`):
```xml
  <dependency>
    <groupId>com.dream11</groupId>
    <artifactId>vertx-rest</artifactId>
    <version>LATEST</version>
  </dependency>
```

- Gradle (in your `build.gradle` file):
```
  dependencies {
   compile 'com.dream11:vertx-aerospike-client:LATEST'
  }
```

### Create Rest Verticle:

Create Application REST Verticle by extending `com.dream11.rest.AbstractRestVerticle` class.

example:

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

### Create a Rest Resource

```java
package com.dream11.rest.app.routes;

import com.dream11.rest.app.dao.HealthCheckDao;
import com.dream11.rest.app.dto.HealthCheckResponseDTO;
import com.dream11.rest.app.util.CompletableFutureUtils;
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
The `@ApiResponse` is only required for creating swagger specification file.
(Refer [this](https://github.com/swagger-api/swagger-core/wiki/Annotations) for more annotations)

#### Validations

* Use all the constraints given [here](https://javaee.github.io/javaee-spec/javadocs/javax/validation/constraints/package-summary.html)
* You can use `@TypeValidationError(code=<errorCode>, message=<errorMessage>)` on DTO parameters to send custom error code and message
  when parsing of parameter fails.
* `@TypeValidationError` can also be used for `Integer`, `Long`, `Float` or `Double` types in `@HeaderParam`, `@QueryParam` etc. If you
  want to use `@TypeValidationError` on a parameter of type other than these types you can create a custom converter similar to `IntegerParamConverter` and a provider extending `ParamConverterProvider`. [Reference](https://blog.sebastian-daschner.com/entries/jaxrs-convert-params)


#### Create Swagger Specification/UI
* Add all swagger-ui resource files present [here](https://github.com/swagger-api/swagger-ui/tree/master/dist), inside `${project.basedir}/src/main/resources/webroot/swagger`. One way to do this is by adding the following plugins in your `pom.xml`.
```xml
<plugins>
  <plugin>
    <groupId>com.googlecode.maven-download-plugin</groupId>
    <artifactId>download-maven-plugin</artifactId>
    <version>1.6.8</version>
    <executions>
      <execution>
        <id>swagger-ui-download</id>
        <goals>
          <goal>wget</goal>
        </goals>
        <phase>generate-resources</phase>
        <configuration>
          <url>https://github.com/swagger-api/swagger-ui/archive/master.zip</url>
          <unpack>false</unpack>
          <outputDirectory>${project.build.directory}/swagger</outputDirectory>
        </configuration>
      </execution>
    </executions>
  </plugin>

  <plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>truezip-maven-plugin</artifactId>
    <version>1.2</version>
    <executions>
      <execution>
        <id>copy dist</id>
        <goals>
          <goal>copy</goal>
        </goals>
        <phase>generate-resources</phase>
        <configuration>
          <fileset>
            <directory>${project.build.directory}/swagger/master.zip/swagger-ui-master/dist</directory>
            <outputDirectory>${project.basedir}/${resource.directory}/webroot/swagger</outputDirectory>
          </fileset>
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```
Note: This will leave a zip file containing the swagger-ui resource files at `${project.build.directory}/swagger`. You may remove it using `maven-clean-plugin` and setting the value of phase to one which happens after `generate-resources`

* Add to `.gitignore`
```gitignore
  **/webroot/swagger/
```

* Create `src/main/resources/config/swagger/swagger-info.json` and put basic information about the service. Sample file
```json
{
  "prettyPrint": true,
  "openAPI": {
    "info": {
      "version": "1.0",
      "title": "some app",
      "description": "some rest application"
    }
  }
}
```

* Add to `pom.xml`
```xml
  <properties>
    <maven.swagger.plugin.version>2.2.0</maven.swagger.plugin.version>
    <maven.dependency.plugin.version>3.1.2</maven.dependency.plugin.version>
    <swagger.outputFileName>swagger</swagger.outputFileName>
    <swagger.configurationFilePath>${project.basedir}/src/main/resources/config/swagger/swagger-info.json</swagger.configurationFilePath>
    <swagger.outputPath>target/classes/webroot/swagger</swagger.outputPath>
    <swagger.resourcePackage>${project.groupId}.${project.artifactId}.rest</swagger.resourcePackage>
    <resource.directory>src/main/resources</resource.directory>
  </properties>

  <plugin>
    <groupId>io.swagger.core.v3</groupId>
    <artifactId>swagger-maven-plugin</artifactId>
    <version>${maven.swagger.plugin.version}</version>
    <configuration>
      <outputFileName>${swagger.outputFileName}</outputFileName>
      <configurationFilePath>${swagger.configurationFilePath}</configurationFilePath>
      <outputPath>${swagger.outputPath}</outputPath>
      <outputFormat>JSONANDYAML</outputFormat>
      <resourcePackages>
        <package>${swagger.resourcePackage}</package>
      </resourcePackages>
      <prettyPrint>true</prettyPrint>
    </configuration>
    <executions>
      <execution>
      <id>generate-swagger-docs</id>
      <phase>compile</phase>
      <goals>
        <goal>resolve</goal>
      </goals>
    </execution>
    </executions>
  </plugin>
```
Note: ${swagger.resourcePackage} should be the package which contains the rest API classes annotated with `@Path`

* Run `mvn compile` to copy swagger resources and create swagger specification file inside `target/classes/webroot/swagger`

* Start the application and go to `<hostname>:<port>/swagger` to view swagger UI

#### Timeouts

* Default timeout for each JAX-RS route is `20` seconds
* You can change the timeout of a particular JAX-RS resource by `@Timeout(<timeoutMillis>)` annotation on class or method
* Note: 503 status code is returned in case of timeouts

## References

* [Swagger Maven Plugin](https://github.com/swagger-api/swagger-core/tree/master/modules/swagger-maven-plugin)
* [Resteasy Vertx](https://docs.jboss.org/resteasy/docs/3.1.0.Final/userguide/html/RESTEasy_Embedded_Container.html)
* [Hibernate Validations](https://hibernate.org/validator/documentation/getting-started/)
* [JAX-RS](https://docs.oracle.com/javaee/6/tutorial/doc/gilik.html)
         
