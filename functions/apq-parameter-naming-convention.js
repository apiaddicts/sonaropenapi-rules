const NAMING_REGEX = {
  "snake_case": /^[a-z0-9_$]*$/,
  "kebab-case": /^[a-z0-9-]*$/,
  "camelCase": /^[a-z]+([A-Z][a-z]+)*([A-Z])?$/,
  "UpperCamelCase": /^[A-Z][a-z]+(?:[A-Z][a-z]+)*$/
};

const NAME_EXCEPTIONS = new Set([
  "$init", "$start", "$limit", "$total", "$expand", "$orderby", "$select", "$exclude", "$filter"
]);

const PARAM_REGEX = /\{([^}{]*)\}/g;

/**
 * @param {string} given
 * @param {object} options
 * @param {string} options.namingConvention
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  if (!given) return [];

  const convention = options.namingConvention || "snake_case";
  const regex = NAMING_REGEX[convention];

  if (!regex) {
    throw new Error(
      `OAR012: Unknown naming convention "${convention}". ` +
      `Allowed values: ${Object.keys(NAMING_REGEX).join(", ")}`
    );
  }

  if (NAME_EXCEPTIONS.has(given)) {
    return [];
  }

  let name = given;

  // Strip parameter names
  name = name.replace(PARAM_REGEX, (_, match) => match); // Keep only the parameter name without braces

  switch (convention) {
    case "camelCase":
    case "UpperCamelCase":
      name = name.replace(/\//g, "");
      if (name.includes("_") || name.includes("-")) {
        return [
          {
            message: `OAR012: "${given}" must follow ${convention}`,
            path: context.path
          }
        ];
      }
      break;

    case "kebab-case":
      name = name.replace(/\//g, "-");
      break;

    case "snake_case":
    default:
      name = name.replace(/\//g, "_");
      break;
  }

  if (!regex.test(name)) {
    return [
      {
        message: `OAR012: "${given}" must follow ${convention}`,
        path: context.path
      }
    ];
  }

  return [];
};