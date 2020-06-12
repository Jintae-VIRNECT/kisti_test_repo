import { api } from '@/plugins/axios'
import Coupon from '@/models/coupon/Coupon'
import profileService from '@/services/profile'
import workspaceService from '@/services/workspace'

export default {
  async getCouponList(searchParams) {
    const { myCouponInfoList, pageMeta } = await api('GET_COUPONS', {
      route: { userId: profileService.getMyProfile().uuid },
      params: searchParams,
    })
    return {
      list: myCouponInfoList.map(coupon => new Coupon(coupon)),
      total: pageMeta.totalElements,
    }
  },
  async addCouponCode(code) {
    const data = await api('ADD_COUPON', {
      params: {
        couponSerialKey: code,
        userId: profileService.getMyProfile().uuid,
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
