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
    this.status = json.StateM
    this.applyItemId = json.ApplyItemID
    this.applyItemName = json.ApplyItemName
  }
}

export const status = [
  { value: 'USE', label: 'coupon.status.use' },
  { value: 'UNUSE', label: 'coupon.status.unuse' },
  { value: 'EXPIRED', label: 'coupon.status.expired' },
  { value: '사용', label: 'coupon.status.use' },
  { value: '사용대기', label: 'coupon.status.unuse' },
  { value: '만료', label: 'coupon.status.expired' },
]
