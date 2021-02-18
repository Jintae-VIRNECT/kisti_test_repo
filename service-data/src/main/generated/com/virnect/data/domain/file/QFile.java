package com.virnect.data.domain.file;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QFile is a Querydsl query type for File
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFile extends EntityPathBase<File> {

    private static final long serialVersionUID = 1963875988L;

    public static final QFile file = new QFile("file");

    public final com.virnect.data.domain.QBaseTimeEntity _super = new com.virnect.data.domain.QBaseTimeEntity(this);

    public final StringPath contentType = createString("contentType");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final BooleanPath deleted = createBoolean("deleted");

    public final NumberPath<Long> downloadHits = createNumber("downloadHits", Long.class);

    public final DateTimePath<java.time.LocalDateTime> expirationDate = createDateTime("expirationDate", java.time.LocalDateTime.class);

    public final BooleanPath expired = createBoolean("expired");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath objectName = createString("objectName");

    public final StringPath sessionId = createString("sessionId");

    public final NumberPath<Long> size = createNumber("size", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath uuid = createString("uuid");

    public final StringPath workspaceId = createString("workspaceId");

    public QFile(String variable) {
        super(File.class, forVariable(variable));
    }

    public QFile(Path<? extends File> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFile(PathMetadata metadata) {
        super(File.class, metadata);
    }

}

