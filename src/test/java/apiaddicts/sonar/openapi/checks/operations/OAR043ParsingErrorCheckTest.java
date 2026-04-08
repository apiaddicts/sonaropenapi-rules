/*
 * SonarQube OpenAPI Plugin
 * Copyright (C) 2018-2019 Societe Generale
 * vincent.girard-reydet AT socgen DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package apiaddicts.sonar.openapi.checks.operations;

import com.sonar.sslr.api.RecognitionException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.apiaddicts.apitools.dosonarapi.api.OpenApiFile;
import org.apiaddicts.apitools.dosonarapi.api.OpenApiVisitorContext;
import org.apiaddicts.apitools.dosonarapi.api.PreciseIssue;
import apiaddicts.sonar.openapi.I18nContext;
import apiaddicts.sonar.openapi.checks.BaseCheck;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OAR043ParsingErrorCheckTest {

  @BeforeClass
  public static void setupLang() throws Exception {
    I18nContext.setLang("en");
    Field field = BaseCheck.class.getDeclaredField("resourceBundle");
    field.setAccessible(true);
    field.set(null, null);
  }

  @Test
  public void reports_parsing_errors_no_cause_no_numbers() {
    OpenApiVisitorContext context = new OpenApiVisitorContext(new TestFile(),
        new RecognitionException(3, "Parsing exception message"));
    OAR043ParsingErrorCheck check = new OAR043ParsingErrorCheck();

    List<PreciseIssue> issues = check.scanFileForIssues(context);

    assertThat(issues).hasSize(1);
    assertThat(issues.get(0).primaryLocation().startLine()).isZero();
    assertThat(issues.get(0).primaryLocation().message()).isEqualTo("OAR043: Error parsing line 0 column null");
  }

  @Test
  public void reports_parsing_errors_with_numbers_in_message() {
    OpenApiVisitorContext context = new OpenApiVisitorContext(new TestFile(),
        new RecognitionException(0, "Error at line 5 column 10"));
    OAR043ParsingErrorCheck check = new OAR043ParsingErrorCheck();

    List<PreciseIssue> issues = check.scanFileForIssues(context);

    assertThat(issues).hasSize(1);
    assertThat(issues.get(0).primaryLocation().startLine()).isEqualTo(4);
  }

  @Test
  public void reports_parsing_errors_with_one_cause() {
    RuntimeException cause = new RuntimeException("Error at line 10 column 3");
    RecognitionException ex = new RecognitionException(0, "outer") {
      @Override public synchronized Throwable getCause() { return cause; }
    };
    OpenApiVisitorContext context = new OpenApiVisitorContext(new TestFile(), ex);
    OAR043ParsingErrorCheck check = new OAR043ParsingErrorCheck();

    List<PreciseIssue> issues = check.scanFileForIssues(context);

    assertThat(issues).hasSize(1);
    assertThat(issues.get(0).primaryLocation().startLine()).isEqualTo(8);
  }

  @Test
  public void reports_parsing_errors_with_two_causes() {
    RuntimeException deepCause = new RuntimeException("Error at line 15 column 5");
    RuntimeException shallowCause = new RuntimeException("wrapper") {
      @Override public synchronized Throwable getCause() { return deepCause; }
    };
    RecognitionException ex = new RecognitionException(0, "outer") {
      @Override public synchronized Throwable getCause() { return shallowCause; }
    };
    OpenApiVisitorContext context = new OpenApiVisitorContext(new TestFile(), ex);
    OAR043ParsingErrorCheck check = new OAR043ParsingErrorCheck();

    List<PreciseIssue> issues = check.scanFileForIssues(context);

    assertThat(issues).hasSize(1);
    assertThat(issues.get(0).primaryLocation().startLine()).isEqualTo(12);
  }

  @Test
  public void reports_no_issues_when_no_exception_and_no_issues() {
    OpenApiVisitorContext context = new OpenApiVisitorContext(new TestFile(), null);
    OAR043ParsingErrorCheck check = new OAR043ParsingErrorCheck();

    List<PreciseIssue> issues = check.scanFileForIssues(context);

    assertThat(issues).isEmpty();
  }

  private static class TestFile implements OpenApiFile {
    @Override public String content() { return null; }
    @Override public String fileName() { return null; }
  }
}

