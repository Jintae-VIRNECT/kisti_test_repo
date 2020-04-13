import api from '@/api/gateway'
import Coupon from '@/models/coupon/Coupon'
import profileService from '@/services/profile'

export default {
  async getCouponList() {
    const myCouponInfoList = [0, 1, 2]
    // const { myCouponInfoList } = await api('GET_COUPONS', {
    //   route: { userId: profileService.getMyProfile().uuid },
    // })
    return myCouponInfoList.map(coupon => new Coupon(coupon))
  },
}
