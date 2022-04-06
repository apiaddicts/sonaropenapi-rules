package org.sonar.samples.openapi.checks.format;

import java.util.HashSet;
import java.util.Set;

import org.sonar.samples.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

public abstract class AbstractNamingConventionCheck extends BaseCheck {
	
	private static final String CAMEL_REGEX = "[a-z]+([A-Z][a-z]+)*([A-Z])?";
	private static final String SNAKE_REGEX = "^[a-z0-9_$]*$";
	private static final String KEBAB_REGEX = "^[a-z0-9-]*$";
	private static final String UPPER_CAMEL_REGEX = "[A-Z]+([a-z]+)*([A-Z])?";

	private static final String PARAM_REGEX = "\\{[^}{]*}";

	public static final String SNAKE_CASE = "snake_case";
	public static final String KEBAB_CASE = "kebab-case";
	public static final String CAMEL_CASE = "camelCase";
	private static final String UPPER_CAMEL_CASE = "UpperCamelCase";

	private String key;
	private String message;
	protected String defaultNamingConvention = SNAKE_CASE;
    protected Set<String> nameExceptions = new HashSet<>();

	public AbstractNamingConventionCheck(String key, String message, String defaultNamingConvention) {
		this.key = key;
		this.message = message;
        this.defaultNamingConvention = defaultNamingConvention;
	}

	public AbstractNamingConventionCheck(String key, String message, String defaultNamingConvention, Set<String> nameExceptions) {
		this.key = key;
		this.message = message;
        this.defaultNamingConvention = defaultNamingConvention;
        this.nameExceptions = nameExceptions;
	}

	protected void validateNamingConvention(String name, JsonNode nameNode) {
		if (nameExceptions.contains(name)) return;
        if (name.contains("/")) {
            name = name.replaceAll(PARAM_REGEX, "");
        }

		switch (defaultNamingConvention) {
			case CAMEL_CASE:
                name = name.replaceAll("/", "");
				if (!isCamelCase(name)) {
					addIssue(key, translate(message, CAMEL_CASE), nameNode.key());
				}
				break;
            case UPPER_CAMEL_CASE:
                name = name.replaceAll("/", "");
                if (!isUpperCamelCase(name)) {
                    addIssue(key, translate(message, UPPER_CAMEL_CASE), nameNode.key());
                }
                break;
			case KEBAB_CASE:
                name = name.replaceAll("/", "-");
				if (!isKebabCase(name)) {
					addIssue(key, translate(message, KEBAB_CASE), nameNode.key());
				}
				break;
			case SNAKE_CASE:
			default:
                name = name.replaceAll("/", "_");
				if (!isSnakeCase(name)) {
					addIssue(key, translate(message, SNAKE_CASE), nameNode.key());
				}
				break;
		}
	}

	private boolean isCamelCase(String name) {
		return name.split("_").length == 1 && name.split("-").length == 1 && name.matches(CAMEL_REGEX);
	}

	private boolean isUpperCamelCase(String name) {
		return name.split("_").length == 1 && name.split("-").length == 1 && name.matches(UPPER_CAMEL_REGEX);
	}

	private boolean isSnakeCase(String name) {
		return name.matches(SNAKE_REGEX);
	}

	private boolean isKebabCase(String name) {
		return name.matches(KEBAB_REGEX);
	}

    
}
