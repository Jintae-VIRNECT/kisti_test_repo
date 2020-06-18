import Model from '@/models/Model'

export default class Coupon extends Model {
  constructor(json) {
    super()
    this.id = json.id
    this.name = json.name
    this.registerDate = json.registerDate
    this.expiredDate = json.expiredDate
    this.startDate = json.startDate
    this.endDate = json.endDate
    this.status = json.status
  }
}

export const status = [
  { value: 'USE', label: 'coupon.status.use' },
  { value: 'UNUSE', label: 'coupon.status.unuse' },
  { value: 'EXPIRED', label: 'coupon.status.expired' },
]
