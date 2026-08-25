# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.6.0-beta-1] - 2026-08-25

### Fixed

- OAR002 - Rewrote to validate the full `x-wso2-scopes` definition (null/empty container and missing/null/blank or empty-array/object `name`/`key`/`roles`) via new `apq-wso2-scopes-valid`.

## [1.5.1] - 2026-08-25

### Changed

- OAR025 - The shared `apq-collection-query-param-required` function now also validates the parameter type for OAR025, keyed by rule code; when `$limit` is present but its type is not `integer`, a distinct type message is emitted.
- OAR022 - OrderbyParameterCheck - Now only applies to paginated collections (operations declaring a 206 response); added `no-pagination` fixtures/tests and corrected the HTML docs (real defaults + 206 condition).
- OAR025 - LimitParameterCheck - Same 206 gating for `$limit`; added `no-pagination` fixtures/tests and corrected the HTML docs.
- OAR020 - ExpandParameterCheck - Corrected the HTML docs to the real defaults (`/me,/health,/ping,/status`, `/exclude`).
- OAR021 - ExcludeParameterCheck - Corrected the HTML docs to the real defaults (`/me,/health,/ping,/status`, `/exclude`).

### Fixed

- OAR035 - AuthorizationResponses - Honor an operation-level `security: []` explicit opt-out: the operation is unsecured, so no 401 is required even under global security. Added `security-opt-out` (v3) fixtures and test.
- OAR096 - ForbiddenResponses - Same `security: []` opt-out fix in the shared `AbstractSecurityResponseCheck`: no 403 required for opted-out operations. Added `security-opt-out` (v3) fixtures and test.
- OAR014 / OAR015 - ResourceLevel - Issue message now interpolates the configured level values (min-level/max-level for OAR014, max-level-allowed for OAR015).
- OAR004 / OAR040 - Wso2Scopes - Issue message now includes the configured `pattern` (passed through `AbstractPatternWso2ScopesCheck`).
- OAR038 - StandardCreateResponse - Issue message now interpolates the configured `data-property` instead of the hardcoded `data`.
- OAR082 - BinaryOrByteFormat - Issue message now shows the configured `fields-to-apply`.
- OAR085 - OpenAPIVersion - Issue message now shows the configured `valid-versions`.
- OAR037 - StringFormat - Fixed false positive on string schemas constrained by `enum`. The check only inspected `format`/`pattern`, so a string with a non-empty `enum` and no `format` was wrongly reported even though the `enum` already constrains the allowed values. When no `format` is declared, a non-empty `enum` now satisfies the rule (like a valid `pattern`); a present-but-invalid `format` still fires even when an `enum` is declared.
- OAR044 - MediaTypeCheck - Media type parameters now follow RFC 9110 (charset without space, other parameter names, multiple parameters); type/subtype can no longer start with `.`.


## [1.5.1-beta-4] - 2026-08-24

### Changed

- OAR025 - The shared `apq-collection-query-param-required` function now also validates the parameter type for OAR025, keyed by rule code; when `$limit` is present but its type is not `integer`, a distinct type message is emitted.


## [1.5.1-beta-3] - 2026-08-14

### Changed

- OAR022 - OrderbyParameterCheck - Now only applies to paginated collections (operations declaring a 206 response); added `no-pagination` fixtures/tests and corrected the HTML docs (real defaults + 206 condition).
- OAR025 - LimitParameterCheck - Same 206 gating for `$limit`; added `no-pagination` fixtures/tests and corrected the HTML docs.
- OAR020 - ExpandParameterCheck - Corrected the HTML docs to the real defaults (`/me,/health,/ping,/status`, `/exclude`).
- OAR021 - ExcludeParameterCheck - Corrected the HTML docs to the real defaults (`/me,/health,/ping,/status`, `/exclude`).


## [1.5.1-beta-2] - 2026-08-12

### Fixed

- OAR035 - AuthorizationResponses - Honor an operation-level `security: []` explicit opt-out: the operation is unsecured, so no 401 is required even under global security. Added `security-opt-out` (v3) fixtures and test.
- OAR096 - ForbiddenResponses - Same `security: []` opt-out fix in the shared `AbstractSecurityResponseCheck`: no 403 required for opted-out operations. Added `security-opt-out` (v3) fixtures and test.


## [1.5.1-beta-1] - 2026-08-04

