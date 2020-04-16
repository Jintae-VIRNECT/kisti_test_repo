import Model from '@/models/Model'

export default class Coupon extends Model {
  constructor(json) {
    super()
    this.id = json.id
    this.name = json.name
    this.registerDate = json.registerDate
    this.expireDate = json.expiredDate
    this.startDate = json.startDate
    this.endDate = json.endDate
    this.status = json.status
  }
}
