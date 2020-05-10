package com.virnect.license.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLicenseProduct is a Querydsl query type for LicenseProduct
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLicenseProduct extends EntityPathBase<LicenseProduct> {

    private static final long serialVersionUID = -2073009993L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLicenseProduct licenseProduct = new QLicenseProduct("licenseProduct");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final QCoupon coupon;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<License, QLicense> licenseList = this.<License, QLicense>createList("licenseList", License.class, QLicense.class, PathInits.DIRECT2);

    public final QLicensePlan licensePlan;

    public final QLicenseType licenseType;

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final QProduct product;

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QLicenseProduct(String variable) {
        this(LicenseProduct.class, forVariable(variable), INITS);
    }

    public QLicenseProduct(Path<? extends LicenseProduct> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLicenseProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLicenseProduct(PathMetadata metadata, PathInits inits) {
        this(LicenseProduct.class, metadata, inits);
    }

    public QLicenseProduct(Class<? extends LicenseProduct> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new QCoupon(forProperty("coupon"), inits.get("coupon")) : null;
        this.licensePlan = inits.isInitialized("licensePlan") ? new QLicensePlan(forProperty("licensePlan"), inits.get("licensePlan")) : null;
        this.licenseType = inits.isInitialized("licenseType") ? new QLicenseType(forProperty("licenseType")) : null;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

