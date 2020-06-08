import Model from '@/models/Model'

export default class Coupon extends Model {
  constructor(json) {
    super()
    this.no = 1
    this.way = '신용카드'
    this.price = 300000
    this.paidDate = new Date()
  }
}
