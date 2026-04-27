const DEFAULT_ALLOWED_PATTERNS = `
;get:^/[^/{}]+$
;get:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+$
;get:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+$
;get:^/[^/{}]+/(\\{[^/{}]+\\}|me)$
;get:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)$
;get:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)$

;post:^/[^/{}]+$
;post:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+$
;post:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+$
;post:^/[^/{}]+/get$
;post:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/get$
;post:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/get$
;post:^/[^/{}]+/delete$
;post:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/delete$
;post:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/delete$

;put:^/[^/{}]+/(\\{[^/{}]+\\}|me)$
;put:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)$
;put:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)$

;patch:^/[^/{}]+/(\\{[^/{}]+\\}|me)$
;patch:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)$
;patch:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)$

;delete:^/[^/{}]+/(\\{[^/{}]+\\}|me)$
;delete:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)$
;delete:^/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)/[^/{}]+/(\\{[^/{}]+\\}|me)$
`;

const SUPPORTED_VERBS = ['get', 'post', 'put', 'patch', 'delete'];
const CONFIG_ERROR_PREFIX = 'OAR018:';

const parseAllowedPatterns = (patterns) => {
  if (typeof patterns !== 'string') {
    throw new Error(
      `${CONFIG_ERROR_PREFIX} allowed-resources-paths must be a string`
    );
  }

  return patterns
    .split(';')
    .map(p => p.trim())
    .filter(Boolean)
    .map(entry => {
      const idx = entry.indexOf(':');

      if (idx === -1) {
        throw new Error(
          `${CONFIG_ERROR_PREFIX} Invalid entry "${entry}". Expected format "<verb>:<regex>"`
        );
      }

      const verb = entry.slice(0, idx).toLowerCase();
      const regexSource = entry.slice(idx + 1);

      if (!SUPPORTED_VERBS.includes(verb)) {
        throw new Error(
          `${CONFIG_ERROR_PREFIX} Unsupported HTTP verb "${verb}" in "${entry}"`
        );
      }

      try {
        return {
          verb,
          regex: new RegExp(regexSource),
        };
      } catch (err) {
        throw new Error(
          `${CONFIG_ERROR_PREFIX} Invalid regex for verb "${verb}": ${regexSource}. ${err.message}`
        );
      }
    });
};

module.exports = (given, functionOptions = {}, context) => {
  const results = [];
  if (!given || typeof given !== 'object') return results;

  let allowedPatterns;

  try {
    const patterns = functionOptions['allowed-resources-paths']?.trim()
        ? functionOptions['allowed-resources-paths']
        : DEFAULT_ALLOWED_PATTERNS;

    allowedPatterns = parseAllowedPatterns(patterns);
  } catch (error) {
    return [{
      message: error.message,
      path: context.path,
    }];
  }

  Object.entries(given).forEach(([path, operations]) => {
    Object.keys(operations || {}).forEach(verb => {
      const verbLower = verb.toLowerCase();
      if (!SUPPORTED_VERBS.includes(verbLower)) return;

      const isAllowed = allowedPatterns.some(p =>
        p.verb === verbLower && p.regex.test(path)
      );

      if (!isAllowed) {
        results.push({
          message: context.rule.message
            .replace('{{path}}', path)
            .replace('{{verb}}', verbLower.toUpperCase()),
          path: [...context.path, path, verb],
        });
      }
    });
  });

  return results;
};