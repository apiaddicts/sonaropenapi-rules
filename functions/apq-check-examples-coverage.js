/**
 * @param {object} given
 * @param {object} options
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  if (!given || typeof given !== 'object') return [];

  const hasExample = (node) => {
    if (!node || typeof node !== 'object') return false;

    if (node.example !== undefined || node.examples !== undefined) return true;

    if (node.content && typeof node.content === 'object') {
      return Object.values(node.content).some(mediaType => hasExample(mediaType));
    }

    if (node.schema) {
      return checkSchema(node.schema);
    }

    if (node.properties || node.items || node.type) {
      return checkSchema(node);
    }

    return false;
  };

  const checkSchema = (schema) => {
    if (!schema || typeof schema !== 'object') return false;

    if (schema.example !== undefined || schema.examples !== undefined) return true;

    if (schema.properties && typeof schema.properties === 'object') {
      return Object.values(schema.properties).some(prop => checkSchema(prop));
    }

    if (schema.items) {
      return checkSchema(schema.items);
    }

    return false;
  };

  if (!hasExample(given)) {
    return [
      {
        message: context.rule.message
      },
    ];
  }

  return [];
};