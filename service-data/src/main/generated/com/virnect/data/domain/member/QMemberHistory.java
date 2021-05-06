package com.virnect.data.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberHistory is a Querydsl query type for MemberHistory
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QMemberHistory extends EntityPathBase<MemberHistory> {

    private static final long serialVersionUID = 1380152480L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberHistory memberHistory = new QMemberHistory("memberHistory");

    public final com.virnect.data.domain.QBaseTimeEntity _super = new com.virnect.data.domain.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final EnumPath<com.virnect.data.domain.DeviceType> deviceType = createEnum("deviceType", com.virnect.data.domain.DeviceType.class);

    public final NumberPath<Long> durationSec = createNumber("durationSec", Long.class);

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final BooleanPath historyDeleted = createBoolean("historyDeleted");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<MemberType> memberType = createEnum("memberType", MemberType.class);

    public final com.virnect.data.domain.roomhistory.QRoomHistory roomHistory;

    public final StringPath sessionId = createString("sessionId");

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath uuid = createString("uuid");

    public final StringPath workspaceId = createString("workspaceId");

    public QMemberHistory(String variable) {
        this(MemberHistory.class, forVariable(variable), INITS);
    }

    public QMemberHistory(Path<? extends MemberHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberHistory(PathMetadata metadata, PathInits inits) {
        this(MemberHistory.class, metadata, inits);
    }

    public QMemberHistory(Class<? extends MemberHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.roomHistory = inits.isInitialized("roomHistory") ? new com.virnect.data.domain.roomhistory.QRoomHistory(forProperty("roomHistory"), inits.get("roomHistory")) : null;
    }

}

