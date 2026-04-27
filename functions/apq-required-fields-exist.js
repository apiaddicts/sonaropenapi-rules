/**
 * @param {object} given
 * @param {object} options
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  const results = [];

  if (!given || given.type !== 'object') return results;

  const { required, properties } = given;
  if (!Array.isArray(required)) return results;

  const propertyNames = new Set(Object.keys(properties || {}));
  const ruleCode = context.rule.name.split(':').pop();

  required.forEach((field, index) => {
    if (!propertyNames.has(field)) {
      results.push({
        message: `${ruleCode}: This value does not exist, '${field}' must be defined in the schema properties.`,
        path: [...context.path, 'required', index]
      });
    }
  });

  return results;
};