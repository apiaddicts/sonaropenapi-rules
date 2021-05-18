package org.sonar.samples.openapi.checks;

import org.sonar.samples.openapi.checks.core.*;
import org.sonar.samples.openapi.checks.format.*;
import org.sonar.samples.openapi.checks.parameters.*;
import org.sonar.samples.openapi.checks.resources.*;
import org.sonar.samples.openapi.checks.security.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class RulesLists {

	private RulesLists() {
	}

	public static List<Class> getSecurityChecks() {
		return Arrays.asList(
				OAR001MandatoryHttpsProtocolCheck.class,
				OAR033HttpHeadersCheck.class,
				OAR036SessionMechanismsCheck.class
		);
	}

	public static List<Class> getFormatChecks() {
		return Arrays.asList(
				OAR006UndefinedConsumesCheck.class,
				OAR007UndefinedProducesCheck.class,
				OAR009ConsumesDefaultMimeTypeCheck.class,
				OAR010ProducesDefaultMimeTypeCheck.class,
				OAR011UrlNamingConventionCheck.class,
				OAR012ParameterNamingConventionCheck.class,
				OAR016NumericFormatCheck.class,
				OAR037StringFormatCheck.class,
				OAR052UndefinedNumericFormatCheck.class
		);
	}

	public static List<Class> getResourcesChecks() {
		return Arrays.asList(
				OAR008AllowedHttpVerbCheck.class,
				OAR013DefaultResponseCheck.class,
				OAR014ResourceLevelWithinNonSuggestedRangeCheck.class,
				OAR015ResourceLevelMaxAllowedCheck.class,
				OAR017ResourcePathCheck.class,
				OAR018ResourcesByVerbCheck.class,
				OAR027PostResponseLocationHeaderCheck.class,
				OAR031ExamplesCheck.class,
				OAR032AmbiguousElementsPathCheck.class,
				OAR035AuthorizationResponsesCheck.class,
				OAR038StandardCreateResponseCheck.class,
				OAR039StandardResponseCodesCheck.class
		);
	}

	public static List<Class> getParametersChecks() {
		return Arrays.asList(
				OAR019SelectParameterCheck.class,
				OAR020ExpandParameterCheck.class,
				OAR021ExcludeParameterCheck.class,
				OAR022OrderbyParameterCheck.class,
				OAR023TotalParameterCheck.class,
				OAR024StartParameterCheck.class,
				OAR025LimitParameterCheck.class,
				OAR026TotalParameterDefaultValueCheck.class
		);
	}

	public static List<Class> getCoreChecks() {
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

	public static List<Class> getAllChecks() {
		List<Class> allChecks = new LinkedList<>();
		allChecks.addAll(getSecurityChecks());
		allChecks.addAll(getFormatChecks());
		allChecks.addAll(getResourcesChecks());
		allChecks.addAll(getParametersChecks());
		allChecks.addAll(getCoreChecks());
		return allChecks;
	}
}
