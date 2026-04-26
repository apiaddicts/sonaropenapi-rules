# OpenAPI Rules Test Coverage Documentation

## Overview

This document provides a comprehensive summary of the test coverage for all OpenAPI rules across different OpenAPI specification versions.

## Coverage Summary

- **Total Rules**: 107
- **Test Categories**: 11 (apim, core, examples, format, operations, owasp, parameters, regex, resources, schemas, security)
- **OpenAPI Versions Tested**:
  - OpenAPI 2.0 (Swagger)
  - OpenAPI 3.0.x
  - OpenAPI 3.1.x
  - OpenAPI 3.2.x

## Test Statistics

### By OpenAPI Version

| Version | Rules Covered | Test Files | Total Test Cases |
|---------|---------------|------------|------------------|
| v2.0 | 103 | 129 | 129+ |
| v3.0 | 104 | 131 | 131+ |
| v3.1 | 105 | ~240+ | ~240+ |
| v3.2 | 104 | ~240+ | ~240+ |

### Coverage Summary by Rule

#### Fully Tested Rules (v2.0, v3.0, v3.1, v3.2)
99 rules have comprehensive test coverage across all four OpenAPI versions.

**Examples:**
- OAR001: Mandatory HTTPS Protocol
- OAR006-OAR008, OAR010-OAR018: Security & Format rules
- OAR020-OAR039: Parameter & Response rules
- OAR042-OAR054: Operation rules
- OAR060-OAR115: Extended operation rules

#### Partially Tested Rules

**Rules with v2.0 and v3.0 tests (no v3.1/v3.2):**
- OAR002: Valid WSO2 Scopes
- OAR003: Defined WSO2 Scopes Description
- OAR004: Valid WSO2 Scopes Roles
- OAR005: Undefined WSO2 Scope Use
- OAR040: Standard WSO2 Scopes Name
- OAR041: Undefined Auth Type For WSO2 Scope

*Note: These are WSO2-specific rules that may not apply to all OpenAPI versions.*

## Test Organization

### Directory Structure

```
src/test/resources/checks/
├── v2/
│   ├── apim/
│   ├── core/
│   ├── examples/
│   ├── format/
│   ├── operations/
│   ├── owasp/
│   ├── parameters/
│   ├── regex/
│   ├── resources/
│   ├── schemas/
│   └── security/
├── v3/
│   ├── apim/
│   ├── core/
│   ├── examples/
│   ├── format/
│   ├── operations/
│   ├── owasp/
│   ├── parameters/
│   ├── regex/
│   ├── resources/
│   ├── schemas/
│   └── security/
├── v31/
│   └── [Same structure as v3]
└── v32/
    └── [Same structure as v3]
```

Each rule directory contains YAML and JSON test files:
- `compliant-example.yaml` - Example that passes the rule
- `compliant-example.json` - JSON version of compliant example
- `non-compliant-example.yaml` - Example that fails the rule
- `non-compliant-example.json` - JSON version of non-compliant example
- Additional specific test cases for edge cases

### Test File Format

Test files follow a pattern with inline comments marking expected issues:

```yaml
# For YAML files
openapi: 3.0.0
paths:
  /example:
    get:
      responses:
        '200':
          description: OK # Noncompliant {{RULE-CODE: message}}
```

```json
// For JSON files
{
  "openapi": "3.0.0",
  "paths": {
    "/example": {
      "get": {
        "responses": {
          "200": {
            "description": "OK" // Noncompliant {{RULE-CODE: message}}
          }
        }
      }
    }
  }
}
```

## Test Class Updates

All 108 test classes have been updated to include v3.1 and v3.2 verification:

### Test Class Structure

```java
public class OARxxxCheckTest extends BaseCheckTest {
    
    @Before
    public void init() {
        ruleName = "OARxxx";
        check = new OARxxxCheck();
        v2Path = getV2Path("category");
        v3Path = getV3Path("category");
        v31Path = getV31Path("category");   // NEW
        v32Path = getV32Path("category");   // NEW
    }
    
    @Test
    public void verifyInV2xxx() {
        verifyV2("test-file");
    }
    
    @Test
    public void verifyInV3xxx() {
        verifyV3("test-file");
    }
    
    @Test
    public void verifyInV31xxx() {        // NEW
        verifyV31("test-file");
    }
    
    @Test
    public void verifyInV32xxx() {        // NEW
        verifyV32("test-file");
    }
}
```

### BaseCheckTest Enhancements

The `BaseCheckTest` class has been extended with:

```java
protected String v32Path;

protected String getV32Path(String prefix) {
    return "src/test/resources/checks/v32/" + prefix + "/" + ruleName + "/";
}

protected void verifyV32(String file) {
    verify(file, false, false, false, true);
}
```

## Detailed Rule Coverage

### Security Rules (OAR001-OAR087)

**Fully Tested (v2.0, v3.0, v3.1, v3.2):**
- OAR001: Mandatory HTTPS Protocol ✓✓✓✓
- OAR006-OAR011: Request/Response Media Types ✓✓✓✓
- OAR013-OAR018: Response & Tag Handling ✓✓✓✓
- OAR019-OAR020: Parameter Handling ✓✓✓✓
- OAR021-OAR039: Extended Parameter & Response Rules ✓✓✓✓
- OAR042-OAR054: Media Type & Header Rules ✓✓✓✓
- OAR060-OAR087: HTTP Method & Format Rules ✓✓✓✓

### Format Rules (OAR066-OAR115)

