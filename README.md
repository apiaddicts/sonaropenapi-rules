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
        <!-- Optional, Path that contains the v2 files, Default is /openapi/v2/** -->
        <sonar.openapi.path.v2>v2/*</sonar.openapi.path.v2>
        <!-- Required, Path that contains the v3 files, Default is /openapi/v3/** -->
        <sonar.openapi.path.v3>v3/*</sonar.openapi.path.v3>
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

# OpenAPI-specific properties go here. Default is /openapi/v2/** and /openapi/v3/**
sonar.openapi.path.v2=v2/*
sonar.openapi.path.v3=v3/*
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