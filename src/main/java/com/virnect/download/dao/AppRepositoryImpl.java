package com.virnect.download.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.virnect.download.domain.App;
import com.virnect.download.domain.Product;
import com.virnect.download.domain.QApp;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Project: PF-Download
 * DATE: 2020-05-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
public class AppRepositoryImpl implements AppRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<App> getAppList(Product product) {
        QApp qApp = QApp.app;

        return jpaQueryFactory
                .select(qApp).from(qApp).where(qApp.product.eq(product)).orderBy(qApp.version.desc()).fetch();
    }
}
