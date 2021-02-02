package com.virnect.download.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.virnect.download.domain.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

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
    public List<App> getActiveAppList(String productName) {
        QApp qApp = QApp.app;
        return jpaQueryFactory
                .select(qApp)
                .from(qApp)
                .innerJoin(qApp.device.product)
                .where(qApp.device.product.name.eq(productName).and(qApp.appStatus.eq(AppStatus.ACTIVE)))
                .orderBy(qApp.versionCode.desc())
                .fetch();
    }

    @Override
    public Long getLatestVersionCodeByPackageName(String packageName) {
        QApp qApp = QApp.app;
        return jpaQueryFactory
                .select(qApp.versionCode.max())
                .from(qApp)
                .where(qApp.packageName.eq(packageName)).fetchOne();
    }

    @Override
    public Optional<App> getLatestVersionAppInfoByPackageName(String packageName) {
        QApp qApp = QApp.app;
        QOS qos = QOS.oS;
        QProduct qProduct = QProduct.product;
        QDevice qDevice = QDevice.device;

        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(qApp)
                        .innerJoin(qApp.os, qos).fetchJoin()
                        .innerJoin(qApp.product, qProduct).fetchJoin()
                        .innerJoin(qApp.device, qDevice).fetchJoin()
                        .where(qApp.packageName.eq(packageName))
                        .where(qApp.appStatus.eq(AppStatus.ACTIVE))
                        .orderBy(qApp.versionCode.desc())
                        .fetchFirst()
        );
    }

    @Override
    public List<App> findByPackageNameAndSignature(String packageName, String signature) {
        QApp qApp = QApp.app;
        QOS qos = QOS.oS;
        QProduct qProduct = QProduct.product;
        QDevice qDevice = QDevice.device;

        return jpaQueryFactory
                .selectFrom(qApp)
                .innerJoin(qApp.os, qos).fetchJoin()
                .innerJoin(qApp.product, qProduct).fetchJoin()
                .innerJoin(qApp.device, qDevice).fetchJoin()
                .where(qApp.packageName.eq(packageName).and(qApp.signature.eq(signature)))
                .orderBy(qApp.versionCode.desc())
                .fetch();
    }

    @Override
    public long registerSigningKeyByPackageName(String packageName, String signingKey) {
        QApp qApp = QApp.app;
        return jpaQueryFactory
                .update(qApp)
                .where(qApp.packageName.eq(packageName))
                .where(qApp.signature.isNull())
                .set(qApp.signature, signingKey)
                .execute();
    }
}
