package org.sonar.samples.openapi.checks.resources;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.samples.openapi.I18nContext;
import org.sonar.samples.openapi.utils.VerbPathMatcher;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sonar.samples.openapi.utils.VerbPathMatcher.*;

@Rule(key = OAR039StandardResponseCodesCheck.KEY)
public class OAR039StandardResponseCodesCheck extends AbstractVerbPathCheck {

    public static final String KEY = "OAR039";
    public static final String MESSAGE = "OAR039.error";

    public static final String OR_OPERATOR = "|";

    private static final String OK_CODE = "200";
    private static final String CREATED_CODE = "201";
    private static final String ACCEPTED_CODE = "202";
    private static final String PARTIAL_CONTENT_CODE = "206";
    private static final String BAD_REQUEST_CODE = "400";
    private static final String NOT_FOUND_CODE = "404";
    //private static final String PAYLOAD_TOO_LARGE = "413";
    private static final String UNSUPPORTED_MEDIA_TYPE = "415";
    private static final String INTERNAL_SERVER_ERROR = "500";
    private static final String SERVICE_UNAVAILABLE = "503";
    //private static final String GATEWAY_TIMEOUT = "504";

    private static final String COMMON_CODES = INTERNAL_SERVER_ERROR + VALUE_SEPARATOR + SERVICE_UNAVAILABLE;
    private static final String OPS_WITH_BODY_CODES = BAD_REQUEST_CODE + VALUE_SEPARATOR + UNSUPPORTED_MEDIA_TYPE;

    private static final String GET_COLLECTION_CODES = OK_CODE + OR_OPERATOR + PARTIAL_CONTENT_CODE + VALUE_SEPARATOR + BAD_REQUEST_CODE + VALUE_SEPARATOR + COMMON_CODES;
    private static final String GET_SUBCOLLECTIONS_CODES = GET_COLLECTION_CODES + VALUE_SEPARATOR + NOT_FOUND_CODE;
    private static final String GET_ONE_CODES = OK_CODE + VALUE_SEPARATOR + BAD_REQUEST_CODE + VALUE_SEPARATOR + NOT_FOUND_CODE + VALUE_SEPARATOR + COMMON_CODES;
    private static final String POST_CODES = OK_CODE + OR_OPERATOR + CREATED_CODE + OR_OPERATOR + ACCEPTED_CODE + VALUE_SEPARATOR + OPS_WITH_BODY_CODES + VALUE_SEPARATOR + COMMON_CODES;
    private static final String POST_SUBRESOURCES_CODES = POST_CODES + VALUE_SEPARATOR + NOT_FOUND_CODE;
    private static final String UPDATE_N_OTHER_POST_OPS_CODES = OK_CODE + VALUE_SEPARATOR + NOT_FOUND_CODE + VALUE_SEPARATOR + OPS_WITH_BODY_CODES + VALUE_SEPARATOR + COMMON_CODES;
    private static final String DELETE_CODES = GET_ONE_CODES;

    private static final String DEFAULT_CODES_BY_RESOURCES_AND_VERB =
            GET_ALL_1ST_LEVEL + PART_SEPARATOR + GET_COLLECTION_CODES +
            GET_ALL_2ND_LEVEL + PART_SEPARATOR + GET_SUBCOLLECTIONS_CODES +
            GET_ALL_3RD_LEVEL + PART_SEPARATOR + GET_SUBCOLLECTIONS_CODES +
            GET_ONE_1ST_LEVEL + PART_SEPARATOR +  GET_ONE_CODES +
            GET_ONE_2ND_LEVEL + PART_SEPARATOR +  GET_ONE_CODES +
            GET_ONE_3RD_LEVEL + PART_SEPARATOR +  GET_ONE_CODES +
            POST_1ST_LEVEL + PART_SEPARATOR +  POST_CODES +
            POST_2ND_LEVEL + PART_SEPARATOR + POST_SUBRESOURCES_CODES +
            POST_3RD_LEVEL + PART_SEPARATOR + POST_SUBRESOURCES_CODES +
            POST_GET_1ST_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES +
            POST_GET_2ND_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES +
            POST_GET_3RD_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES +
            POST_DELETE_1ST_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES +
            POST_DELETE_2ND_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES +
            POST_DELETE_3RD_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES +
            PUT_1ST_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES +
            PUT_2ND_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES +
            PUT_3RD_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES +
            DELETE_1ST_LEVEL + PART_SEPARATOR + DELETE_CODES +
            DELETE_2ND_LEVEL + PART_SEPARATOR + DELETE_CODES +
            DELETE_3RD_LEVEL + PART_SEPARATOR + DELETE_CODES +
            PATCH_1ST_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES +
            PATCH_2ND_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES +
            PATCH_3RD_LEVEL + PART_SEPARATOR + UPDATE_N_OTHER_POST_OPS_CODES;

    private static final String DEFAULT_EXCLUSION = "get:/status";

    @RuleProperty(
            key = "required-codes-by-resources-paths",
            description = "List of allowed resources path. Format: <V>,<V>:<RX>,<RX>:<C>;<V>,<V>:<RX>,<RX>:<C>",
            defaultValue = DEFAULT_CODES_BY_RESOURCES_AND_VERB)
    private String pattern = DEFAULT_CODES_BY_RESOURCES_AND_VERB;

    @RuleProperty(
            key = "resources-exclusions",
            description = "List of explicit resources to exclude from this rule. Format: <V>,<V>:<R>,<R>;<V>,<V>:<R>,<R>",
            defaultValue = DEFAULT_EXCLUSION
    )
    private String exclusion = DEFAULT_EXCLUSION;

    public OAR039StandardResponseCodesCheck() {
        super(KEY);
    }

    @Override
    protected void visitFile(JsonNode root) {
        matcher = new VerbPathMatcher(pattern, exclusion);
    }

    @Override
    protected void matchV2(JsonNode node, JsonNode operationNode, String verb, String path, PatternGroup pg) {
        Set<String> expectedCodes = pg.getValues();
        JsonNode responsesNode = operationNode.value().get("responses");
        Set<String> currentCodes = responsesNode.properties().stream().map(JsonNode::key).map(JsonNode::getTokenValue).collect(Collectors.toSet());
        expectedCodes.removeAll(currentCodes);
        for (String missingCode : expectedCodes.stream().sorted().collect(Collectors.toList())) {
            String[] orCodes = missingCode.split("\\|");
            if (orCodes.length > 1) {
                if (Collections.disjoint(currentCodes, Arrays.asList(orCodes))) {
                    addIssue(KEY, translate(MESSAGE, String.join("en".equals(I18nContext.getLang()) ? " or " : " ó " , orCodes)), responsesNode.key());
                }
            } else {
                addIssue(KEY, translate(MESSAGE, missingCode), responsesNode.key());
            }
        }
    }
}