### Fixed

- OAR014 / OAR015 - ResourceLevel - Issue message now interpolates the configured level values (min-level/max-level for OAR014, max-level-allowed for OAR015).
- OAR004 / OAR040 - Wso2Scopes - Issue message now includes the configured `pattern` (passed through `AbstractPatternWso2ScopesCheck`).
- OAR038 - StandardCreateResponse - Issue message now interpolates the configured `data-property` instead of the hardcoded `data`.
- OAR082 - BinaryOrByteFormat - Issue message now shows the configured `fields-to-apply`.
- OAR085 - OpenAPIVersion - Issue message now shows the configured `valid-versions`.
- OAR037 - StringFormat - Fixed false positive on string schemas constrained by `enum`. The check only inspected `format`/`pattern`, so a string with a non-empty `enum` and no `format` was wrongly reported even though the `enum` already constrains the allowed values. When no `format` is declared, a non-empty `enum` now satisfies the rule (like a valid `pattern`); a present-but-invalid `format` still fires even when an `enum` is declared.
- OAR044 - MediaTypeCheck - Media type parameters now follow RFC 9110 (charset without space, other parameter names, multiple parameters); type/subtype can no longer start with `.`.


## [1.5.0] - 2026-07-28

### Added

- OAR022 - OrderbyParameterCheck - Added `single-resource` test cases (v2, v3, v31, v32) verifying that paths ending with a path parameter (e.g. `/examples/{id}`) are correctly excluded by `applyToParameterizedPaths = false`.
- OAR025 - LimitParameterCheck - Added `single-resource` test cases (v2, v3, v31, v32) verifying that paths ending with a path parameter (e.g. `/examples/{id}`) are correctly excluded by `applyToParameterizedPaths = false`.
- OAR031 - ExamplesCheck - Per-level configuration via rule parameters `validate-response`, `validate-request-body`, `validate-parameter` and `validate-property` (all `true` by default); each level can be disabled independently.

### Fixed

- OAR017 - ResourcePathCheck - Added `delete` to the `exclude_patterns` default (now `get,me,search,delete`); paths ending with `/delete` (e.g. `/orders/delete`, `/orders/{orderId}/delete`) are now treated as pseudo-parameters and no longer trigger the alternation rule.
- OAR020 - ExpandParameterCheck - Fixed `verifyInV2PathEndingWithParam` test method that was incorrectly calling `verifyV3("with-param")` instead of `verifyV2("with-param")`; the Swagger 2.0 `with-param` test fixtures are now correctly exercised in v2 mode.
- OAR021 - ExcludeParameterCheck - Fixed `verifyInV2PathEndingWithParam` test, now correctly calls `verifyV2("with-param")`.
- OAR044 - MediaTypeCheck - Fixed `MEDIA_RANGE_PATTERN` to allow `*/*` (full wildcard) as a valid OAP3 media range; the type component now accepts `*` in addition to RFC 6838 type names. Added test coverage for vendor-specific types (`application/vnd.ms-excel`, `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`, `application/ld+json`, `application/vnd.github+json`).
- OAR004 - ValidWso2ScopesRoles - Fixed a field-shadowing bug in the shared base class that made the `pattern` parameter have no effect.
- OAR014 - ResourceLevelWithinNonSuggestedRange - `matchLevel` ignored `maxLevel` entirely; the parameter is now applied.
- OAR019 - SelectParameterCheck - Added real support for `paths` and `pathValidationStrategy`, and re-added the `parameterName` parameter (removed in an earlier refactor).
- OAR020 - ExpandParameterCheck - Removed hardcoded path-exclusion logic that bypassed the configurable `paths` parameter; re-added `parameterName`.
- OAR021 - ExcludeParameterCheck - Removed hardcoded path-exclusion logic that bypassed the configurable `paths` parameter; re-added `parameterName`.
- OAR038 - StandardCreateResponse - The `dataNode` parameter was never read; the check always used its default value instead.
- OAR040 - StandardWso2ScopesName - Fixed the same field-shadowing bug as OAR004.
- OAR082 - BinaryOrByteFormat - `fields-to-apply` was read before Sonar injected its configured value, so the parameter had no effect.
- OAR085 - OpenAPIVersion - `valid-versions` was read before Sonar injected its configured value, so the parameter had no effect.

### Changed

