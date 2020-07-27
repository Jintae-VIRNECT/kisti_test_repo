import { api } from '@/plugins/axios'
import Coupon from '@/models/coupon/Coupon'
import profileService from '@/services/profile'

export default {
  /**
   * 쿠폰 리스트 조회
   * @param {Object} searchParams
   */
  async searchCoupons(searchParams = {}) {
    const { couponholdinfos, totalcnt } = await api('GET_PAY_COUPONS', {
      params: {
        userno: profileService.getMyProfile().userId,
        pagesize: 10,
        pageNo: searchParams.page || 1,
      },
    })
    return {
      list: couponholdinfos.map(coupon => new Coupon(coupon)),
      total: totalcnt,
    }
  },
  /**
   * 쿠폰 등록
   * @param {String} code
   */
  async addCouponCode(code) {
    const { userId, email, nickname } = profileService.getMyProfile()
    const data = await api('ADD_PAY_COUPON', {
      params: {
        userno: userId,
        userId: email,
        username: nickname,
        couponno: code,
      },
    })
    return data
  },
}
