import Model from '@/models/Model'

function convert(obj) {
  return Object.values(obj).map(({ PropertyInfo, Child }) => {
    return {
      id: PropertyInfo.identifier,
      label: PropertyInfo.ComponentName,
      children: Child,
    }
  })
}
function recursion(obj, depth) {
  depth--
  const list = convert(obj)
  list.forEach(prop => {
    if (depth && prop.children) {
      prop.children = recursion(prop.children, depth)
    }
  })
  return list
}

export default class Properties extends Model {
  /**
   * 씬그룹 구조
   * @param {Object} json
   */
  constructor(str) {
    super()
    this._json = JSON.parse(str)
  }

  tree(depth = 3) {
    return recursion(this._json.PropertyInfo, depth)
  }
}
