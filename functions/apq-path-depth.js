/**
 * @param {string} targetVal
 * @param {object} options
 * @param {import('@stoplight/spectral-core').RulesetFunctionContext} context
 */
module.exports = (targetVal, options, context) => {
  const maxDepth = options.maxDepth ?? 3
  const ignore = options.ignoreSegments ?? []

  const segments = targetVal
    .split('/')
    .filter(Boolean)
    .filter(segment => !ignore.includes(segment))

  if (segments.length > maxDepth) {
    return [
      { message: context.rule.message }
    ]
  }
}