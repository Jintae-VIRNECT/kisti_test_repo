package com.virnect.data.domain.session;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSessionPropertyHistory is a Querydsl query type for SessionPropertyHistory
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSessionPropertyHistory extends EntityPathBase<SessionPropertyHistory> {

    private static final long serialVersionUID = -1105655301L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSessionPropertyHistory sessionPropertyHistory = new QSessionPropertyHistory("sessionPropertyHistory");

    public final com.virnect.data.domain.QBaseTimeEntity _super = new com.virnect.data.domain.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath defaultOutputMode = createString("defaultOutputMode");

    public final StringPath defaultRecordingLayout = createString("defaultRecordingLayout");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath keepalive = createBoolean("keepalive");

    public final StringPath mediaMode = createString("mediaMode");

    public final BooleanPath recording = createBoolean("recording");

    public final StringPath recordingMode = createString("recordingMode");

    public final com.virnect.data.domain.roomhistory.QRoomHistory roomHistory;

    public final EnumPath<SessionType> sessionType = createEnum("sessionType", SessionType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QSessionPropertyHistory(String variable) {
        this(SessionPropertyHistory.class, forVariable(variable), INITS);
    }

    public QSessionPropertyHistory(Path<? extends SessionPropertyHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSessionPropertyHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSessionPropertyHistory(PathMetadata metadata, PathInits inits) {
        this(SessionPropertyHistory.class, metadata, inits);
    }

    public QSessionPropertyHistory(Class<? extends SessionPropertyHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.roomHistory = inits.isInitialized("roomHistory") ? new com.virnect.data.domain.roomhistory.QRoomHistory(forProperty("roomHistory"), inits.get("roomHistory")) : null;
    }

}

