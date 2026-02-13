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

import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.OpenApiVisitorContext;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.ValidationException;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.ValidationIssue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Rule(key = OAR043ParsingErrorCheck.CHECK_KEY)
public class OAR043ParsingErrorCheck extends BaseCheck {

  public static final String CHECK_KEY = "OAR043";

  private final Pattern PATTERN = Pattern.compile("\\d+");

  @Override
  public void scanFile(OpenApiVisitorContext context) {
    super.scanFile(context);
    RecognitionException parsingException = context.parsingException();

    if (parsingException instanceof ValidationException) {
      for (ValidationException issue : ((ValidationException) parsingException).getCauses()) {
        addIssue(CHECK_KEY, issue.formatMessage(), issue.getNode());
      }
    } else if (parsingException != null) {
      processRecognitionException(parsingException);
    } else {
      for (ValidationIssue issue : context.getIssues()) {
        addIssue(CHECK_KEY, issue.formatMessage(), issue.getNode());
      }
    }
  }

  private void processRecognitionException(RecognitionException parsingException) {
    Throwable cause = parsingException;
    int diff = 1;

    if (parsingException.getCause() != null) {
      cause = parsingException.getCause();
      diff = 2;
      if (cause.getCause() != null) {
        cause = cause.getCause();
        diff = 3;
      }
    }

    Matcher m = PATTERN.matcher(cause.getMessage());
    String line = m.find() ? m.group() : null;
    String column = m.find() ? m.group() : null;

    int l = (line == null || line.trim().isEmpty()) ? 0 : Integer.parseInt(line);
    l = Math.max(0, l - diff);

    addLineIssue(CHECK_KEY, translate("OAR043.error-parser", l, column), l);
  }
}