- OAR019, OAR020, OAR021 - `paths` now takes plain path segments (e.g. `/status`) instead of a regular expression. Default excluded paths (`/me`, `/health`, `/ping`, `/status`) are now matched as real path segments instead of a loose substring, so a path like `/subscription-status-reports` is no longer wrongly excluded.
- OAR037 - StringFormatCheck - Reclassified as a security rule (`VULNERABILITY`, tag `safety`, keeping its existing `format` rule group/package). String schemas must now declare a valid `format`, or — when no `format` is declared — a non-empty, syntactically valid `pattern`; schemas with neither a valid `format` nor a valid `pattern` are reported.
- OAR037 - StringFormatCheck - Rule no longer fires when a string schema omits the `format` field entirely; it only fires when `format` is present but not a recognized value.
- OAR031 - ExamplesCheck - Examples are now validated as four **independent** levels (response, request body, parameter, property). The response/request-body/parameter levels require an example declared at the media-type or schema **root** (non-recursive); examples nested inside schema properties no longer satisfy them. Aligns OAR031 with the Spectral ruleset (identical findings on the same document) and is stricter than before, so existing specs may surface new findings.

## [1.5.0-beta-4] - 2026-07-14

### Fixed

- OAR004 - ValidWso2ScopesRoles - Fixed a field-shadowing bug in the shared base class that made the `pattern` parameter have no effect.
- OAR014 - ResourceLevelWithinNonSuggestedRange - `matchLevel` ignored `maxLevel` entirely; the parameter is now applied.
- OAR019 - SelectParameterCheck - Added real support for `paths` and `pathValidationStrategy`, and re-added the `parameterName` parameter (removed in an earlier refactor).
- OAR020 - ExpandParameterCheck - Removed hardcoded path-exclusion logic that bypassed the configurable `paths` parameter; re-added `parameterName`.
- OAR021 - ExcludeParameterCheck - Removed hardcoded path-exclusion logic that bypassed the configurable `paths` parameter; re-added `parameterName`.
- OAR038 - StandardCreateResponse - The `dataNode` parameter was never read; the check always used its default value instead.
- OAR040 - StandardWso2ScopesName - Fixed the same field-shadowing bug as OAR004.
- OAR082 - BinaryOrByteFormat - `fields-to-apply` was read before Sonar injected its configured value, so the parameter had no effect.
- OAR085 - OpenAPIVersion - `valid-versions` was read before Sonar injected its configured value, so the parameter had no effect.

### Changed

- OAR019, OAR020, OAR021 - `paths` now takes plain path segments (e.g. `/status`) instead of a regular expression. Default excluded paths (`/me`, `/health`, `/ping`, `/status`) are now matched as real path segments instead of a loose substring, so a path like `/subscription-status-reports` is no longer wrongly excluded.

## [1.5.0-beta-3] - 2026-07-08

### Changed

- OAR037 - StringFormatCheck - Reclassified as a security rule (`VULNERABILITY`, tag `safety`, keeping its existing `format` rule group/package). String schemas must now declare a valid `format`, or — when no `format` is declared — a non-empty, syntactically valid `pattern`; schemas with neither a valid `format` nor a valid `pattern` are reported.

## [1.5.0-beta-2] - 2026-06-24

### Changed

- OAR037 - StringFormatCheck - Rule no longer fires when a string schema omits the `format` field entirely; it only fires when `format` is present but not a recognized value.

### Fixed

- OAR017 - ResourcePathCheck - Added `delete` to the `exclude_patterns` default (now `get,me,search,delete`); paths ending with `/delete` (e.g. `/orders/delete`, `/orders/{orderId}/delete`) are now treated as pseudo-parameters and no longer trigger the alternation rule.
- OAR020 - ExpandParameterCheck - Fixed `verifyInV2PathEndingWithParam` test method that was incorrectly calling `verifyV3("with-param")` instead of `verifyV2("with-param")`; the Swagger 2.0 `with-param` test fixtures are now correctly exercised in v2 mode.
- OAR021 - ExcludeParameterCheck - Fixed `verifyInV2PathEndingWithParam` test, now correctly calls `verifyV2("with-param")`.
- OAR044 - MediaTypeCheck - Fixed `MEDIA_RANGE_PATTERN` to allow `*/*` (full wildcard) as a valid OAP3 media range; the type component now accepts `*` in addition to RFC 6838 type names. Added test coverage for vendor-specific types (`application/vnd.ms-excel`, `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`, `application/ld+json`, `application/vnd.github+json`).

