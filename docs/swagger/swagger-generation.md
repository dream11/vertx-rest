# OpenAPI Specification generation

#### How to create Swagger Specification/UI
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
Note: `${swagger.resourcePackage}` should be the package which contains the rest API classes annotated with `@Path`

* Run `mvn compile` to copy swagger resources and create swagger specification file inside `target/classes/webroot/swagger`

* Start the application and go to `<hostname>:<port>/swagger` to view swagger UI
