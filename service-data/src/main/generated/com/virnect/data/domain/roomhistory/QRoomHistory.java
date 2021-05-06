package com.virnect.data.domain.roomhistory;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoomHistory is a Querydsl query type for RoomHistory
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QRoomHistory extends EntityPathBase<RoomHistory> {

    private static final long serialVersionUID = -1446651574L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoomHistory roomHistory = new QRoomHistory("roomHistory");

    public final com.virnect.data.domain.QBaseTimeEntity _super = new com.virnect.data.domain.QBaseTimeEntity(this);

    public final DateTimePath<java.time.LocalDateTime> activeDate = createDateTime("activeDate", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final NumberPath<Long> durationSec = createNumber("durationSec", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath leaderId = createString("leaderId");

    public final StringPath licenseName = createString("licenseName");

    public final NumberPath<Integer> maxUserCount = createNumber("maxUserCount", Integer.class);

    public final ListPath<com.virnect.data.domain.member.MemberHistory, com.virnect.data.domain.member.QMemberHistory> memberHistories = this.<com.virnect.data.domain.member.MemberHistory, com.virnect.data.domain.member.QMemberHistory>createList("memberHistories", com.virnect.data.domain.member.MemberHistory.class, com.virnect.data.domain.member.QMemberHistory.class, PathInits.DIRECT2);

    public final StringPath profile = createString("profile");

    public final StringPath sessionId = createString("sessionId");

    public final com.virnect.data.domain.session.QSessionPropertyHistory sessionPropertyHistory;

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> unactiveDate = createDateTime("unactiveDate", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath workspaceId = createString("workspaceId");

    public QRoomHistory(String variable) {
        this(RoomHistory.class, forVariable(variable), INITS);
    }

    public QRoomHistory(Path<? extends RoomHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoomHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoomHistory(PathMetadata metadata, PathInits inits) {
        this(RoomHistory.class, metadata, inits);
    }

    public QRoomHistory(Class<? extends RoomHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sessionPropertyHistory = inits.isInitialized("sessionPropertyHistory") ? new com.virnect.data.domain.session.QSessionPropertyHistory(forProperty("sessionPropertyHistory"), inits.get("sessionPropertyHistory")) : null;
    }

}

