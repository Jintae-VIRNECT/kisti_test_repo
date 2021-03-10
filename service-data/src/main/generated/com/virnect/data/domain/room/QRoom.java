package com.virnect.data.domain.room;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoom is a Querydsl query type for Room
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QRoom extends EntityPathBase<Room> {

    private static final long serialVersionUID = -2081840652L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoom room = new QRoom("room");

    public final com.virnect.data.domain.QBaseTimeEntity _super = new com.virnect.data.domain.QBaseTimeEntity(this);

    public final DateTimePath<java.time.LocalDateTime> activeDate = createDateTime("activeDate", java.time.LocalDateTime.class);

    public final BooleanPath audioRestrictedMode = createBoolean("audioRestrictedMode");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath leaderId = createString("leaderId");

    public final StringPath licenseName = createString("licenseName");

    public final NumberPath<Integer> maxUserCount = createNumber("maxUserCount", Integer.class);

    public final ListPath<com.virnect.data.domain.member.Member, com.virnect.data.domain.member.QMember> members = this.<com.virnect.data.domain.member.Member, com.virnect.data.domain.member.QMember>createList("members", com.virnect.data.domain.member.Member.class, com.virnect.data.domain.member.QMember.class, PathInits.DIRECT2);

    public final StringPath profile = createString("profile");

    public final EnumPath<RoomStatus> roomStatus = createEnum("roomStatus", RoomStatus.class);

    public final StringPath sessionId = createString("sessionId");

    public final com.virnect.data.domain.session.QSessionProperty sessionProperty;

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final BooleanPath videoRestrictedMode = createBoolean("videoRestrictedMode");

    public final StringPath workspaceId = createString("workspaceId");

    public QRoom(String variable) {
        this(Room.class, forVariable(variable), INITS);
    }

    public QRoom(Path<? extends Room> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoom(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoom(PathMetadata metadata, PathInits inits) {
        this(Room.class, metadata, inits);
    }

    public QRoom(Class<? extends Room> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sessionProperty = inits.isInitialized("sessionProperty") ? new com.virnect.data.domain.session.QSessionProperty(forProperty("sessionProperty"), inits.get("sessionProperty")) : null;
    }

}

