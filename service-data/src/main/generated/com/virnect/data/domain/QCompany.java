package com.virnect.data.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCompany is a Querydsl query type for Company
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCompany extends EntityPathBase<Company> {

    private static final long serialVersionUID = -1309888093L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCompany company = new QCompany("company");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final BooleanPath audioRestrictedMode = createBoolean("audioRestrictedMode");

    public final NumberPath<Integer> companyCode = createNumber("companyCode", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QLanguage language;

    public final StringPath licenseName = createString("licenseName");

    public final BooleanPath localRecording = createBoolean("localRecording");

    public final BooleanPath recording = createBoolean("recording");

    public final EnumPath<com.virnect.data.domain.session.SessionType> sessionType = createEnum("sessionType", com.virnect.data.domain.session.SessionType.class);

    public final BooleanPath storage = createBoolean("storage");

    public final BooleanPath sttStreaming = createBoolean("sttStreaming");

    public final BooleanPath sttSync = createBoolean("sttSync");

    public final BooleanPath translation = createBoolean("translation");

    public final BooleanPath tts = createBoolean("tts");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final BooleanPath videoRestrictedMode = createBoolean("videoRestrictedMode");

    public final StringPath workspaceId = createString("workspaceId");

    public QCompany(String variable) {
        this(Company.class, forVariable(variable), INITS);
    }

    public QCompany(Path<? extends Company> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCompany(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCompany(PathMetadata metadata, PathInits inits) {
        this(Company.class, metadata, inits);
    }

    public QCompany(Class<? extends Company> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.language = inits.isInitialized("language") ? new QLanguage(forProperty("language"), inits.get("language")) : null;
    }

}

