export const deepGet = (obj, keys) =>
  keys.reduce((xs, x) => {
    return xs && xs[x] !== null && xs[x] !== undefined ? xs[x] : null
  }, obj)
