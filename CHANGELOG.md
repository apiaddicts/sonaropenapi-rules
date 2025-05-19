# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.1] - 2025-01-29

### Added

#### - Adding new rules:
    - OAR012 - ParameterNamingConvention
    - OAR016 - NumericFormat
    - OAR018 - ResourcesByVerb
    - OAR039 - StandardResponseCodes
    - OAR042 - BasePath
    - OAR048 - AtMostOneBodyParameter
    - OAR052 - UndefinedNumericFormat
    - OAR053 - ResponseHeaders

## [1.2.0] - 2025-01-29

### Added

#### - A rule:
    - OAR113 - CustomField

### Fixed

#### - Improve test:
    - OAR068 - PascalCaseNamingConvention
    - OAR086 - DescriptionFormat
    - OAR094 - UseExamples

## [1.1.0] - 2024-09-02

### Added

#### - Some rules:

    - OAR060 - QueryParametersOptional
    - OAR061 - GetMethod
    - OAR062 - PostMethod
    - OAR063 - PutMethod
    - OAR064 - PatchMethod
    - OAR065 - DeleteMethod
    - OAR066 - SnakeCaseNamingConvention
    - OAR067 - CamelCaseNamingConvention
    - OAR068 - PascalCaseNamingConvention
    - OAR069 - PathParamAndQuery
    - OAR070 - BrokenAccessControl
    - OAR071 - GetQueryParamsDefined
    - OAR072 - NonOKModelResponse
    - OAR073 - RateLimit
    - OAR074 - NumericParameterIntegrity
    - OAR075 - StringParameterIntegrity
    - OAR076 - NumericFormat
    - OAR077 - ParametersInQuerySnakeCase
    - OAR078 - VerbsSecurity
    - OAR079 - PathParameter404
    - OAR080 - SecuritySchemas
    - OAR081 - PasswordFormat
    - OAR082 - BinaryOrByteFormat
    - OAR083 - ForbiddenQueryParams
    - OAR084 - ForbiddenQueryFormats
    - OAR085 - OpenAPIVersion
    - OAR086 - DescriptionFormat
    - OAR087 - SummaryFormat
    - OAR088 - RefParam
    - OAR089 - RefRequestBody
    - OAR090 - RefResponse
    - OAR091 - ParamOnlyRef
    - OAR092 - RequestBodyOnlyRef
    - OAR093 - ResponseOnlyRef
    - OAR094 - UseExamples
    - OAR096 - ForbiddenResponse
    - OAR097 - ShortBasePath
    - OAR098 - LongBasePath
    - OAR099 - ApiPrefixBasePath
    - OAR100 - LastPartBasePath
    - OAR101 - FirstPartBasePath
    - OAR102 - SecondPartBasePath
    - OAR103 - ResourcesByGetVerb
    - OAR104 - ResourcesByPostVerb
    - OAR105 - ResourcesByPutVerb
    - OAR106 - ResourcesByPatchVerb
    - OAR107 - ResourcesByDeleteVerb
    - OAR108 - SchemaValidator
    - OAR109 - ForbiddenInternalServerError
    - OAR110 - LicenseInformation
    - OAR111 - ContactInformation
    - OAR112 - RuleTemplate
 
### - Support for OpenAPI 3.1

All the rules had been updated to be fully compatible with OpenAPI 3.1

### - Make your own rule

Now, it is possible to make your own custom rule by using OAR112 - RuleTemplate
