package com.virnect.data.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberPermission is a Querydsl query type for MemberPermission
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QMemberPermission extends EntityPathBase<MemberPermission> {

    private static final long serialVersionUID = -2067944893L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberPermission memberPermission = new QMemberPermission("memberPermission");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.virnect.data.domain.QPermission permission;

    public QMemberPermission(String variable) {
        this(MemberPermission.class, forVariable(variable), INITS);
    }

    public QMemberPermission(Path<? extends MemberPermission> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberPermission(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberPermission(PathMetadata metadata, PathInits inits) {
        this(MemberPermission.class, metadata, inits);
    }

    public QMemberPermission(Class<? extends MemberPermission> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.permission = inits.isInitialized("permission") ? new com.virnect.data.domain.QPermission(forProperty("permission")) : null;
    }

}

