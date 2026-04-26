# OpenAPI Rules Test Quick Reference Guide

## 🎯 Quick Start

### Run All Tests
```bash
mvn test
```

### Run Specific Rule Tests
```bash
# Test OAR001 (HTTPS Protocol)
mvn test -Dtest=OAR001MandatoryHttpsProtocolCheckTest

# Test OAR045 (Response Schema)
mvn test -Dtest=OAR045DefinedResponseCheckTest
```

### Run Tests by Category
```bash
# All security rules
mvn test -Dtest="*checks/security/*Test"

# All format rules
mvn test -Dtest="*checks/format/*Test"

# All operation rules
mvn test -Dtest="*checks/operations/*Test"
```

### Run Tests by OpenAPI Version
```bash
# All v3.0 tests (method names containing "V3")
mvn test -Dtest="*Test" -DincludePatterns=".*InV3.*"

# All v3.1 tests (method names containing "V31")
mvn test -Dtest="*Test" -DincludePatterns=".*InV31.*"

# All v3.2 tests (method names containing "V32")
mvn test -Dtest="*Test" -DincludePatterns=".*InV32.*"
```

---

## 📋 Test File Organization

### Finding Test Files
```
src/test/resources/checks/
├── v2/          # OpenAPI 2.0 (Swagger) tests
├── v3/          # OpenAPI 3.0.x tests
├── v31/         # OpenAPI 3.1.x tests
└── v32/         # OpenAPI 3.2.x tests
```

### Example Paths
```
src/test/resources/checks/v2/security/OAR001/
src/test/resources/checks/v3/security/OAR001/
src/test/resources/checks/v31/security/OAR001/
src/test/resources/checks/v32/security/OAR001/
```

---

## 🧪 Understanding Test Files

### Test File Format
Each rule directory contains test files with expected issues marked inline:

**YAML Format:**
```yaml
openapi: 3.0.0
paths:
  /example:
    get:
      responses:
        '200':
          description: OK
          # Noncompliant {{OAR001: HTTPS protocol is mandatory}}
```

**JSON Format:**
```json
{
  "openapi": "3.0.0",
  "paths": {
    "/example": {
      "get": {
        "responses": {
          "200": {
            "description": "OK"
            // Noncompliant {{OAR001: HTTPS protocol is mandatory}}
          }
        }
      }
    }
  }
}
```

### Comment Syntax
- `// Noncompliant {{RULE-CODE: message}}` - Marks an issue at this line
- `// Noncompliant@+1 {{...}}` - Issue on next line
- `// Noncompliant@-1 {{...}}` - Issue on previous line
- Multiple issues on same line: Use multiple `{{...}}` blocks

---

## 📝 Writing New Tests

### 1. Add Test Files
```bash
# Create test files for a new rule
mkdir -p src/test/resources/checks/{v2,v3,v31,v32}/security/OARxxx/

# Add test files
cp template.yaml src/test/resources/checks/v2/security/OARxxx/
cp template.json src/test/resources/checks/v2/security/OARxxx/
```

### 2. Update Test Class
```java
public class OARxxxCheckTest extends BaseCheckTest {
    
    @Before
    public void init() {
        ruleName = "OARxxx";
        check = new OARxxxCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
        v31Path = getV31Path("security");    // NEW
        v32Path = getV32Path("security");    // NEW
    }
    
    @Test
    public void verifyInV2Example() {
        verifyV2("example");
    }
    
    @Test
    public void verifyInV3Example() {
        verifyV3("example");
    }
    
    @Test
    public void verifyInV31Example() {      // NEW
        verifyV31("example");
    }
    
    @Test
    public void verifyInV32Example() {      // NEW
        verifyV32("example");
    }
}
```

### 3. Generate v3.1 and v3.2 Tests
```bash
# Copy v3 tests to v3.1 and update version
sed 's/openapi: 3\.0\.\d\+/openapi: 3.1.0/g' v3/file.yaml > v31/file.yaml

# Copy v3 tests to v3.2 and update version
sed 's/openapi: 3\.0\.\d\+/openapi: 3.2.0/g' v3/file.yaml > v32/file.yaml
```

