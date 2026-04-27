/**
 * @param {string} given
 * @param {object} options
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (parameters, options, context) => {
  if (!parameters || !Array.isArray(parameters) || parameters.length === 0) {
    return [];
  }

  const bodyParams = parameters.filter(param => {
    return param && typeof param === 'object' && param.in === 'body';
  });

  if (bodyParams.length > 1) {
    return [
      {
        message: context.rule.message,
      }
    ];
  }

  return [];
};