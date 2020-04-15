package com.virnect.license.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLicense is a Querydsl query type for License
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLicense extends EntityPathBase<License> {

    private static final long serialVersionUID = 1079253272L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLicense license = new QLicense("license");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QLicensePlan licensePlan;

    public final StringPath serialKey = createString("serialKey");

    public final EnumPath<LicenseStatus> status = createEnum("status", LicenseStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QLicense(String variable) {
        this(License.class, forVariable(variable), INITS);
    }

    public QLicense(Path<? extends License> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLicense(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLicense(PathMetadata metadata, PathInits inits) {
        this(License.class, metadata, inits);
    }

    public QLicense(Class<? extends License> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.licensePlan = inits.isInitialized("licensePlan") ? new QLicensePlan(forProperty("licensePlan"), inits.get("licensePlan")) : null;
    }

}

