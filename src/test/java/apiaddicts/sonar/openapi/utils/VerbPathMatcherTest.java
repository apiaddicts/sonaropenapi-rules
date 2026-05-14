package apiaddicts.sonar.openapi.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class VerbPathMatcherTest {

    @Test
    public void testMatchesWithValuesExcluded() {
        VerbPathMatcher matcher = new VerbPathMatcher("get:^/items$", "get:/items");
        Optional<VerbPathMatcher.PatternGroup> result = matcher.matchesWithValues("get", "/items");
        assertThat(result).isEmpty();
    }

    @Test
    public void testMatchesWithValuesMatched() {
        VerbPathMatcher matcher = new VerbPathMatcher("get:^/items$");
        Optional<VerbPathMatcher.PatternGroup> result = matcher.matchesWithValues("get", "/items");
        assertThat(result).isPresent();
    }

    @Test
    public void testMatchesWithValuesNoMatch() {
        VerbPathMatcher matcher = new VerbPathMatcher("get:^/items$");
        Optional<VerbPathMatcher.PatternGroup> result = matcher.matchesWithValues("get", "/other");
        assertThat(result).isEmpty();
    }

    @Test
    public void testMatchesWithUnknownVerb() {
        VerbPathMatcher matcher = new VerbPathMatcher("get:^/items$");
        Optional<VerbPathMatcher.PatternGroup> result = matcher.matchesWithValues("post", "/items");
        assertThat(result).isEmpty();
    }

    @Test
    public void testPatternGroupStrToSetWithNull() {
        VerbPathMatcher.PatternGroup pg = new VerbPathMatcher.PatternGroup(
                Pattern.compile(".*"), Collections.singletonList(null));
        assertThat(pg.getValues()).isEmpty();
    }

    @Test
    public void testPatternGroupWithValues() {
        VerbPathMatcher.PatternGroup pg = new VerbPathMatcher.PatternGroup(
                Pattern.compile(".*"), Arrays.asList("a,b", "c"));
        assertThat(pg.getValues(0)).containsExactlyInAnyOrder("a", "b");
        assertThat(pg.getValues(1)).containsExactly("c");
    }

    @Test
    public void testMatchesReturnsFalseForMultipleMeWords() {
        VerbPathMatcher matcher = new VerbPathMatcher("get:^/me/[^/{}]*/me$");
        Optional<VerbPathMatcher.PatternGroup> result = matcher.matchesWithValues("get", "/me/pets/me");
        assertThat(result).isEmpty();
    }

    @Test
    public void testMatchesReturnsTrueForSingleMeWord() {
        VerbPathMatcher matcher = new VerbPathMatcher("get:^/me$");
        Optional<VerbPathMatcher.PatternGroup> result = matcher.matchesWithValues("get", "/me");
        assertThat(result).isPresent();
    }

    @Test
    public void testExpressionWithMultipleVerbs() {
        VerbPathMatcher matcher = new VerbPathMatcher("get,post:^/items$");
        assertThat(matcher.matchesWithValues("get", "/items")).isPresent();
        assertThat(matcher.matchesWithValues("post", "/items")).isPresent();
    }

    @Test
    public void testExpressionWithMultiplePatterns() {
        VerbPathMatcher matcher = new VerbPathMatcher("get:^/items$,^/users$");
        assertThat(matcher.matchesWithValues("get", "/items")).isPresent();
        assertThat(matcher.matchesWithValues("get", "/users")).isPresent();
    }

    @Test
    public void testNullExclusionExpression() {
        VerbPathMatcher matcher = new VerbPathMatcher("get:^/items$", null);
        assertThat(matcher.matchesWithValues("get", "/items")).isPresent();
    }

    @Test
    public void testExpressionWithValues() {
        VerbPathMatcher matcher = new VerbPathMatcher("get:^/items$:value1,value2");
        Optional<VerbPathMatcher.PatternGroup> result = matcher.matchesWithValues("get", "/items");
        assertThat(result).isPresent();
        assertThat(result.get().getValues()).containsExactlyInAnyOrder("value1", "value2");
    }
}