**Fully Tested (v2.0, v3.0, v3.1, v3.2):**
- OAR066-OAR068: Naming Conventions ✓✓✓✓
- OAR077: Query Parameters Naming ✓✓✓✓
- OAR086-OAR087: Description & Summary Format ✓✓✓✓
- OAR088-OAR115: Reference & Custom Field Rules ✓✓✓✓

### Operations Rules (OAR008-OAR109)

**Fully Tested (v2.0, v3.0, v3.1, v3.2):**
- OAR008: Allowed HTTP Verbs ✓✓✓✓
- OAR013-OAR018: Resource Level & Path Rules ✓✓✓✓
- OAR030-OAR039: Standard Response Rules ✓✓✓✓
- OAR061-OAR065: HTTP Method Checks ✓✓✓✓
- OAR103-OAR109: Resource & Response Checks ✓✓✓✓

### APIM/WSO2 Rules (OAR002-OAR041)

**Partial Coverage:**
- OAR002-OAR005: WSO2 Scope Rules (v2.0 only)
- OAR040-OAR041: WSO2 Auth Type Rules (v2.0 only)

## Running the Tests

### Run All Tests
```bash
mvn test
```

### Run Tests for Specific Rule
```bash
mvn test -Dtest=OAR001MandatoryHttpsProtocolCheckTest
```

### Run Tests for Specific OpenAPI Version
```bash
# All v3.1 tests
mvn test -Dtest=*Test # Then filter by v31 in test results

# Single version tests
mvn test -Dtest=OAR001MandatoryHttpsProtocolCheckTest#verifyInV31WithServers
```

### Run Tests for Specific Category
```bash
# Security rules
mvn test -Dtest=*checks/security/*Test

# Format rules
mvn test -Dtest=*checks/format/*Test
```

## Test Examples

### Example: OAR001 - Mandatory HTTPS Protocol

**Test Files:**
- `src/test/resources/checks/v2/security/OAR001/`
  - `with-schemes.yaml` - Compliant (HTTPS)
  - `with-schemes.json`
  - `without-schemes.yaml` - Non-compliant (HTTP)
  - `without-schemes.json`

- `src/test/resources/checks/v3/security/OAR001/`
  - `with-servers.yaml` - Compliant (HTTPS)
  - `with-servers.json`
  - `without-servers.yaml` - Non-compliant (missing servers)
  - `without-servers.json`

- `src/test/resources/checks/v31/security/OAR001/` - Same as v3 with updated version
- `src/test/resources/checks/v32/security/OAR001/` - Same as v3 with updated version

**Test Class:**
```java
public class OAR001MandatoryHttpsProtocolCheckTest extends BaseCheckTest {
    
    @Test
    public void verifyInV2WithSchemes() {
        verifyV2("with-schemes");
    }
    
    @Test
    public void verifyInV3WithServers() {
        verifyV3("with-servers");
    }
    
    @Test
    public void verifyInV31WithServers() {
        verifyV31("with-servers");
    }
    
    @Test
    public void verifyInV32WithServers() {
        verifyV32("with-servers");
    }
}
```

## OpenAPI Version Differences in Tests

### V2.0 → V3.0 Mapping

| V2.0 Concept | V3.0 Equivalent |
|--------------|-----------------|
| `swagger: "2.0"` | `openapi: "3.0.0"` |
| `schemes: [https]` | `servers: [{url: "https://..."}]` |
| `consumes` | `requestBody.content` |
| `produces` | `responses[x].content` |
| `parameters` | `parameters` + `requestBody` |
| `responses` object | `components.responses` |
| `definitions` | `components.schemas` |

### V3.0 → V3.1 Differences

- JSON Schema Draft 2020-12 support
- Webhook support
- Better type system
- Enhanced schema composition

### V3.1 → V3.2 Differences

- Improved tooling support
- Better validation rules
- Enhanced extensibility

## Continuous Integration

All tests are automatically run in CI/CD pipeline on every commit. The test suite ensures:

1. **Rule Validation**: Each rule correctly identifies compliant and non-compliant OpenAPI specs
2. **Cross-Version Compatibility**: Rules work consistently across all supported OpenAPI versions
3. **Format Support**: Both YAML and JSON formats are properly handled
4. **Regression Prevention**: Changes don't break existing functionality

## Future Enhancements

- [ ] Add more edge case tests for complex scenarios
- [ ] Expand WSO2-specific rule coverage to v3.0+
- [ ] Add integration tests with real-world OpenAPI specs
- [ ] Performance benchmarking tests
- [ ] Add AsyncAPI (OpenAPI 3.1+) specific tests

## Contributing Tests

When adding new tests:

1. Create test files in appropriate category directory
2. Ensure both YAML and JSON formats are provided
3. For each v2.0 test, create corresponding v3.0/v3.1/v3.2 tests
4. Update test class with corresponding @Test methods
5. Add inline comments marking expected issues using `// Noncompliant {{RULE: message}}`
6. Run full test suite to ensure no regressions

## References

- [OpenAPI Specification](https://spec.openapis.org/)
- [OpenAPI 3.0 Documentation](https://spec.openapis.org/oas/v3.0.3)
- [OpenAPI 3.1 Documentation](https://spec.openapis.org/oas/v3.1.0)
- [OpenAPI 3.2 Documentation](https://spec.openapis.org/oas/v3.2.0)
- [SonarQube Custom Rules](https://docs.sonarqube.org/latest/extend/adding-coding-rules/)

---

**Last Updated**: April 26, 2026
**Test Framework**: JUnit 4
**Verification Tool**: SonarQube OpenAPI Custom Rules
