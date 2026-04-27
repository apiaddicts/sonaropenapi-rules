/**
 * @param {object} given
 * @param {object} options
 * @param {string} options.fieldName
 * @param {string} options.fieldLocation
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  const { fieldName, fieldLocation } = options || {};
  const ruleCode = context.rule.name.split(':').pop();

  if (!given || !fieldName || !fieldLocation) return [];

  const locations = fieldLocation.split(',').map(l => l.trim());
  const results = [];

  const hasField = node =>
    node && typeof node === 'object' && Object.prototype.hasOwnProperty.call(node, fieldName);

  const addError = path =>
    results.push({
      message: `${ruleCode}: Field or extension ${fieldName} must be at the assigned location`,
      path,
    });

  if (hasField(given)) {
    return [];
  }

  const rootLocations = ['path', 'components'];

  const missingRoot = rootLocations.find(
    loc => locations.includes(loc) && !given[loc === 'path' ? 'paths' : loc]
  );

  if (missingRoot) {
    return [
      {
        message: `${ruleCode}: The location ${missingRoot} is not present.`,
        path: [],
      },
    ];
  }

  const opLoc = locations.find(l => l.startsWith('operation_'));
  const respLoc = locations.find(l => l.startsWith('response_'));

  const verb = opLoc?.split('_')[1];
  const status = respLoc?.split('_')[1];

  Object.entries(given.paths || {}).forEach(([pathKey, pathItem]) => {
    if (hasField(pathItem)) return;

    if (!verb || !pathItem[verb]) return;

    const operation = pathItem[verb];

    if (hasField(operation)) return;

    if (!operation.responses || !operation.responses[status]) return;

    const response = operation.responses[status];

    if (!hasField(response)) {
      addError(['paths', pathKey, verb, 'responses', status]);
    }
  });

  return results;
};