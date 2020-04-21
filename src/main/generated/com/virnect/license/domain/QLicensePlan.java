package com.virnect.license.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLicensePlan is a Querydsl query type for LicensePlan
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLicensePlan extends EntityPathBase<LicensePlan> {

    private static final long serialVersionUID = 1477954657L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLicensePlan licensePlan = new QLicensePlan("licensePlan");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final QCoupon coupon;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<LicenseProduct, QLicenseProduct> licenseProductList = this.<LicenseProduct, QLicenseProduct>createList("licenseProductList", LicenseProduct.class, QLicenseProduct.class, PathInits.DIRECT2);

    public final EnumPath<PlanStatus> planStatus = createEnum("planStatus", PlanStatus.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath userId = createString("userId");

    public final StringPath workspaceId = createString("workspaceId");

    public QLicensePlan(String variable) {
        this(LicensePlan.class, forVariable(variable), INITS);
    }

    public QLicensePlan(Path<? extends LicensePlan> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLicensePlan(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLicensePlan(PathMetadata metadata, PathInits inits) {
        this(LicensePlan.class, metadata, inits);
    }

    public QLicensePlan(Class<? extends LicensePlan> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new QCoupon(forProperty("coupon"), inits.get("coupon")) : null;
    }

}

