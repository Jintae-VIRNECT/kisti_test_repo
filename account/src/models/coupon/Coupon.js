import Model from '@/models/Model'

export default class Coupon extends Model {
  constructor(json) {
    super()
    this.no = json.CouponNo
    this.name = json.CouponName
    this.typeCode = json.CouponTypeCode
    this.typeName = json.CouponTypeCodeM
    this.registerDate = json.RegYMD
    this.expiredDate = json.ExpYMD
    this.usedDate = json.UseYMD
    this.applyItemId = json.ApplyItemID
    this.applyItemName = json.ApplyItemName
    this.status = json.StateM
  }
}

export const status = [
  { value: 'USE', label: 'coupon.status.use' },
  { value: 'UNUSE', label: 'coupon.status.unuse' },
  { value: 'EXPIRED', label: 'coupon.status.expired' },
]
