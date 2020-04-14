import api from '@/api/gateway'
import Coupon from '@/models/coupon/Coupon'
import profileService from '@/services/profile'

import switchPromise from '@/test/switchPromise'

export default {
  async getCouponList() {
    const myCouponInfoList = [0, 1, 2]
    // const { myCouponInfoList } = await api('GET_COUPONS', {
    //   route: { userId: profileService.getMyProfile().uuid },
    // })
    return myCouponInfoList.map(coupon => new Coupon(coupon))
  },
  async addCouponCode(code) {
    const data = await api('ADD_COUPON', {
      couponSerialKey: code,
      userId: profileService.getMyProfile().uuid,
    })
    return data
  },
  async useCoupon() {
    return switchPromise()
  },
}