### Added

- OAR022 - OrderbyParameterCheck - Added `single-resource` test cases (v2, v3, v31, v32) verifying that paths ending with a path parameter (e.g. `/examples/{id}`) are correctly excluded by `applyToParameterizedPaths = false`.
- OAR025 - LimitParameterCheck - Added `single-resource` test cases (v2, v3, v31, v32) verifying that paths ending with a path parameter (e.g. `/examples/{id}`) are correctly excluded by `applyToParameterizedPaths = false`.

## [1.5.0-beta-1] - 2026-06-15

### Added

- OAR031 - ExamplesCheck - Per-level configuration via rule parameters `validate-response`, `validate-request-body`, `validate-parameter` and `validate-property` (all `true` by default); each level can be disabled independently.

### Changed

- OAR031 - ExamplesCheck - Examples are now validated as four **independent** levels (response, request body, parameter, property). The response/request-body/parameter levels require an example declared at the media-type or schema **root** (non-recursive); examples nested inside schema properties no longer satisfy them. Aligns OAR031 with the Spectral ruleset (identical findings on the same document) and is stricter than before, so existing specs may surface new findings.


## [1.4.1] - 2026-06-04

### Added

- Add OpenAPI language support without YAML and JSON conflicts.

### Changed

- Bump plugin version to `1.4.1`.
- Update `sonaropenapi.version` to `1.2.1`.
- Reference `openapi-front-end` and `openapi-test-tools` dependencies via `${sonaropenapi.version}` property instead of hardcoded version.

### Fixed

- OAR004 - ValidWso2ScopesRoles - Fixed false negative where `roles` defined as a YAML/JSON array were not validated element by element. Updated `AbstractPatternWso2ScopesCheck.visitScope()` to iterate array elements via `fieldNode.elements()` and validate each one individually. Added test fixtures for array roles in v2, v3, v31 and v32 formats.
- OAR014 - ResourceLevelWithinNonSuggestedRange - Removed upper bound threshold: rule now fires for all depths ≥ 4 (previously only fired for depths 4–5), aligning with Spectral behavior. Updated v2 test fixtures to mark depth-6 paths as noncompliant.
- OAR015 - ResourceLevelMaxAllowed - Updated depth calculation algorithm in `AbstractResourceLevelCheck.matchLevel(String path)` to count only literal segments, explicitly excluding path parameters (e.g. `{customerId}`) and `/me` segments — matching Spectral's algorithm exactly. Previously used a `pathParts − literalParamPairs` formula that produced different results for paths starting with parameters, consecutive parameters, or containing `/me`.
- OAR020 - ExpandParameterCheck - Fixed false negative where GET operations on non-`/examples` paths (e.g. `/pets`, `/orders`) without a `parameters` block were not reported. Changed default path strategy from include-only `/examples` to exclude-all (empty exclude list), so the rule now applies to all collection GET endpoints. Added `/me` path exclusion and health-check path exclusion (`status`, `health`, `ping`) in `visitNode`, aligning with Spectral's filter. Added `without-parameters` test cases for v2, v3, v31 and v32.
- OAR021 - ExcludeParameterCheck - Same fix as OAR020 applied for `$exclude` parameter. Changed default path strategy to exclude-all, added `/me` and health-check exclusions, added `without-parameters` test cases.
- OAR028 - FilterParameterCheck - Rewritten to extend `AbstractQueryParameterCheck`. Fires exactly once per GET operation when `$filter` query parameter is absent; does not fire if `$filter` is present alongside other parameters; resolves `$filter` referenced via `$ref` to components. Covers ALL collection GET endpoints except `/me` paths, terminal `/{id}` paths and health-check paths (`status`, `health`, `ping`).
- OAR037 - StringFormatCheck - Fixed false negative where string schemas without a `format` field were not reported. Updated `isInvalidString` to also fire when `format == null`.
- OAR038 - StandardCreateResponseCheck - POST 201 responses must have a schema whose properties are named `data` or `error`, each with at least one sub-property. Fires with a distinct message when the property name is invalid vs. when sub-properties are missing.
- OAR066 - SnakeCaseNamingConventionCheck - Fixed false positives on industry-standard property name prefixes. Skip properties whose names start with `@` or `x-`.
- OAR073 - RateLimitCheck - Extended default excluded paths from `/status, /health-check` to `/status, /health, /health-check, /ping, /liveness, /readiness` in `DEFAULT_PATHS`.

