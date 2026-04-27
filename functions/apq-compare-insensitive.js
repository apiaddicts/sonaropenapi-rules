/**
 * Enhanced semantic text comparison function
 * Detects both exact matches and semantic similarity
 * @param {object} given
 * @param {object} options
 * @param {string} options.property
 * @param {string} options.equalTo
 * @param {string} options.result
 * @param {number} options.threshold       - Similarity threshold (0.0-1.0), default 0.6
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  const errors = [];
  if (!given) {
    return errors;
  }

  const propA = given[options.property];
  const propB = given[options.equalTo];

  if (typeof propA !== 'string' || typeof propB !== 'string') {
    return errors;
  }

  if (options.result !== 'falsy') {
    return errors;
  }

  const threshold = options.threshold || 0.6;

  // Exact match (case-insensitive)
  if (propA.trim().toUpperCase() === propB.trim().toUpperCase()) {
    errors.push({
      message: context.rule.message,
      path: [...context.path, options.property]
    });
    return errors;
  }

  // Semantic similarity check
  const similarity = calculateSimilarity(propA, propB);
  if (similarity >= threshold) {
    errors.push({
      message: `${context.rule.message} (${Math.round(similarity * 100)}% similar)`,
      path: [...context.path, options.property]
    });
  }

  return errors;
};

/**
 * Calculate semantic similarity between two strings
 * Uses token overlap and cosine similarity
 */
function calculateSimilarity(str1, str2) {
  const tokens1 = tokenize(str1);
  const tokens2 = tokenize(str2);

  if (tokens1.length === 0 || tokens2.length === 0) {
    return 0;
  }

  // Calculate Jaccard similarity (token overlap)
  const intersection = new Set([...tokens1].filter(x => tokens2.includes(x)));
  const union = new Set([...tokens1, ...tokens2]);
  const jaccardSimilarity = intersection.size / union.size;

  // Calculate word order similarity
  const order1 = tokens1.join(' ');
  const order2 = tokens2.join(' ');
  const levenshteinDist = levenshteinDistance(order1, order2);
  const maxLen = Math.max(order1.length, order2.length);
  const orderSimilarity = maxLen === 0 ? 0 : 1 - levenshteinDist / maxLen;

  // Weighted average
  return jaccardSimilarity * 0.6 + orderSimilarity * 0.4;
}

/**
 * Tokenize string into meaningful words with stemming
 */
function tokenize(str) {
  return str
    .toLowerCase()
    .replace(/[^\w\s]/g, '')
    .split(/\s+/)
    .filter(token => token.length > 2) // Ignore very short words
    .map(token => stem(token)) // Apply stemming
    .sort();
}

/**
 * Simple word stemming (removes common suffixes)
 */
function stem(word) {
  // Remove common verb/noun suffixes
  return word
    .replace(/s$/, '')           // plural
    .replace(/ed$/, '')          // past tense
    .replace(/ing$/, '')         // continuous
    .replace(/er$/, '')          // agent
    .replace(/est$/, '');        // superlative
}

/**
 * Calculate Levenshtein distance between two strings
 */
function levenshteinDistance(str1, str2) {
  const len1 = str1.length;
  const len2 = str2.length;
  const matrix = Array(len2 + 1).fill(null).map(() => Array(len1 + 1).fill(0));

  for (let i = 0; i <= len1; i++) matrix[0][i] = i;
  for (let j = 0; j <= len2; j++) matrix[j][0] = j;

  for (let j = 1; j <= len2; j++) {
    for (let i = 1; i <= len1; i++) {
      const cost = str1[i - 1] === str2[j - 1] ? 0 : 1;
      matrix[j][i] = Math.min(
        matrix[j][i - 1] + 1,
        matrix[j - 1][i] + 1,
        matrix[j - 1][i - 1] + cost
      );
    }
  }

  return matrix[len2][len1];
}

