export default class SceneGroup {
  /**
   * 씬그룹 구조
   * @param {Object} json
   */
  constructor(json) {
    this.id = json.id
    this.priority = json.priority
    this.name = json.name
  }
}
