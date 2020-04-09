package com.virnect.license.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCouponProduct is a Querydsl query type for CouponProduct
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCouponProduct extends EntityPathBase<CouponProduct> {

    private static final long serialVersionUID = -464679552L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCouponProduct couponProduct = new QCouponProduct("couponProduct");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final QCoupon coupon;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QLicenseType licenseType;

    public final QProduct product;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QCouponProduct(String variable) {
        this(CouponProduct.class, forVariable(variable), INITS);
    }

    public QCouponProduct(Path<? extends CouponProduct> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCouponProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCouponProduct(PathMetadata metadata, PathInits inits) {
        this(CouponProduct.class, metadata, inits);
    }

    public QCouponProduct(Class<? extends CouponProduct> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new QCoupon(forProperty("coupon")) : null;
        this.licenseType = inits.isInitialized("licenseType") ? new QLicenseType(forProperty("licenseType")) : null;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

