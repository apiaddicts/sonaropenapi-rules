<p align="center">
	<a href="https://apiaddicts.org/">
	  <img src="https://apiaddicts-web.s3.eu-west-1.amazonaws.com/wp-content/uploads/2022/03/17155736/cropped-APIAddicts-logotipo_rojo-2048x523.png">
	</a>
</p>

## Contributors
### CloudAPPi
CloudAppi is one leader in APIs in global word. See the CloudAPPi Services

### Madrid Digital
Madrid Digital is a public administration in Spain. See the Comunidad de Madrid website

## Configure scanner

### Maven plugin

#### Configure properties

In `pom.xml` configure:

````xml
    <properties>
        <!-- Optional, When is set only the language specified is analyzed -->
        <sonar.language>openapi</sonar.language>
        <!-- Optional, Default value is src/main,pom.xml -->
        <sonar.sources>.</sonar.sources>
    </properties>
````

#### Run scanner

`mvn sonar:sonar -Dsonar.host.url=<HOST> -Dsonar.login=<KEY>`

### External `sonar-scanner`

#### Install `sonar-scanner`

Download the `sonar-scanner` from https://docs.sonarqube.org/latest/analysis/scan/sonarscanner/ and make it accessible.

#### Configure properties

In `sonar-project.properties` (file in root project folder) configure:

````properties
# must be unique in a given SonarQube instance
sonar.projectKey=test:test
# this is the name and version displayed in the SonarQube UI. Was mandatory prior to SonarQube 6.1.
sonar.projectName=OpenAPI plugin tests
sonar.projectVersion=1.0-SNAPSHOT

# Path is relative to the sonar-project.properties file. Replace "\" by "/" on Windows.
# This property is optional if sonar.modules is set.
sonar.sources=.

# Encoding of the source code. Default is default system encoding
sonar.sourceEncoding=UTF-8
# Select the language to use for analysis
sonar.language=openapi
````

#### Run scanner

`sonar-scanner -Dsonar.host.url=<HOST> -Dsonar.login=<KEY>`

## Compatibility

This plugin is supported by SonarQube versions greater or equal to `6.7.4`

### Explicit compatibility versions tested

| Version |
|---------|
| `6.7.4` |
| `7.9-community` |
| `8.3-community` |
