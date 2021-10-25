package com.virnect.download.dao;

import static com.virnect.download.domain.AppStatus.*;
import static com.virnect.download.domain.QApp.*;
import static com.virnect.download.domain.QDevice.*;
import static com.virnect.download.domain.QOS.*;
import static com.virnect.download.domain.QProduct.*;

import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.download.domain.App;
import com.virnect.download.dto.domain.DeviceLatestVersionCodeDto;
import com.virnect.download.dto.domain.QDeviceLatestVersionCodeDto;

/**
 * Project: PF-Download
 * DATE: 2020-05-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@RequiredArgsConstructor
public class AppRepositoryImpl implements AppRepositoryCustom {
	private final JPAQueryFactory query;

	@Override
	public List<App> getActiveAppList(List<Long> deviceIds, List<Long> latestVersionCodes) {
		return query
			.select(app)
			.from(app)
			.innerJoin(app.os, oS).fetchJoin()
			.innerJoin(app.device, device).fetchJoin()
			.where(
				app.appStatus.eq(ACTIVE),
				app.device.id.in(deviceIds),
				app.versionCode.in(latestVersionCodes)
			)
			.orderBy(app.id.asc())
			.fetch();
	}

	@Override
	public Long getLatestVersionCodeByPackageName(String packageName) {
		return query
			.select(app.versionCode.max())
			.from(app)
			.where(app.packageName.eq(packageName)).fetchOne();
	}

	@Override
	public Optional<App> getLatestVersionAppInfoByPackageName(String packageName) {
		return Optional.ofNullable(
			query
				.selectFrom(app)
				.innerJoin(app.os, oS).fetchJoin()
				.innerJoin(app.product, product).fetchJoin()
				.innerJoin(app.device, device).fetchJoin()
				.where(app.packageName.eq(packageName))
				.where(app.appStatus.eq(ACTIVE))
				.orderBy(app.versionCode.desc())
				.fetchFirst()
		);
	}

	@Override
	public List<App> findByPackageNameAndSignature(String packageName, String signature) {

		return query
			.selectFrom(app)
			.innerJoin(app.os, oS).fetchJoin()
			.innerJoin(app.product, product).fetchJoin()
			.innerJoin(app.device, device).fetchJoin()
			.where(app.packageName.eq(packageName).and(app.signature.eq(signature)))
			.orderBy(app.versionCode.desc())
			.fetch();
	}

	@Override
	public List<DeviceLatestVersionCodeDto> getLatestVersionInfoPerDeviceOfProduct(String productName) {
		return query
			.select(new QDeviceLatestVersionCodeDto(app.device.id, app.versionCode.max()))
			.from(app)
			.innerJoin(app.device, device)
			.innerJoin(app.device.product, product)
			.where(
				app.device.product.name.eq(productName),
				app.appStatus.eq(ACTIVE)
			)
			.groupBy(app.device.id)
			.fetch();
	}

	@Override
	public long registerSigningKeyByPackageName(String packageName, String signingKey) {
		return query
			.update(app)
			.where(app.packageName.eq(packageName))
			.where(app.signature.isNull())
			.set(app.signature, signingKey)
			.execute();
	}
}
