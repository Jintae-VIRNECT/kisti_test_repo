import Model from '@/models/Model'

export default class Coupon extends Model {
  constructor() {
    super()
    this.name = '2주 무료 사용 쿠폰'
    this.registerDate = '20.02.30'
    this.expireDate = '21.12.30'
    this.startDate = '20.03.06'
    this.endDate = '20.03.09'
    this.state = 'wait'
  }
}
