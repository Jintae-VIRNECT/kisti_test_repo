import Model from '@/models/Model'

export default class ReportItem extends Model {
  /**
   * 리포트 아이템 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.id = json.id
    this.title = json.title
    this.answer = json.answer
    this.type = json.type
    this.priority = json.priority
    this.photoFilePath = json.photoFilePath
  }
}