---

## 🔍 Test Class Methods

### Available Methods in BaseCheckTest

```java
// Verify specific OpenAPI versions
protected void verifyV2(String file)
protected void verifyV3(String file)
protected void verifyV31(String file)    // NEW
protected void verifyV32(String file)    // NEW

// Get path for test files
protected String getV2Path(String category)
protected String getV3Path(String category)
protected String getV31Path(String category)    // NEW
protected String getV32Path(String category)    // NEW

// Assert rule properties
protected void assertRuleProperties(String title, RuleType type, 
                                   String severity, Set<String> tags)

// Assert parameter properties
protected void assertParameterProperties(String paramName, String defaultValue,
                                        RuleParamType type)

// Create tag set
protected Set<String> tags(String... tags)
```

---

## 📂 Test Categories

| Category | Count | Focus Area |
|----------|-------|-----------|
| apim | 6 rules | APIM & WSO2 specific rules |
| core | 8 rules | Core validation rules |
| examples | 2 rules | Example usage rules |
| format | 31 rules | Formatting & naming conventions |
| operations | 28 rules | HTTP operation rules |
| owasp | 2 rules | OWASP security rules |
| parameters | 11 rules | Parameter validation rules |
| regex | 1 rule | Regular expression patterns |
| resources | 15 rules | Resource/endpoint rules |
| schemas | 4 rules | Schema validation rules |
| security | 21 rules | Security-related rules |

---

## 🔗 Common Test Patterns

### Pattern 1: Required Field
```yaml
# Test: field must be defined
# Noncompliant {{OARxxx: Field 'x' is required}}
openapi: 3.0.0
```

### Pattern 2: Invalid Format
```yaml
# Test: value must match pattern
# Noncompliant {{OARxxx: Invalid format}}
openapi: 3.0.0
servers:
  - url: "http://example.com"  # Should be https
```

### Pattern 3: Multiple Issues
```yaml
# Noncompliant {{OARxxx: Issue 1}}
value1: invalid
# Noncompliant {{OARyyy: Issue 2}}
value2: invalid
```

### Pattern 4: Conditional Issue
```yaml
responses:
  '200':
    description: OK
    # Noncompliant {{OARxxx: Missing content for non-204 response}}
```

---

## 🐛 Debugging Tests

### Enable Logging
```bash
mvn test -X
```

### Run Single Test Method
```bash
mvn test -Dtest=OAR001MandatoryHttpsProtocolCheckTest#verifyInV31WithServers
```

### View Test Output
```bash
# Check target/surefire-reports/ for detailed test results
cat target/surefire-reports/apiaddicts.sonar.openapi.checks.security.OAR001MandatoryHttpsProtocolCheckTest.txt
```

### Common Issues
- **File not found**: Verify path in test file
- **Version mismatch**: Check OpenAPI version in test file
- **Pattern mismatch**: Verify `Noncompliant` comment format
- **Missing content**: Ensure both YAML and JSON files exist

---

## 📊 Test Coverage Goals

| Metric | Current | Target |
|--------|---------|--------|
| Rules with v2.0 tests | 103 | 100% |
| Rules with v3.0 tests | 104 | 100% |
| Rules with v3.1 tests | 105 | 100% |
| Rules with v3.2 tests | 104 | 100% |
| Multiple scenarios per rule | 2-5 | 3+ |
| Both YAML & JSON tests | ✓ | ✓ |

---

## 🚀 CI/CD Integration

Tests run automatically on:
- Every commit to develop branch
- Every pull request
- Scheduled nightly builds

Test execution time: ~5-10 minutes for full suite

---

## 📚 Additional Resources

- [OpenAPI Specification](https://spec.openapis.org/)
- [SonarQube Rules Development](https://docs.sonarqube.org/)
- [JUnit Testing Guide](https://junit.org/junit4/)
- [Project Documentation](./TEST_COVERAGE.md)

---

**Last Updated**: April 26, 2026
