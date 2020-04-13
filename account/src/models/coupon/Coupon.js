import Model from '@/models/Model'

export default class Coupon extends Model {
  constructor(json) {
    super()
    this.name = '2주 무료 사용 쿠폰' //json.name
    this.registerDate = new Date() //json.registerDate
    this.expireDate = new Date() //json.expireDate
    this.startDate = new Date() //json.startDate
    this.endDate = new Date() //json.endDate
    this.status = 'EXPIRED' //json.status
  }
}
