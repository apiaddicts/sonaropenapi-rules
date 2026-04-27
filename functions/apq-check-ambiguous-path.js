/**
 * Validates that URL path segments use clear, unambiguous names
 * @param {string} given - The path key (e.g. "/users/{id}/items")
 * @param {object} options - Function options
 * @param {string} options.ambiguous-words - Comma-separated list of ambiguous words
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  const results = [];

  if (typeof given !== 'string') {
    return results;
  }

  // Default ambiguous words (from the original regex)
  const defaultAmbiguousWords = [
    'elements', 'instances', 'resources', 'values', 'terms', 'objects', 'items'
  ];

  // Parse custom ambiguous words from options if provided
  let ambiguousWords = defaultAmbiguousWords;
  if (options && options['ambiguous-words']) {
    ambiguousWords = options['ambiguous-words']
      .split(',')
      .map(w => w.trim().toLowerCase())
      .filter(Boolean);
  }

  // Split path into segments and filter out parameters
  const segments = given
    .split('/')
    .filter(segment => {
      // Keep only static segments (not empty, not {param})
      return segment.length > 0 && !segment.startsWith('{');
    });

  // Check each segment for ambiguous words
  for (const segment of segments) {
    const lowerSegment = segment.toLowerCase();
    for (const word of ambiguousWords) {
      if (lowerSegment.includes(word)) {
        results.push({
          message: `${context.rule.message || 'OAR032'}: Path segment '${segment}' is ambiguous. Avoid using words like '${word}' in resource names.`,
          path: context.path
        });
        break; // Report only once per segment
      }
    }
  }

  return results;
};
