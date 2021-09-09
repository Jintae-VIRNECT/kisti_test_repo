import Model from '@/models/Model'

/**
 * child 객체의 형태를 변경하는 함수.
 * @param {Object} obj
 * @returns {Object} properties
 */
function convert(obj) {
  return Object.values(obj).map(({ objectName, objectChildList }) => {
    return {
      label: objectName,
      children: objectChildList,
    }
  })
}

/**
 * 씬그룹 > 씬 > 텍스트의 tree 구조를 가질 수 있도록 만들어주는 재귀함수
 * @param {Object} obj
 * @param {Number} depth
 * @returns {Object} properties
 */
function recursion(obj, depth) {
  depth--
  // convert 함수를 통해, obj 객체의 프로퍼티를 변형한다.
  const list = convert(obj)
  list.forEach(prop => {
    // depth 숫자가 남아있고, children 인 객체가 남아있으면 재귀를 돈다.
    if (depth && prop.children) {
      prop.children = recursion(prop.children, depth)
    }
  })
  return list
}

export default class Properties extends Model {
  constructor(json) {
    super()
    this._json = json
  }

  /**
   * properties 데이터의 형변환을 완료한 트리 데이터를 반환한다.
   * @param {Object} json
   * @returns {Object} properties
   */
  tree(depth = 3) {
    return recursion(this._json.propertyObjectList, depth)
  }
}
