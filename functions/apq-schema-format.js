// Check that format is valid for a schema type.

/**
 * @param {object} given
 * @param {object} options
 * @param {object} options.formats
 * @param {Array<string>} options.formats.string
 * @param {Array<string>} options.formats.number
 * @param {Array<string>} options.formats.integer
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = function checkTypeAndFormat(given, options, context) {
  if (given === null || typeof given !== "object") {
    return [];
  }

  const errors = [];
  const path = context.path || [];

  if (given.type === "string" && options.formats.string) {
    if (given.format) {
      if (!options.formats.string.includes(given.format)) {
        errors.push({
          message: `Schema with type string has unrecognized format: ${given.format}`,
          path: [...path, "format"]
        });
      }
    }
  } else if (given.type === "integer" && options.formats.integer) {
    if (given.format) {
      if (!options.formats.integer.includes(given.format)) {
        errors.push({
          message: `Schema with type integer has unrecognized format: ${given.format}`,
          path: [...path, "format"]
        });
      }
    } else {
      errors.push({
        message: "Schema with type integer should specify format",
        path
      });
    }
  } else if (given.type === "number" && options.formats.number) {
    if (given.format) {
      if (!options.formats.number.includes(given.format)) {
        errors.push({
          message: `Schema with type number has unrecognized format: ${given.format}`,
          path: [...path, "format"]
        });
      }
    } else {
      errors.push({
        message: "Schema with type number should specify format",
        path
      });
    }
  } else if (given.type === "boolean") {
    if (given.format) {
      errors.push({
        message: "Schema with type boolean should not specify format",
        path: [...path, "format"]
      });
    }
  } else if (given.properties && typeof given.properties === "object") {
    // eslint-disable-next-line no-restricted-syntax
    for (const [key, value] of Object.entries(given.properties)) {
      errors.push(...checkTypeAndFormat(value, options, {
        path: [...path, "properties", key],
        document: context.document,
        documentInventory: context.documentInventory,
        rule: context.rule
      }));
    }
  }

  if (given.type === "array") {
    errors.push(...checkTypeAndFormat(given.items, options, {
      path: [...path, "items"],
      document: context.document,
      documentInventory: context.documentInventory,
      rule: context.rule
    }));
  }

  if (given.allOf && Array.isArray(given.allOf)) {
    // eslint-disable-next-line no-restricted-syntax
    for (const [index, value] of given.allOf.entries()) {
      errors.push(...checkTypeAndFormat(value, options, {
        path: [...path, "allOf", index],
        document: context.document,
        documentInventory: context.documentInventory,
        rule: context.rule
      }));
    }
  }

  return errors;
};
