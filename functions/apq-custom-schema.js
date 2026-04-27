// options:
//   except: 'array<string>'
//   schema: object

function DeepCompare() {
  var i, l, leftChain, rightChain;

  function Objects(x, y, required = []) {
    let p;

    // Quick checking of one object being a subset of another.
    // todo: cache the structure of arguments[0] for performance
    for (p in y) {
      if (y.hasOwnProperty(p) !== x.hasOwnProperty(p)) {
        if (required.includes(p)) {
          return false;
        }
      } else if (typeof y[p] !== typeof x[p]) {
        return false;
      }
    }

    for (p in x) {
      if (y.hasOwnProperty(p) !== x.hasOwnProperty(p)) {
        if (required.includes(p)) {
          return false;
        }
        continue;
      } else if (typeof y[p] !== typeof x[p]) {
        return false;
      }

      switch (typeof x[p]) {
        case "object":
        case "function":
          leftChain.push(x);
          rightChain.push(y);

          if (!Objects(x[p], y[p], x[p].required || required)) {
            return false;
          }

          leftChain.pop();
          rightChain.pop();
          break;

        default:
          if (x[p] !== y[p]) {
            return false;
          }
          break;
      }
    }

    return true;
  }

  for (i = 1, l = arguments.length; i < l; i++) {
    leftChain = []; //Todo: this can be cached
    rightChain = [];

    if (!Objects(arguments[0], arguments[i], arguments[0].required || [])) {
      return false;
    }
  }

  return true;
}

/**
 * @param {Array<string>} given
 * @param {object} options
 * @param {Array<string>} options.except
 * @param {object} options.schema
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (given, options, context) => {
  const result = [];
  const paths = given || [];
  if (paths.length === 0) return result;
  if (options.except.includes(String(context.path[1]))) return result;

  if (DeepCompare(options.schema, given)) return result;

  return [{
    message: context.rule.message,
  }];
};
