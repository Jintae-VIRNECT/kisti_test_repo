package com.virnect.data.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLanguage is a Querydsl query type for Language
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLanguage extends EntityPathBase<Language> {

    private static final long serialVersionUID = 1329316594L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLanguage language = new QLanguage("language");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final QCompany company;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath transEnUs = createBoolean("transEnUs");

    public final BooleanPath transEsEs = createBoolean("transEsEs");

    public final BooleanPath transFrFr = createBoolean("transFrFr");

    public final BooleanPath transJaJp = createBoolean("transJaJp");

    public final BooleanPath transKoKr = createBoolean("transKoKr");

    public final BooleanPath transPlPl = createBoolean("transPlPl");

    public final BooleanPath transRuRu = createBoolean("transRuRu");

    public final BooleanPath transUkUa = createBoolean("transUkUa");

    public final BooleanPath transZh = createBoolean("transZh");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QLanguage(String variable) {
        this(Language.class, forVariable(variable), INITS);
    }

    public QLanguage(Path<? extends Language> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLanguage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLanguage(PathMetadata metadata, PathInits inits) {
        this(Language.class, metadata, inits);
    }

    public QLanguage(Class<? extends Language> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new QCompany(forProperty("company"), inits.get("company")) : null;
    }

}

