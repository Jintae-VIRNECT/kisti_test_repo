package com.virnect.license.dao;

import com.virnect.license.domain.Coupon;
import com.virnect.license.domain.QCoupon;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-License
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.14
 */
public class CouponCustomRepositoryImpl extends QuerydslRepositorySupport implements CouponCustomRepository {
    public CouponCustomRepositoryImpl() {
        super(Coupon.class);
    }

    @Override
    public boolean hasAlreadyGenerateEventCoupon(String userId) {
        return false;
    }
}
