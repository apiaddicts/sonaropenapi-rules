/**
 * Requires one or more HTTP response codes when path parameters are present.
 *
 * @param {object} given
 * @param {object} options
 * @param {string} options.response Comma-separated HTTP status codes (e.g. "400,404,200")
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  if (!given || typeof given !== 'object') {
    return [];
  }

  const ruleCode = context.rule?.name?.split(':').pop();
  const responseOpt = options?.response;

  if (typeof responseOpt !== 'string') {
    throw new TypeError(`${ruleCode}: "response" option must be a comma-separated string of HTTP status codes`);
  }

  const requiredResponses = responseOpt
    .split(',')
    .map(r => r.trim())
    .filter(r => /^\d{3}$/.test(r));

  if (requiredResponses.length === 0) {
    throw new Error(`${ruleCode}: No valid HTTP status codes found in "response" option`);
  }

  const pathItemKeyIndex = context.path?.length - 2;
  const pathItem = typeof pathItemKeyIndex === 'number'
    ? context.path?.[pathItemKeyIndex]
    : null;

  const pathParams = (pathItem?.parameters || []).filter(
    p => p?.in === 'path'
  );

  const operationParams = (given.parameters || []).filter(
    p => p?.in === 'path'
  );

  if (pathParams.length === 0 && operationParams.length === 0) {
    return [];
  }

  const responses = given?.responses || {};

  const hasRequiredResponse = requiredResponses.some(
    code => responses[code]
  );

  if (!hasRequiredResponse) {
    return [
      {
        message: context.rule.message,
      }
    ];
  }

  return [];
};