package com.virnect.license.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = -1740780433L;

    public static final QCoupon coupon = new QCoupon("coupon");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final StringPath callNumber = createString("callNumber");

    public final StringPath company = createString("company");

    public final StringPath companyCategory = createString("companyCategory");

    public final StringPath companyEmail = createString("companyEmail");

    public final StringPath companyService = createString("companyService");

    public final StringPath companySite = createString("companySite");

    public final NumberPath<Integer> companyWorker = createNumber("companyWorker", Integer.class);

    public final StringPath content = createString("content");

    public final EnumPath<CouponPeriodType> couponPeriodType = createEnum("couponPeriodType", CouponPeriodType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath department = createString("department");

    public final NumberPath<Long> duration = createNumber("duration", Long.class);

    public final DateTimePath<java.time.LocalDateTime> expiredDate = createDateTime("expiredDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<Status> marketInfoReceive = createEnum("marketInfoReceive", Status.class);

    public final EnumPath<Status> personalInfoPolicy = createEnum("personalInfoPolicy", Status.class);

    public final StringPath position = createString("position");

    public final EnumPath<CouponStatus> status = createEnum("status", CouponStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath userId = createString("userId");

    public QCoupon(String variable) {
        super(Coupon.class, forVariable(variable));
    }

    public QCoupon(Path<? extends Coupon> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCoupon(PathMetadata metadata) {
        super(Coupon.class, metadata);
    }

}

