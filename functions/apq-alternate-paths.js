
const isVariable = (part) => {
  return (part.startsWith('{') && part.endsWith('}'));
}

module.exports = (given, { except }, context) => {
  const result = [];
  const paths = given || [];
  if (paths.length === 0) return result;

  const parts = paths.substr(1).split('/');
  let previousIsVar = isVariable(parts.shift());
  if (previousIsVar) {
    return [{
      message: context.rule.message,
    }];
  }

  for (const part of parts) {
    if (except && except.includes(part)) {
      previousIsVar = true;
      continue;
    }

    const currentIsVariable = isVariable(part);
    if (currentIsVariable === previousIsVar) {
      return [{
        message: context.rule.message,
      }];
    }
    previousIsVar = currentIsVariable;
  }

  return result;
};
