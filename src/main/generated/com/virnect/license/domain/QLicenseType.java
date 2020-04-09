package com.virnect.license.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLicenseType is a Querydsl query type for LicenseType
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLicenseType extends EntityPathBase<LicenseType> {

    private static final long serialVersionUID = 1478086770L;

    public static final QLicenseType licenseType = new QLicenseType("licenseType");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QLicenseType(String variable) {
        super(LicenseType.class, forVariable(variable));
    }

    public QLicenseType(Path<? extends LicenseType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLicenseType(PathMetadata metadata) {
        super(LicenseType.class, metadata);
    }

}

