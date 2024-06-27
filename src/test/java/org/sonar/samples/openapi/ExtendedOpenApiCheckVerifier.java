package org.sonar.samples.openapi;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.Ordering;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.junit.Assert;
import org.apiaddicts.apitools.dosonarapi.TestIssue;
import org.apiaddicts.apitools.dosonarapi.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class is equals to {@link org.sonar.openapi.OpenApiCheckVerifier} (the default verifier) but with a subtle difference,
 * it validates multiple issues in the same line, the method modified is {@link org.sonar.openapi.OpenApiCheckVerifier#collectExpectedIssue(Trivia trivia)}
 */
public class ExtendedOpenApiCheckVerifier {
    private List<TestIssue> expectedIssues = new ArrayList<TestIssue>();

    public ExtendedOpenApiCheckVerifier() {
    }

    public static List<PreciseIssue> scanFileForIssues(File file, OpenApiCheck check, boolean isV2, boolean isV31) {
        return check.scanFileForIssues(TestOpenApiVisitorRunner.createContext(file, isV2, isV31));
    }

    public static void verify(String path, OpenApiCheck check, boolean isV2, boolean isV31) {
        ExtendedOpenApiCheckVerifier verifier = new ExtendedOpenApiCheckVerifier();
        OpenApiVisitor collector = new ExtendedOpenApiCheckVerifier.ExpectedIssueCollector(verifier);
        File file = new File(path);
        TestOpenApiVisitorRunner.scanFileForComments(file, isV2, isV31, new OpenApiVisitor[]{collector});
        Iterator<PreciseIssue> actualIssues = getActualIssues(file, check, isV2, isV31);
        verifier.checkIssues(actualIssues);
        if (actualIssues.hasNext()) {
            PreciseIssue issue = (PreciseIssue) actualIssues.next();
            throw new AssertionError("Unexpected issue at line " + line(issue) + ": \"" + issue.primaryLocation().message() + "\"");
        }
    }

    private static int line(PreciseIssue issue) {
        return issue.primaryLocation().startLine();
    }

    private void checkIssues(Iterator<PreciseIssue> actualIssues) {
        Iterator<TestIssue> var2 = this.expectedIssues.iterator();

        while (var2.hasNext()) {
            TestIssue expected = (TestIssue) var2.next();
            if (!actualIssues.hasNext()) {
                throw new AssertionError("Missing issue at line " + expected.line());
            }

            this.verifyIssue(expected, (PreciseIssue) actualIssues.next());
        }

    }

    private void verifyIssue(TestIssue expected, PreciseIssue actual) {
        if (line(actual) > expected.line()) {
            Assert.fail("Missing issue at line " + expected.line());
        }

        if (line(actual) < expected.line()) {
            Assert.fail("Unexpected issue at line " + line(actual) + ": \"" + actual.primaryLocation().message() + "\"");
        }

        if (expected.message() != null) {
            ((AbstractCharSequenceAssert<?, ?>) Assertions.assertThat(actual.primaryLocation().message()).as("Bad message at line " + expected.line(), new Object[0])).isEqualTo(expected.message());
        }

        if (expected.effortToFix() != null) {
            ((AbstractIntegerAssert<?>) Assertions.assertThat(actual.cost()).as("Bad effortToFix at line " + expected.line(), new Object[0])).isEqualTo(expected.effortToFix());
        }

        if (expected.startColumn() != null) {
            ((AbstractIntegerAssert<?>) Assertions.assertThat(actual.primaryLocation().startLineOffset()).as("Bad start column at line " + expected.line(), new Object[0])).isEqualTo(expected.startColumn());
        }

        if (expected.endColumn() != null) {
            ((AbstractIntegerAssert<?>) Assertions.assertThat(actual.primaryLocation().endLineOffset()).as("Bad end column at line " + expected.line(), new Object[0])).isEqualTo(expected.endColumn());
        }

        if (expected.endLine() != null) {
            ((AbstractIntegerAssert<?>) Assertions.assertThat(actual.primaryLocation().endLine()).as("Bad end line at line " + expected.line(), new Object[0])).isEqualTo(expected.endLine());
        }

        if (expected.secondaryLines() != null) {
            ((ListAssert<?>) Assertions.assertThat(secondary(actual)).as("Bad secondary locations at line " + expected.line(), new Object[0])).isEqualTo(expected.secondaryLines());
        }

    }

    private static List<Integer> secondary(PreciseIssue issue) {
        List<Integer> result = new ArrayList<Integer>();
        Iterator<IssueLocation> var2 = issue.secondaryLocations().iterator();

        while (var2.hasNext()) {
            IssueLocation issueLocation = (IssueLocation) var2.next();
            result.add(issueLocation.startLine());
        }

        return Ordering.natural().sortedCopy(result);
    }

    private static Iterator<PreciseIssue> getActualIssues(File file, OpenApiCheck check, boolean isV2, boolean isV31) {
        List<PreciseIssue> issues = scanFileForIssues(file, check, isV2, isV31);
        List<PreciseIssue> sortedIssues = Ordering.natural().onResultOf(ExtendedOpenApiCheckVerifier::line).sortedCopy(issues);
        return sortedIssues.iterator();
    }

    public void collectExpectedIssue(Trivia trivia) {
        String text = trivia.getToken().getValue().trim();
        String marker = "Noncompliant";
        if (text.startsWith(marker)) {
            int issueLine = trivia.getToken().getLine();
            String paramsAndMessage = text.substring(marker.length()).trim();
            if (paramsAndMessage.startsWith("@")) {
                String[] spaceSplit = paramsAndMessage.split("[\\s\\[{]", 2);
                String lineMarker = spaceSplit[0].substring(1);
                issueLine = lineValue(issueLine, lineMarker);
                paramsAndMessage = spaceSplit.length > 1 ? spaceSplit[1] : "";
            }

            // START MODIFIED BLOCK

            int endIndex;
            int endIndexFix;
            String params = null;
            if (paramsAndMessage.startsWith("[[")) {
                endIndex = paramsAndMessage.indexOf("]]");
                params = paramsAndMessage.substring(2, endIndex);
                paramsAndMessage = paramsAndMessage.substring(endIndex + 2).trim();
            }

            while (paramsAndMessage.startsWith("{{")) {
                endIndexFix = paramsAndMessage.indexOf("}}}");
                endIndex = paramsAndMessage.indexOf("}}");
                if (endIndexFix == endIndex) {
                    endIndex = endIndex + 1;
                }
                String message = paramsAndMessage.substring(2, endIndex);
                paramsAndMessage = paramsAndMessage.substring(endIndex + 2).trim();

                TestIssue issue = TestIssue.create(message, issueLine);
                if (params != null) addParams(issue, params);
                this.expectedIssues.add(issue);
            }

            // END MODIFIED BLOCK

        } else if (text.startsWith("^")) {
            this.addPreciseLocation(trivia);
        }

    }

    private static void addParams(TestIssue issue, String params) {
        Iterator<String> var2 = Splitter.on(';').split(params).iterator();

        while (var2.hasNext()) {
            String param = (String) var2.next();
            int equalIndex = param.indexOf(61);
            if (equalIndex == -1) {
                throw new IllegalStateException("Invalid param at line 1: " + param);
            }

            String name = param.substring(0, equalIndex);
            String value = param.substring(equalIndex + 1);
            if ("effortToFix".equalsIgnoreCase(name)) {
                issue.effortToFix(Integer.valueOf(value));
            } else if ("startColumn".equalsIgnoreCase(name)) {
                issue.startColumn(Integer.valueOf(value));
            } else if ("endColumn".equalsIgnoreCase(name)) {
                issue.endColumn(Integer.valueOf(value));
            } else if ("endLine".equalsIgnoreCase(name)) {
                issue.endLine(lineValue(issue.line(), value));
            } else {
                if (!"secondary".equalsIgnoreCase(name)) {
                    throw new IllegalStateException("Invalid param at line 1: " + name);
                }

                addSecondaryLines(issue, value);
            }
        }

    }

    private static void addSecondaryLines(TestIssue issue, String value) {
        List<Integer> secondaryLines = new ArrayList<Integer>();
        if (!"".equals(value)) {
            Iterator<String> var3 = Splitter.on(',').split(value).iterator();

            while (var3.hasNext()) {
                String secondary = (String) var3.next();
                secondaryLines.add(lineValue(issue.line(), secondary));
            }
        }

        issue.secondary(secondaryLines);
    }

    private static int lineValue(int baseLine, String shift) {
        if (shift.startsWith("+")) {
            return baseLine + Integer.valueOf(shift.substring(1));
        } else {
            return shift.startsWith("-") ? baseLine - Integer.valueOf(shift.substring(1)) : Integer.valueOf(shift);
        }
    }

    private void addPreciseLocation(Trivia trivia) {
        Token token = trivia.getToken();
        int line = token.getLine();
        String text = token.getValue();
        if (token.getColumn() > 1) {
            throw new IllegalStateException("Line " + line + ": comments asserting a precise location should start at column 1");
        } else {
            String missingAssertionMessage = String.format("Invalid test file: a precise location is provided at line %s but no issue is asserted at line %s", line, line - 1);
            if (this.expectedIssues.isEmpty()) {
                throw new IllegalStateException(missingAssertionMessage);
            } else {
                TestIssue issue = (TestIssue) this.expectedIssues.get(this.expectedIssues.size() - 1);
                if (issue.line() != line - 1) {
                    throw new IllegalStateException(missingAssertionMessage);
                } else {
                    issue.endLine(issue.line());
                    issue.startColumn(text.indexOf(94) + 1);
                    issue.endColumn(text.lastIndexOf(94) + 2);
                }
            }
        }
    }

    @VisibleForTesting
    List<TestIssue> getCollectedIssues() {
        return Collections.unmodifiableList(this.expectedIssues);
    }

    private static final class ExpectedIssueCollector extends OpenApiVisitor {
        private final ExtendedOpenApiCheckVerifier verifier;

        private ExpectedIssueCollector(ExtendedOpenApiCheckVerifier verifier) {
            this.verifier = verifier;
        }

        protected void visitToken(Token token) {
            Iterator<Trivia> var2 = token.getTrivia().iterator();

            while (var2.hasNext()) {
                Trivia trivia = (Trivia) var2.next();
                this.verifier.collectExpectedIssue(trivia);
            }

        }
    }
}
