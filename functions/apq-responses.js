
module.exports = (given, options, context) => {
  const result = [];
  if (!given) return result;

  if (options.verbs !== '*' && !Object.keys(given).some(key => options.verbs.includes(key))) {
    return result;
  }

  Object.keys(given).forEach(element => {
    const verb = given[element];
    if (!verb.responses) return;
    if (options.parameters && !verb.parameters) return;
    if (options.parameters && !verb.parameters.some(p => options.parameters.includes(p.in))) return;

    if (Object.keys(verb.responses).some(response => options.responses.includes(response))) return;

    const path = context.path || [];
    result.push({
      message: context.rule.message,
      path: [...path, element]
    });
  });

  return result;
};
