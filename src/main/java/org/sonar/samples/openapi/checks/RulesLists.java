package org.sonar.samples.openapi.checks;

import org.sonar.samples.openapi.checks.core.*;
import org.sonar.samples.openapi.checks.format.*;
import org.sonar.samples.openapi.checks.parameters.*;
import org.sonar.samples.openapi.checks.resources.*;
import org.sonar.samples.openapi.checks.security.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class RulesLists {

	private RulesLists() {
	}

	public static List<Class<?>> getSecurityChecks() {
		return Arrays.asList(
				OAR001MandatoryHttpsProtocolCheck.class,
				OAR002ValidWso2ScopesCheck.class,
				OAR003DefinedWso2ScopesDescriptionCheck.class,
				OAR004ValidWso2ScopesRolesCheck.class,
				OAR005UndefinedWso2ScopeUseCheck.class,
				OAR033HttpHeadersCheck.class,
				OAR036SessionMechanismsCheck.class,
				OAR040StandardWso2ScopesNameCheck.class,
				OAR041UndefinedAuthTypeForWso2ScopeCheck.class,
				OAR053ResponseHeadersCheck.class,
				OAR054HostCheck.class
		);
	}

	public static List<Class<?>> getFormatChecks() {
		return Arrays.asList(
				OAR006UndefinedConsumesCheck.class,
				OAR007UndefinedProducesCheck.class,
				OAR009ConsumesDefaultMimeTypeCheck.class,
				OAR010ProducesDefaultMimeTypeCheck.class,
				OAR011UrlNamingConventionCheck.class,
				OAR012ParameterNamingConventionCheck.class,
				OAR016NumericFormatCheck.class,
				OAR037StringFormatCheck.class,
				OAR042BasePathCheck.class,
				OAR052UndefinedNumericFormatCheck.class
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
				OAR035AuthorizationResponsesCheck.class,
				OAR038StandardCreateResponseCheck.class,
				OAR039StandardResponseCodesCheck.class
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
				OAR028FilterParameterCheck.class
		);
	}

	public static List<Class<?>> getCoreChecks() {
		return Arrays.asList(
				OAR043ParsingErrorCheck.class,
				OAR044MediaTypeCheck.class,
				OAR045DefinedResponseCheck.class,
				OAR046DeclaredTagCheck.class,
				OAR047DocumentedTagCheck.class,
				OAR048AtMostOneBodyParameterCheck.class,
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
		return allChecks;
	}
}
