package com.virnect.license.dao;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-License
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.14
 */
public interface CouponCustomRepository {
    boolean hasAlreadyGenerateEventCoupon(String userId);
}
