/**
 * @param {object} given
 * @param {object} options
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  if (!given) return [];

  const results = [];
  const ruleCode = context.rule.name.split(':').pop();

  const globalSecurity = given.security ?? given.swagger?.security;
  if (globalSecurity) {
    return results;
  }

  const methods = ['get', 'post', 'put', 'patch', 'delete'];

  const pathsObj = given.paths || {};
  for (const [pathName, pathItem] of Object.entries(pathsObj)) {
    for (const method of methods) {
      const operation = pathItem[method];
      if (!operation) continue;

      if (!operation.security || operation.security.length === 0) {
        results.push({
          message: `${ruleCode}: The operation '${method} ${pathName}' must have security defined.`,
          path: ['paths', pathName, method, 'security']
        });
      }
    }
  }

  return results;
};
