package com.virnect.uaa.domain.auth.account.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.14
 */

@Getter
@Setter
@ApiModel
@RequiredArgsConstructor
public class OTPQRGenerateResponse {
	@ApiModelProperty(value = "OTP 로그인을 위한 정보가 담긴 OTP Auth URL", example = "otpauth://totp/virnect:sky456139@virnect.com?secret=5GY6KGZDJ23CODMK&issuer=virnect&algorithm=SHA1&digits=6&period=30")
	private final String authUrl;
	@ApiModelProperty(value = "OTP 로그인을 위한 정보가 담긴 QR 이미지 (Base64로 인코딩 됨)", example = "iVBORw0KGgoAAAANSUhEUgAAAfQAAAH0AQAAAADjreInAAADLElEQVR42u3dQXqDIBCA0XHlMTyqHtUjdOlKGoFhhmjaRsiqv4s0VZ8rPmCGwUhoOr4Ej8fj8Xg8Ho//d36TfEzp23CcOz7CLvM6Pv6u4/64+viy5DsHPL6bT/+F5fBzsGeOx7lw0OOW9aGWyQs8vo9fJF48/CrxIfvhj/Z7tNV1zC053pIeh8d39/pxNF13QbTp4vEf9KnVBtdNLjaw4/Gf8k/jd7ot9p9lOM/955/Gfzz+DW/xS9WJXn38Ev/g8W/7+kgBc5xEVuHM/Mf8Dx7/nt/yvTZCx/gln9tKYFMyOef2j8ff9zFg0Vxh0IRNCljEQuc4u7zOH+LxTT6D2Go1YZO/BU0dpnY+XOYP8fi73iIUqTKJ1n9KWT9xD8Hju/p4zLZ0IlXWWqeTOx7f1ZfgpAzTc+lTrSf9YfzG4xt8CG5xxOUPp1f1C9OGx3fz7t69XNxdOU0avxMNuqaMx3fyQymT0fqFVLAgea4YW60WNmiKEY/v5H3VzPxUyZVLZ/af1o/x+BY/+IJB0Qy1zh/9xFKu4mc8vtGngq1Vh2lrulXVtAbRF/0vHn/XhzxWi+j6SU5dizivA/t5/Mbjm72t35Wlu0mLqMWXs17kb/D4Br/lgCWdL/GL7hpxHaZbXcbjO/lqr0geybXDdHPKSeeZ83P9Fx7f4G1znKtVzbnC7EtOJ00sz/s38fi7Xm8LlsSxDnMdLWG9uxJrPL6fzxs2tX5GV41L3DxaO5fT+gke3+LL+RCewhSjJX6WV+M3Hn/Ph91tjluKWux1Hnno9iE2Ht/Jb3XqULeOuJouv3/9PH7j8S3erQtrhYwrnZaSyS6VDAGP7+rPycG9PGlw8XPe047H9/N1082bSDR/M7nsti4in+u38Pjbvt5EMrk9xYOvaVj1xYL5JR54fBdv7+84xy8psLHVlelq/yce3+Lt/UVDCWKqRhysavVV/SEef9/brmGbNXplJV7X+Ws8voO30PmpfquqH1wEj/+QF4uVp1KroA3Wv9gDj+/mXRKxFMyIvdPINrHLeLl/E4+/7/3vN/ilk3r9uF5TweM7+dsHHo/H4/F4PB7/r/w36O+7E3v1XosAAAAASUVORK5CYII=")
	private final String qrCode;
}
