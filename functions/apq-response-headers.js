module.exports = (responseNode, options = {}, context) => {
  if (!responseNode || typeof responseNode !== 'object') {
    return [];
  }

  const results = [];

  const {
    'mandatory-headers': mandatoryHeadersOpt = '',
    'allowed-headers': allowedHeadersOpt = '',
    'included-response-codes': includedCodesOpt = '*',
    'excluded-response-codes': excludedCodesOpt = '',
    'path-exclusions': pathExclusionsOpt = '',
  } = options;

  const mandatoryHeaders = mandatoryHeadersOpt
    .split(',')
    .map(h => h.toLowerCase().trim())
    .filter(Boolean);

  const allowedHeaders = allowedHeadersOpt
    .split(',')
    .map(h => h.toLowerCase().trim())
    .filter(Boolean);

  const includedCodes = includedCodesOpt
    .split(',')
    .map(c => c.trim())
    .filter(Boolean);

  const excludedCodes = excludedCodesOpt
    .split(',')
    .map(c => c.trim())
    .filter(Boolean);

  const pathExclusions = pathExclusionsOpt
    .split(',')
    .map(p => p.trim())
    .filter(Boolean);

  const path = context.path?.find(p => typeof p === 'string' && p.startsWith('/'));
  const responseCode = context.path?.[context.path.length - 1];

  if (path && pathExclusions.includes(path)) {
    return [];
  }

  if (excludedCodes.includes(String(responseCode))) {
    return [];
  }

  if (!includedCodes.includes('*') && !includedCodes.includes(String(responseCode))) {
    return [];
  }

  let headers = responseNode.headers;

  if (!headers && responseNode.$ref && context.document?.resolved) {
    try {
      const ref = responseNode.$ref.replace(/^#\//, '').split('/');
      let resolved = context.document.resolved;
      for (const part of ref) {
        resolved = resolved?.[part];
      }
      headers = resolved?.headers;
    } catch (_) {
    }
  }

  const headerNames = headers && typeof headers === 'object'
    ? Object.keys(headers).map(h => h.toLowerCase().trim())
    : [];
  const ruleCode = context.rule.name.split(':').pop();

  if (mandatoryHeaders.length > 0) {
    const missing = mandatoryHeaders.filter(h => !headerNames.includes(h));
    if (missing.length > 0) {
      results.push({
        message: `${ruleCode}: Headers [${missing.join(', ')}] are required`,
      });
    }
  }

  if (allowedHeaders.length > 0 && headerNames.length > 0) {
    const forbidden = headerNames.filter(h => !allowedHeaders.includes(h));
    if (forbidden.length > 0) {
      results.push({
        message: `${ruleCode}: Header not allowed`,
      });
    }
  }

  return results;
};