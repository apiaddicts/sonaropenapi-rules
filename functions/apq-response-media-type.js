/**
 * @param {object} given
 * @param {object} options
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */

const NO_BODY_CODES = new Set(['204']);

module.exports = (given, options, context) => {
  const errors = [];
  if (!given) return errors;

  const root = context.document.parserResult.data;

  if (root.swagger) {
    const produces = given.produces ?? root.produces;

    if (!produces || !Array.isArray(produces) || !produces.includes('application/json')) {
      errors.push({
        message: context.rule.message,
        path: [...context.path]
      });
    }

    return errors;
  }

  const responses = given.responses;
  if (!responses) return errors;

  for (const [statusCode, response] of Object.entries(responses)) {
    if (NO_BODY_CODES.has(statusCode)) continue;

    if (!response?.content?.['application/json']) {
      errors.push({
        message: context.rule.message,
        path: [...context.path, 'responses', statusCode]
      });
    }
  }

  return errors;
};