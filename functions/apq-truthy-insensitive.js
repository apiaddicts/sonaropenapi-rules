
/**
 * @param {Object} given
 * @param {object} options
 * @param {string} options.property
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  const errors = [];

  if (!given || Object.keys(given).some(key => key.toUpperCase() !== options.property.toUpperCase())) {
    errors.push({
      message: context.rule.message
    });
  }

  return errors;
};