## [1.4.1-beta-5] - 2026-06-02

### Fixed

- OAR028 - FilterParameterCheck - Rewritten to extend `AbstractQueryParameterCheck`. Fires exactly once per GET operation when `$filter` query parameter is absent; does not fire if `$filter` is present alongside other parameters; resolves `$filter` referenced via `$ref` to components. Covers ALL collection GET endpoints except `/me` paths, terminal `/{id}` paths and health-check paths (`status`, `health`, `ping`).

## [1.4.1-beta-4] - 2026-05-31

### Fixed

- OAR020 - ExpandParameterCheck - Fixed false negative where GET operations on non-`/examples` paths (e.g. `/pets`, `/orders`) without a `parameters` block were not reported. Changed default path strategy from include-only `/examples` to exclude-all (empty exclude list), so the rule now applies to all collection GET endpoints. Added `/me` path exclusion and health-check path exclusion (`status`, `health`, `ping`) in `visitNode`, aligning with Spectral's filter. Added `without-parameters` test cases for v2, v3, v31 and v32.
- OAR021 - ExcludeParameterCheck - Same fix as OAR020 applied for `$exclude` parameter. Changed default path strategy to exclude-all, added `/me` and health-check exclusions, added `without-parameters` test cases.
- OAR037 - StringFormatCheck - Fixed false negative where string schemas without a `format` field were not reported. Updated `isInvalidString` to also fire when `format == null`.
- OAR038 - StandardCreateResponseCheck - POST 201 responses must have a schema whose properties are named `data` or `error`, each with at least one sub-property. Fires with a distinct message when the property name is invalid vs. when sub-properties are missing.
- OAR066 - SnakeCaseNamingConventionCheck - Fixed false positives on industry-standard property name prefixes. Skip properties whose names start with `@` or `x-`.
- OAR073 - RateLimitCheck - Extended default excluded paths from `/status, /health-check` to `/status, /health, /health-check, /ping, /liveness, /readiness` in `DEFAULT_PATHS`.

## [1.4.1-beta-3] - 2026-05-29

### Fixed

- OAR004 - ValidWso2ScopesRoles - Fixed false negative where `roles` defined as a YAML/JSON array were not validated element by element. Updated `AbstractPatternWso2ScopesCheck.visitScope()` to iterate array elements via `fieldNode.elements()` and validate each one individually. Added test fixtures for array roles in v2, v3, v31 and v32 formats.
- OAR014 - ResourceLevelWithinNonSuggestedRange - Removed upper bound threshold: rule now fires for all depths ≥ 4 (previously only fired for depths 4–5), aligning with Spectral behavior. Updated v2 test fixtures to mark depth-6 paths as noncompliant.
- OAR015 - ResourceLevelMaxAllowed - Updated depth calculation algorithm in `AbstractResourceLevelCheck.matchLevel(String path)` to count only literal segments, explicitly excluding path parameters (e.g. `{customerId}`) and `/me` segments — matching Spectral's algorithm exactly. Previously used a `pathParts − literalParamPairs` formula that produced different results for paths starting with parameters, consecutive parameters, or containing `/me`.

## [1.4.1-beta-2] - 2026-05-28

### Added

- Add OpenAPI language support without YAML and JSON conflicts.

## [1.4.1-beta-1] - 2026-05-26

### Changed

- Bump plugin version to `1.4.1-beta-1`.
- Update `sonaropenapi.version` to `1.2.1-beta-1`.
- Reference `openapi-front-end` and `openapi-test-tools` dependencies via `${sonaropenapi.version}` property instead of hardcoded version.

## [1.4.0] - 2026-05-22

### Security

- Upgrade `org.json:json` to `20231013` to fix CVE vulnerabilities
- Upgrade `jackson-dataformat-yaml` from 2.13.3 to 2.18.6 to fix CVE alerts.
- Upgrade `assertj-core` from 3.22.0 to 3.27.7 to fix XXE vulnerability.

### Changed

- Move sonar organization config to github action

### Added

#### Now, support for OpenAPI 3.2 is included. These are some of the new changes:

