/**
 * Validates that path parameters don't appear as query parameters
 * This prevents ambiguity and design issues
 *
 * @param {object} given - The paths object
 * @param {object} options - Function options
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  const errors = [];

  if (!given || typeof given !== 'object') {
    return errors;
  }

  // Iterate through each path in the paths object
  for (const [pathKey, pathItem] of Object.entries(given)) {
    if (!pathItem || typeof pathItem !== 'object') {
      continue;
    }

    // Extract path parameters from the path pattern
    const pathParams = extractPathParameters(pathKey);

    if (pathParams.length === 0) {
      continue;
    }

    // Check each operation in this path
    for (const [operationKey, operation] of Object.entries(pathItem)) {
      if (!operation || typeof operation !== 'object' ||
          !['get', 'post', 'put', 'patch', 'delete', 'head', 'options', 'trace'].includes(operationKey)) {
        continue;
      }

      const parameters = operation.parameters || [];

      if (!Array.isArray(parameters)) {
        continue;
      }

      // Look for query parameters that match path parameters
      for (let i = 0; i < parameters.length; i++) {
        const param = parameters[i];
        if (param && param.in === 'query' && param.name) {
          if (pathParams.includes(param.name)) {
            errors.push({
              message: `Parameter '${param.name}' appears in both path and query on '${operationKey.toUpperCase()} ${pathKey}'. Path parameters should not be duplicated as query parameters.`,
              path: [...context.path, pathKey, operationKey, 'parameters', i, 'name']
            });
          }
        }
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
