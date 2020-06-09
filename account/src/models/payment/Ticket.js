import Model from '@/models/Model'

export default class Ticket extends Model {
  constructor(json) {
    super()
    this.product = 'REMOTE'
    this.name = 'VIRNECT Remote Basic Plan 1인 이용권'
    this.count = 1
    this.price = 300000
  }
}
