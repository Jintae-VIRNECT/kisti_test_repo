package com.virnect.license.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = -1740780433L;

    private static final PathInits INITS = PathInits.DIRECT2;

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

    public final ListPath<CouponProduct, QCouponProduct> couponProductList = this.<CouponProduct, QCouponProduct>createList("couponProductList", CouponProduct.class, QCouponProduct.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath department = createString("department");

    public final NumberPath<Long> duration = createNumber("duration", Long.class);

    public final DateTimePath<java.time.LocalDateTime> expiredDate = createDateTime("expiredDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QLicensePlan licensePlan;

    public final EnumPath<Status> marketInfoReceive = createEnum("marketInfoReceive", Status.class);

    public final StringPath name = createString("name");

    public final EnumPath<Status> personalInfoPolicy = createEnum("personalInfoPolicy", Status.class);

    public final StringPath position = createString("position");

    public final DateTimePath<java.time.LocalDateTime> registerDate = createDateTime("registerDate", java.time.LocalDateTime.class);

    public final StringPath serialKey = createString("serialKey");

    public final EnumPath<CouponStatus> status = createEnum("status", CouponStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath userId = createString("userId");

    public QCoupon(String variable) {
        this(Coupon.class, forVariable(variable), INITS);
    }

    public QCoupon(Path<? extends Coupon> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCoupon(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCoupon(PathMetadata metadata, PathInits inits) {
        this(Coupon.class, metadata, inits);
    }

    public QCoupon(Class<? extends Coupon> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.licensePlan = inits.isInitialized("licensePlan") ? new QLicensePlan(forProperty("licensePlan"), inits.get("licensePlan")) : null;
    }

}

