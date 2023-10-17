package apiquality.sonar.openapi.checks;

import apiquality.sonar.openapi.checks.apim.wso2.*;
import apiquality.sonar.openapi.checks.core.*;
import apiquality.sonar.openapi.checks.format.*;
import apiquality.sonar.openapi.checks.parameters.*;
import apiquality.sonar.openapi.checks.resources.*;
import apiquality.sonar.openapi.checks.security.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class RulesLists {

	private RulesLists() {
	}

	public static List<Class<?>> getWSO2Checks() {
		return Arrays.asList(
				OAR002ValidWso2ScopesCheck.class,
				OAR003DefinedWso2ScopesDescriptionCheck.class,
				OAR004ValidWso2ScopesRolesCheck.class,
				OAR005UndefinedWso2ScopeUseCheck.class,
				OAR040StandardWso2ScopesNameCheck.class,
				OAR041UndefinedAuthTypeForWso2ScopeCheck.class
		);
	}

	public static List<Class<?>> getSecurityChecks() {
		return Arrays.asList(
				OAR001MandatoryHttpsProtocolCheck.class,
				OAR033HttpHeadersCheck.class,
				OAR035UnauthorizedResponseCheck.class,
				OAR036SessionMechanismsCheck.class,
				OAR054HostCheck.class,
				OAR070BrokenAccessControlCheck.class,
				OAR072NonOKModelResponseCheck.class,
				OAR073RateLimitCheck.class,
				OAR074NumericParameterIntegrityCheck.class,
				OAR075StringParameterIntegrityCheck.class,
				OAR076NumericFormatCheck.class,
				OAR078VerbsSecurityCheck.class,
				OAR079PathParameter404Check.class,
				OAR080SecuritySchemasCheck.class,
				OAR081PasswordFormatCheck.class,
				OAR082BinaryOrByteFormatCheck.class,
				OAR083ForbiddenQueryParamsCheck.class,
				OAR084ForbiddenFormatsInQueryCheck.class,
				OAR085OpenAPIVersionCheck.class,
				OAR096ForbiddenResponseCheck.class
		);
	}

	public static List<Class<?>> getFormatChecks() {
		return Arrays.asList(
				OAR006UndefinedRequestMediaTypeCheck.class,
				OAR007UndefinedResponseMediaTypeCheck.class,
				OAR009DefaultRequestMediaTypeCheck.class,
				OAR010DefaultResponseMediaTypeCheck.class,
				OAR011UrlNamingConventionCheck.class,
				OAR037StringFormatCheck.class,
				OAR066SnakeCaseNamingConventionCheck.class,
				OAR067CamelCaseNamingConventionCheck.class,
				OAR068PascalCaseNamingConventionCheck.class,
				OAR077ParametersInQuerySnakeCaseCheck.class,
				OAR086DescriptionFormatCheck.class,
				OAR087SummaryFormatCheck.class,
				OAR088RefParamCheck.class,
				OAR089RefRequestBodyCheck.class,
				OAR090RefResponseCheck.class,
				OAR094UseExamplesCheck.class,
				OAR097ShortBasePathCheck.class,
				OAR098LongBasePathCheck.class,
				OAR099ApiPrefixBasePathCheck.class,
				OAR100LastPartBasePathCheck.class,
				OAR101FirstPartBasePathCheck.class,
				OAR102SecondPartBasePathCheck.class			
		);
	}

	public static List<Class<?>> getResourcesChecks() {
		return Arrays.asList(
				OAR008AllowedHttpVerbCheck.class,
				OAR013DefaultResponseCheck.class,
				OAR014ResourceLevelWithinNonSuggestedRangeCheck.class,
				OAR015ResourceLevelMaxAllowedCheck.class,
				OAR017ResourcePathCheck.class,
				OAR018ResourcesByVerbCheck.class,
				OAR027PostResponseLocationHeaderCheck.class,
				OAR029StandardResponseCheck.class,
				OAR030StatusEndpointCheck.class,
				OAR031ExamplesCheck.class,
				OAR032AmbiguousElementsPathCheck.class,
				OAR034StandardPagedResponseCheck.class,
				OAR038StandardCreateResponseCheck.class,
				OAR061GetMethodCheck.class,
				OAR062PostMethodCheck.class,
				OAR063PutMethodCheck.class,
				OAR064PatchMethodCheck.class,
				OAR065DeleteMethodCheck.class,
				OAR071GetQueryParamsDefinedCheck.class,
				OAR091ParamOnlyRefCheck.class,
				OAR092RequestBodyOnlyRefCheck.class,
				OAR093ResponseOnlyRefCheck.class				
		);
	}

	public static List<Class<?>> getParametersChecks() {
		return Arrays.asList(
				OAR019SelectParameterCheck.class,
				OAR020ExpandParameterCheck.class,
				OAR021ExcludeParameterCheck.class,
				OAR022OrderbyParameterCheck.class,
				OAR023TotalParameterCheck.class,
				OAR024StartParameterCheck.class,
				OAR025LimitParameterCheck.class,
				OAR026TotalParameterDefaultValueCheck.class,
				OAR028FilterParameterCheck.class,
				OAR060QueryParametersOptionalCheck.class,
				OAR069PathParamAndQueryCheck.class
				
		);
	}

	public static List<Class<?>> getCoreChecks() {
		return Arrays.asList(
				OAR043ParsingErrorCheck.class,
				OAR044MediaTypeCheck.class,
				OAR045DefinedResponseCheck.class,
				OAR046DeclaredTagCheck.class,
				OAR047DocumentedTagCheck.class,
				OAR049NoContentIn204Check.class,
				OAR050ProvideOpSummaryCheck.class,
				OAR051DescriptionDiffersSummaryCheck.class
		);
	}

	public static List<Class<?>> getAllChecks() {
		List<Class<?>> allChecks = new LinkedList<>();
		allChecks.addAll(getSecurityChecks());
		allChecks.addAll(getFormatChecks());
		allChecks.addAll(getResourcesChecks());
		allChecks.addAll(getParametersChecks());
		allChecks.addAll(getCoreChecks());
        allChecks.addAll(getWSO2Checks());
		return allChecks;
	}
}
