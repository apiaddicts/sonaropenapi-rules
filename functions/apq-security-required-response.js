/**
 * Validates that operations with security defined include a 401 Unauthorized response
 * @param {object} given - The operation node
 * @param {object} options - Function options
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  const results = [];

  if (!given || typeof given !== 'object') {
    return results;
  }

  const responses = given.responses;
  if (!responses || typeof responses !== 'object') {
    return results;
  }

  // Check if this operation has security defined
  const operationHasSecurity = given.security &&
    Array.isArray(given.security) &&
    given.security.length > 0;

  // Check if there's global security in the document
  let globalHasSecurity = false;
  try {
    const rootSecurity = context.document?.parserResult?.data?.security;
    globalHasSecurity = rootSecurity &&
      Array.isArray(rootSecurity) &&
      rootSecurity.length > 0;
  } catch (e) {
    // Ignore errors accessing root security
  }

  // Only require 401 if security is defined (operation-level or global)
  if (operationHasSecurity || globalHasSecurity) {
    if (!responses['401']) {
      results.push({
        message: context.rule.message || 'OAR035: Response code 401 must be defined for operations with security schemes defined.',
        path: [...context.path, 'responses']
      });
    }
  }

  return results;
};
