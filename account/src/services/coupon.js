import Coupon from '@/models/coupon/Coupon'

export default {
  getCouponList() {
    const data = [0, 1, 2, 3, 4]
    return data.map(coupon => new Coupon(coupon))
  },
}
