/**
 * Validates that properties intended for password storage use format: password
 * @param {object} given - The schema properties object
 * @param {object} options - Function options
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  const results = [];

  if (!given || typeof given !== 'object') {
    return results;
  }

  // Check each property in the schema
  Object.entries(given).forEach(([propName, propSchema]) => {
    if (!propSchema || typeof propSchema !== 'object') {
      return;
    }

    // Check if this is a password-related field (name contains "password")
    const isPasswordField = propName.toLowerCase().includes('password');

    if (isPasswordField && propSchema.type === 'string') {
      // Password fields must have format: password
      if (propSchema.format !== 'password') {
        results.push({
          message: context.rule.message || 'OAR081: Password fields should use format: password',
          path: [...context.path, propName, 'format']
        });
      }
    }
  });

  return results;
};
