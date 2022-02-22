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
import com.virnect.download.domain.Device;
import com.virnect.download.domain.OS;
import com.virnect.download.domain.Product;
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
	public Long getLatestVersionCodeByPackageName(String packageName) {
		return query
			.select(app.versionCode.max())
			.from(app)
			.where(app.packageName.eq(packageName)).fetchOne();
	}

	@Override
	public Optional<App> getLatestVersionActiveAppInfoByPackageName(String packageName) {
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
	public List<App> getAppByPackageNameAndSignature(String packageName, String signature) {

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
	public App getActiveAppByDeviceLatestVersionCode(
		DeviceLatestVersionCodeDto deviceLatestVersionCodeDto
	) {
		return query
			.select(app)
			.from(app)
			.innerJoin(app.os, oS).fetchJoin()
			.innerJoin(app.device, device).fetchJoin()
			.where(
				app.appStatus.eq(ACTIVE),
				app.device.id.eq(deviceLatestVersionCodeDto.getDeviceId()),
				app.versionCode.eq(deviceLatestVersionCodeDto.getVersionCode())
			)
			.orderBy(app.id.asc())
			.fetchFirst();
	}

	@Override
	public Optional<App> getAppByProductAndOsAndDevice(
		Product product, OS os, Device device
	) {
		return Optional.ofNullable(
			query
				.selectFrom(app)
				.where(app.product.eq(product), app.os.eq(os), app.device.eq(device))
				.orderBy(app.createdDate.desc())
				.fetchFirst()
		);
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
