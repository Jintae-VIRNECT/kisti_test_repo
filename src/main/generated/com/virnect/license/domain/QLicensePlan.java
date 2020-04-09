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

    public static final QLicensePlan licensePlan = new QLicensePlan("licensePlan");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> expiredDate = createDateTime("expiredDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<License, QLicense> licenseList = this.<License, QLicense>createList("licenseList", License.class, QLicense.class, PathInits.DIRECT2);

    public final EnumPath<PlanStatus> planStatus = createEnum("planStatus", PlanStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath userId = createString("userId");

    public QLicensePlan(String variable) {
        super(LicensePlan.class, forVariable(variable));
    }

    public QLicensePlan(Path<? extends LicensePlan> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLicensePlan(PathMetadata metadata) {
        super(LicensePlan.class, metadata);
    }

}

