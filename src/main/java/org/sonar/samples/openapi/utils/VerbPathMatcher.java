package org.sonar.samples.openapi.utils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VerbPathMatcher {

    public static final String GROUP_SEPARATOR = ";";
    public static final String PART_SEPARATOR = ":";
    public static final String VALUE_SEPARATOR = ",";

    public static final String GET_WORD = "get";
    public static final String DELETE_WORD = "delete";

    public static final String STATIC_PATH_PART_REGEX = "[^\\/{}]*";
    public static final String DYNAMIC_PATH_PART_REGEX = "\\{" + STATIC_PATH_PART_REGEX + "\\}";
    public static final String SLASH = "\\/";

    public static final String COLLECTION_PATH = SLASH + STATIC_PATH_PART_REGEX;
    public static final String ELEMENT_PATH = COLLECTION_PATH + SLASH + DYNAMIC_PATH_PART_REGEX;

    public static final String GET_ALL = ";get:^" + COLLECTION_PATH + "$";
    public static final String GET_ONE = ";get:^" + ELEMENT_PATH + "$";
    public static final String GET_ALL_SUB_RESOURCES = ";get:^" + ELEMENT_PATH + SLASH + STATIC_PATH_PART_REGEX + "$";
    public static final String POST = ";post:^" + COLLECTION_PATH + "$";
    public static final String POST_GET = ";post:^" + COLLECTION_PATH + SLASH + GET_WORD + "$";
    public static final String POST_DELETE = ";post:^" + COLLECTION_PATH + SLASH + DELETE_WORD + "$";
    public static final String POST_SUB_RESOURCE = ";post:^" + ELEMENT_PATH + SLASH + STATIC_PATH_PART_REGEX + "$";
    public static final String POST_SUB_RESOURCE_GET = ";post:^" + ELEMENT_PATH + SLASH + STATIC_PATH_PART_REGEX + SLASH + GET_WORD + "$";
    public static final String POST_SUB_RESOURCE_DELETE = ";post:^" + ELEMENT_PATH + SLASH + STATIC_PATH_PART_REGEX + SLASH + DELETE_WORD + "$";
    public static final String PUT = ";put:^" + ELEMENT_PATH + "$";
    public static final String PUT_SUB_RESOURCE = ";put:^" + ELEMENT_PATH + SLASH + ELEMENT_PATH + "$";
    public static final String DELETE = ";delete:^" + ELEMENT_PATH + "$";
    public static final String DELETE_SUB_RESOURCE = ";put:^" + ELEMENT_PATH + SLASH + ELEMENT_PATH + "$";
    public static final String PATCH = ";patch:^" + ELEMENT_PATH + "$";
    public static final String PATCH_SUB_RESOURCE = ";patch:^" + ELEMENT_PATH + SLASH + ELEMENT_PATH + "$";

    private Map<String, List<PatternGroup>> patternsByVerb;
    private Map<String, Set<String>> exclusionsByVerb;

    public VerbPathMatcher(String expression) {
        this(expression, null);
    }
    public VerbPathMatcher(String expression, String exclusionExpression) {
        processPatterns(expression);
        processExclusions(exclusionExpression);
    }

    private void processPatterns(String expression) {
        patternsByVerb = new HashMap<>();
        for (String group : expression.split(GROUP_SEPARATOR)) {
            String[] parts = group.split(PART_SEPARATOR);
            if (parts.length < 2) continue;
            String[] verbs = parts[0].split(VALUE_SEPARATOR);
            String[] regex = parts[1].split(VALUE_SEPARATOR);
            final List<String> values = new ArrayList<>(Arrays.asList(parts).subList(2, parts.length));
            List<PatternGroup> patterns = Stream.of(regex).map(rx -> Pattern.compile(rx.trim())).map(p -> new PatternGroup(p, values)).collect(Collectors.toList());
            for (String verb : verbs) {
                verb = verb.trim();
                patternsByVerb.putIfAbsent(verb, new LinkedList<>());
                patternsByVerb.get(verb).addAll(patterns);
            }
        }
    }

    private void processExclusions(String expression) {
        if (expression == null) expression = "";
        exclusionsByVerb = new HashMap<>();
        for (String group : expression.split(GROUP_SEPARATOR)) {
            String[] parts = group.split(PART_SEPARATOR);
            if (parts.length < 2) continue;
            String[] verbs = parts[0].split(VALUE_SEPARATOR);
            String[] exclusions = parts[1].split(VALUE_SEPARATOR);
            for (String verb : verbs) {
                verb = verb.trim();
                exclusionsByVerb.put(verb, Arrays.stream(exclusions).map(String::trim).collect(Collectors.toSet()));
            }
        }
    }

    public boolean matches(String verb, String path) {
        return matchesWithValues(verb, path).isPresent();
    }

    public Optional<VerbPathMatcher.PatternGroup> matchesWithValues(String verb, String path) {
        if (exclusionsByVerb.getOrDefault(verb, Collections.emptySet()).contains(path)) return Optional.empty();
        return patternsByVerb.getOrDefault(verb, Collections.emptyList())
                .stream().filter(p -> p.matches(path)).findFirst();
    }

    public static class PatternGroup {
        Pattern pattern;
        private List<Set<String>> values;

        public PatternGroup(Pattern pattern, List<String> values) {
            this.pattern = pattern;
            this.values = values.stream().map(this::strToSet).collect(Collectors.toList());
        }

        private Set<String> strToSet(String str) {
            if (str == null) return Collections.emptySet();
            return Arrays.stream(str.split(VALUE_SEPARATOR)).map(String::trim).collect(Collectors.toSet());
        }

        public boolean matches(String path) {
            return pattern.matcher(path).matches();
        }

        public Set<String> getValues() {
            return getValues(0);
        }

        public Set<String> getValues(int i) {
            return new HashSet<>(values.get(i));
        }
    }
}
