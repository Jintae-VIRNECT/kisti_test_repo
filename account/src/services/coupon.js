import { api } from '@/plugins/axios'
import Coupon from '@/models/coupon/Coupon'
import profileService from '@/services/profile'
import workspaceService from '@/services/workspace'

export default {
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
  async useCoupon(couponId) {
    const data = await api('USE_COUPON', {
      params: {
        couponId,
        userId: profileService.getMyProfile().uuid,
        workspaceId: workspaceService
          .getMyWorkspaces()
          .find(workspace => workspace.role === 'MASTER').uuid,
      },
    })
    return data
  },
}
