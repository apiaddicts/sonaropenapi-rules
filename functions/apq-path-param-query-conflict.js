/**
 * Validates that path parameters don't appear as query parameters
 * This prevents ambiguity and design issues
 *
 * @param {object} given - The operation object
 * @param {object} options - Function options
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  const errors = [];

  if (!given || typeof given !== 'object') {
    return errors;
  }

  // Extract path from context (the path is available in the JSONPath)
  const pathKey = context.path[1]; // paths -> /resource/{id}

  if (!pathKey) {
    return errors;
  }

  // Extract path parameters from the path pattern
  const pathParams = extractPathParameters(pathKey);

  if (pathParams.length === 0) {
    return errors;
  }

  // Check operation parameters
  const parameters = given.parameters || [];

  if (!Array.isArray(parameters)) {
    return errors;
  }

  // Look for query parameters that match path parameters
  for (const param of parameters) {
    if (param && param.in === 'query' && param.name) {
      if (pathParams.includes(param.name)) {
        errors.push({
          message: `Parameter '${param.name}' appears in both path and query. Path parameters should not be duplicated as query parameters.`,
          path: [...context.path, 'parameters', parameters.indexOf(param), 'name']
        });
      }
    }
  }

  return errors;
};

/**
 * Extract parameter names from OpenAPI path pattern
 * Example: /users/{id}/posts/{postId} -> ['id', 'postId']
 */
function extractPathParameters(pathPattern) {
  const matches = pathPattern.match(/\{([^}]+)\}/g) || [];
  return matches.map(match => match.slice(1, -1)); // Remove { and }
}
