import Model from '@/models/Model'

export default class SceneGroup extends Model {
  /**
   * 씬그룹 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.id = json.id
    this.priority = json.priority
    this.name = json.name
  }
}