- All existing rules (OAR001 - OAR115) have been updated and validated for compatibility with the OpenAPI 3.2 specification.
- Added a comprehensive set of test cases for every rule to ensure correct behavior and validation under OpenAPI 3.2 schemas.
- Enhanced the engine to support new 3.2 structural changes, including updated reference handling and metadata fields.

### Fixed

- Resolve language suffix conflict between the plugin's custom YAML/JSON support and SonarQube's built-in language detection.
- OAR020 - ExpandParameterCheck: rule now explicitly requires `$expand` (with `$` prefix) as the query parameter name, rejecting `expand` without prefix, aligning with Spectral behavior.
- OAR021 - ExcludeParameterCheck: rule now explicitly requires `$exclude` (with `$` prefix) as the query parameter name, rejecting `exclude` without prefix, aligning with Spectral behavior.
- OAR028 - FilterParameterCheck: rule now only evaluates `query` parameters; header, path and cookie parameters are ignored, aligning with Spectral behavior.
- OAR051 - DescriptionDiffersSummaryCheck: rule now evaluates all HTTP methods (GET, POST, PUT, PATCH, DELETE), not only GET, aligning with Spectral behavior.
- OAR066 - SnakeCaseNamingConventionCheck: rule now recursively validates nested schema property names at all depth levels, aligning with Spectral behavior.


## [1.3.7] - 2026-05-18

### Fixed
  - Add Spanish documentation for OAR048

## [1.3.6] - 2026-05-05

### Fixed
    - External `$ref` tests no longer require outbound internet access. Fixtures
      are now served by a local HTTP server (`ExternalRefHttpServer`) on
      `http://localhost:18089`, started in `BaseCheckTest`. Affected tests:
      OAR031 (v2/v3), OAR094, OAR068, OAR086.

## [1.3.5] - 2026-04-08

### Fixed
    - OAR102 - SecondPartBasePathCheck Test
    - OAR101 - FirstPartBasePathCheck Test
    - OAR034 - StandardPagedResponseSchemaCheck Test
    - OAR029 - StandardResponseSchemaCheck Test
    - OAR083 - ForbiddenQueryParamsCheck Test
    - OAR084 - ForbiddenFormatsInQueryCheck Test
    - OAR043 - ParsingErrorCheck Test
    - OAR028 - FilterParameterCheck Test
    - OAR073 - RateLimitCheck Test
    - OAR079 - PathParameter404Check Test

    - AbstractSchemaCheck
    - AbstractForbiddenQueryCheck
    - AbstractPathResponseCheck
    - VerbPathMatcher

## [1.3.4] - 2026-04-01

### Fixed
    - OAR029 - StandardResponseSchemaCheck Test
    - OAR080 - SecuritySchemasCheck Test
    - OAR112 - RegexCheck Test

    - OpenAPICustomPlugin Test
    - OpenAPICustomProfileDefinition Test
    - OpenAPICustomRuleRepository Test
    - OpenAPICustomRulesDefinition Test

## [1.3.3] - 2026-03-20

### Fixed
    - OAR113 - CustomField Documentation
    - OAR114 - HttpResponseHeaders Documentation
    - OAR115 - VerifyRequiredFields Documentation

## [1.3.2] - 2026-03-05

### Fixed
    - OAR031 - Examples

## [1.3.1] - 2026-02-19

### Changed
    - Resolved all SonarCloud issues

## [1.3.0] - 2026-01-05

### Changed
    - OAR104 - ResourcesByPostVerbCheck

### Fixed
    - OAR019 - SelectParameterCheck
    - OAR020 - ExpandParameterCheck

## [1.2.5] - 2025-12-31

### Fixed
    - OAR021 - ExludeParameterCheck

## [1.2.4] - 2025-12-15

### Fixed
    - OAR022 - OrderbyParameterCheck
    - OAR006 - UndefinedRequestMediaTypeCheck
    - OAR009 - DefaultRequestMediaTypeCheck

## [1.2.3] - 2025-11-17

### Fixed
    - OAR031 - ExamplesCheck

## [1.2.2] - 2025-11-04

### Fixed
    - OAR017 - ResourcePathCheck
    - OAR025 - Limit parameter check
    - OAR034 - StandardPagedResponseSchemaCheck
    - OAR041 - UndefinedAuthTypeForWso2ScopeCheck
    - OAR043 - ParsingErrorCheck

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
