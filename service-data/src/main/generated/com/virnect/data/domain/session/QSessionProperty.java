package com.virnect.data.domain.session;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSessionProperty is a Querydsl query type for SessionProperty
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSessionProperty extends EntityPathBase<SessionProperty> {

    private static final long serialVersionUID = 1777503321L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSessionProperty sessionProperty = new QSessionProperty("sessionProperty");

    public final com.virnect.data.domain.QBaseTimeEntity _super = new com.virnect.data.domain.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath defaultOutputMode = createString("defaultOutputMode");

    public final StringPath defaultRecordingLayout = createString("defaultRecordingLayout");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath keepalive = createBoolean("keepalive");

    public final StringPath mediaMode = createString("mediaMode");

    public final StringPath publisherId = createString("publisherId");

    public final BooleanPath recording = createBoolean("recording");

    public final StringPath recordingMode = createString("recordingMode");

    public final com.virnect.data.domain.room.QRoom room;

    public final EnumPath<SessionType> sessionType = createEnum("sessionType", SessionType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QSessionProperty(String variable) {
        this(SessionProperty.class, forVariable(variable), INITS);
    }

    public QSessionProperty(Path<? extends SessionProperty> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSessionProperty(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSessionProperty(PathMetadata metadata, PathInits inits) {
        this(SessionProperty.class, metadata, inits);
    }

    public QSessionProperty(Class<? extends SessionProperty> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.room = inits.isInitialized("room") ? new com.virnect.data.domain.room.QRoom(forProperty("room"), inits.get("room")) : null;
    }

}

