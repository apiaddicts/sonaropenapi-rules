/**
 * @param {object} given
 * @param {object} options
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = function apqStandardResponseCodes(given, options, context) {
  const results = [];

  if (!given || !context?.path) return results;

  const responses = given.responses;
  if (!responses || typeof responses !== 'object') return results;

  const definedCodes = Object.keys(responses);
  const pathIndex = context.path.indexOf('paths');
  if (pathIndex === -1) return results;

  const resourcePath = context.path[pathIndex + 1];
  const verb = context.path[pathIndex + 2];

  if (typeof resourcePath !== 'string' || typeof verb !== 'string') return results;

  const exclusions = options?.['resources-exclusions'] || [];
  if (exclusions.some(ex => {
    const [exVerb, exPath] = ex.split(':');
    return exVerb.toLowerCase() === verb.toLowerCase() && new RegExp(`^${exPath}$`).test(resourcePath);
  })) return results;

  const rulesConfig = options?.['required-codes-by-resources-paths'];
  if (!rulesConfig) return results;

  const rules = rulesConfig
    .split(/[\n;]/)
    .map(r => r.trim())
    .filter(Boolean)
    .map(rule => {
      const parts = rule.split(':');
      if (parts.length < 3) return null;

      const verbPart = parts[0].toLowerCase();
      const codesPart = parts[parts.length - 1];
      const regexPart = parts.slice(1, -1).join(':');

      return {
        verb: verbPart,
        pathRegex: new RegExp(regexPart),
        requiredCodes: codesPart.split(',').map(c => c.trim()).filter(Boolean)
      };
    }).filter(Boolean);

  const matchedRule = rules.find(
    r => r.verb === verb.toLowerCase() && r.pathRegex.test(resourcePath)
  );

  if (!matchedRule) return results;

  matchedRule.requiredCodes.forEach(respCode => {
    let missing = false;
    let msg = "";

    if (respCode.includes('|')) {
      const alternatives = respCode.split('|');
      if (!alternatives.some(c => definedCodes.includes(c))) {
        missing = true;
        msg = `OAR039: Response code ${alternatives.join(' or ')} must be defined.`;
      }
    } else if (!definedCodes.includes(respCode)) {
      missing = true;
      msg = `OAR039: Response code ${respCode} must be defined.`;
    }

    if (missing) {
      results.push({
        message: msg,
        path: context.path
      });
    }
  });

  return results;
};