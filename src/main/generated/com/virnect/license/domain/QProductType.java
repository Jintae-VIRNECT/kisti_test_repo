package com.virnect.license.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductType is a Querydsl query type for ProductType
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QProductType extends EntityPathBase<ProductType> {

    private static final long serialVersionUID = 627511840L;

    public static final QProductType productType = new QProductType("productType");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<Product, QProduct> productList = this.<Product, QProduct>createList("productList", Product.class, QProduct.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QProductType(String variable) {
        super(ProductType.class, forVariable(variable));
    }

    public QProductType(Path<? extends ProductType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductType(PathMetadata metadata) {
        super(ProductType.class, metadata);
    }

}

