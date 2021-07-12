package com.virnect.uaa.infra.rest.app;

import org.springframework.stereotype.Service;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.app.dto.AppDetailInfoResponse;

@Slf4j
@Service
public class AppFallbackFactory implements FallbackFactory<AppRestService> {
	@Override
	public AppRestService create(Throwable cause) {
		log.error(cause.getMessage(), cause);
		return packageName -> {
			log.error("[ERR_APP_REST_SERVICE] - FAllBackFactory Execute");
			AppDetailInfoResponse appDetailInfoResponse = new AppDetailInfoResponse();
			appDetailInfoResponse.setUuid("");
			appDetailInfoResponse.setVersion("");
			appDetailInfoResponse.setDeviceType("");
			appDetailInfoResponse.setOperationSystem("");
			appDetailInfoResponse.setPackageName("");
			appDetailInfoResponse.setAppUrl("");
			appDetailInfoResponse.setProductName("");
			appDetailInfoResponse.setSigningKey("");
			return new ApiResponse<>(appDetailInfoResponse);
		};
	}
}
