package com.virnect.license.application;

import com.virnect.license.dao.*;
import com.virnect.license.domain.*;
import com.virnect.license.dto.request.CouponActiveRequest;
import com.virnect.license.dto.request.CouponRegisterRequest;
import com.virnect.license.dto.request.EventCouponRequest;
import com.virnect.license.dto.response.EventCouponResponse;
import com.virnect.license.dto.response.MyCouponInfoListResponse;
import com.virnect.license.dto.response.MyCouponInfoResponse;
import com.virnect.license.dto.rest.UserInfoRestResponse;
import com.virnect.license.exception.LicenseServiceException;
import com.virnect.license.global.common.ApiResponse;
import com.virnect.license.global.common.PageMetadataResponse;
import com.virnect.license.global.error.ErrorCode;
import com.virnect.license.infra.mail.EmailMessage;
import com.virnect.license.infra.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-License
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LicenseService {
    private final CouponRepository couponRepository;
    private final CouponProductRepository couponProductRepository;
    private final ProductRepository productRepository;
    private final LicenseTypeRepository licenseTypeRepository;
    private final LicensePlanRepository licensePlanRepository;
    private final LicenseProductRepository licenseProductRepository;
    private final UserRestService userRestService;
    private final EmailService emailService;
    private final ProductTypeRepository productTypeRepository;


    /**
     * 이벤트 쿠폰 생성
     *
     * @param eventCouponRequest
     * @return
     */
    @Transactional
    public ApiResponse<EventCouponResponse> generateEventCoupon(EventCouponRequest eventCouponRequest) {
        String LICENSE_TYPE_OF_2WEEK_FREE_COUPON = "2_WEEK_FREE_TRIAL_LICENSE";
        String serialKey = UUID.randomUUID().toString().toUpperCase();
//         Check Duplicate Register Request
        boolean isAlreadyRegisterEventCoupon = this.couponProductRepository.existsByLicenseType_NameAndAndCoupon_UserId(LICENSE_TYPE_OF_2WEEK_FREE_COUPON, eventCouponRequest.getUserId());

        if (isAlreadyRegisterEventCoupon) {
            throw new LicenseServiceException(ErrorCode.ERR_ALREADY_REGISTER_EVENT_COUPON);
        }

        Duration couponExpiredDuration = Duration.ofDays(14);
        LocalDateTime couponExpiredDate = LocalDateTime.now().plusDays(14);

        Coupon coupon = Coupon.builder()
                .company(eventCouponRequest.getCompanyName())
                .department(eventCouponRequest.getDepartment())
                .position(eventCouponRequest.getPosition())
                .companyEmail(eventCouponRequest.getCompanyEmail())
                .callNumber(eventCouponRequest.getCallNumber())
                .companySite(eventCouponRequest.getCompanySite())
                .companyCategory(eventCouponRequest.getCompanyCategory())
                .companyService(eventCouponRequest.getCompanyService())
                .companyWorker(eventCouponRequest.getCompanyWorker())
                .content(eventCouponRequest.getContent())
                .personalInfoPolicy(eventCouponRequest.getPersonalInfoPolicy())
                .marketInfoReceive(eventCouponRequest.getMarketInfoReceivePolicy())
                .duration(couponExpiredDuration.toDays())
                .expiredDate(couponExpiredDate)
                .periodType(CouponPeriodType.DAY)
                .couponStatus(CouponStatus.UNUSE)
                .serialKey(serialKey)
                .userId(eventCouponRequest.getUserId())
                .build();

        this.couponRepository.save(coupon);

        LicenseType licenseType = this.licenseTypeRepository.findByName(LICENSE_TYPE_OF_2WEEK_FREE_COUPON)
                .orElseThrow(() -> new LicenseServiceException(ErrorCode.ERR_CREATE_COUPON));

        for (String productName : eventCouponRequest.getProducts()) {
            Product product = this.productRepository.findByProductType_NameAndName("BASIC", productName)
                    .orElseThrow(() -> new LicenseServiceException(ErrorCode.ERR_CREATE_COUPON));

            CouponProduct couponProduct = CouponProduct.builder()
                    .product(product)
                    .coupon(coupon)
                    .licenseType(licenseType)
                    .build();

            this.couponProductRepository.save(couponProduct);
        }

//        ApiResponse<UserInfoRestResponse> apiResponse = this.userRestService.getUserInfoByUserId(eventCouponRequest.getUserId());
//        UserInfoRestResponse userInfoRestResponse = apiResponse.getData();
//
//        if (apiResponse.getCode() != 200 || userInfoRestResponse.getEmail() == null) {
//            log.info("user server error: {}", apiResponse.toString());
//            throw new LicenseServiceException(ErrorCode.ERR_CREATE_COUPON);
//        }
//
//        EmailMessage message = new EmailMessage();
//        message.setSubject("2주 무료 라이선스 쿠폰 발급 : 시리얼 코드를 확인하세요");
//        message.setTo(userInfoRestResponse.getEmail());
//        message.setMessage("Serial Key : " + serialKey);
//        emailService.sendEmail(message);

        return new ApiResponse<>(new EventCouponResponse(true, coupon.getCreatedDate()));
    }

    /**
     * 이벤트 쿠폰 등록
     *
     * @param couponRegisterRequest
     * @return
     */

    @Transactional
    public ApiResponse<MyCouponInfoListResponse> couponRegister(CouponRegisterRequest couponRegisterRequest) {
        // 1. 쿠폰 조회
        Coupon coupon = this.couponRepository.findByUserIdAndSerialKey(couponRegisterRequest.getUserId(), couponRegisterRequest.getCouponSerialKey())
                .orElseThrow(() -> new LicenseServiceException(ErrorCode.ERR_COUPON_NOT_FOUND));

        // 2. 쿠폰 사용 여부 검사
        if (coupon.getStatus().equals(CouponStatus.USE)) {
            throw new LicenseServiceException(ErrorCode.ERR_COUPON_REGISTER_ALREADY_USED);
        }

        // 3. 쿠폰 만료일 검사
        if (coupon.getExpiredDate().isBefore(LocalDateTime.now()) || coupon.getStatus().equals(CouponStatus.EXPIRED)) {
            throw new LicenseServiceException(ErrorCode.ERR_COUPON_REGISTER_EXPIRED);
        }

        // 4. 쿠폰 상태 변경 (미사용 -> 사용)
        coupon.setStatus(CouponStatus.USE);

        this.couponRepository.save(coupon);
        // 5. 쿠폰 기반으로 라이선스 정보 등록
        licenseRegisterByCouponProduct(couponRegisterRequest.getUserId(), coupon);


        return null;
    }


    private void licenseRegisterByCouponProduct(String userId, Coupon coupon) {
        // 1. 라이선스 플랜 생성
        LicensePlan licensePlan = LicensePlan.builder()
                .expiredDate(LocalDateTime.now().plusDays(coupon.getDuration()))
                .userId(userId)
                .coupon(coupon)
                .planStatus(PlanStatus.ACTIVE)
                .build();

        this.licensePlanRepository.save(licensePlan);

        List<CouponProduct> couponProductList = coupon.getCouponProductList();

        // 2. 쿠폰 기반으로 쿠폰에 관련된 상품 정보 입력
        for (CouponProduct couponProduct : couponProductList) {
            // 2-1. 쿠폰 상품 조회
            Product product = couponProduct.getProduct();
            // 2-2. 쿠폰에 부여된 라이선스 타입 조회
            LicenseType licenseType = couponProduct.getLicenseType();
            LicenseProduct licenseProduct = LicenseProduct.builder()
                    .product(product)
                    .licenseType(licenseType)
                    .price(product.getPrice() * 9)
                    .quantity(9)
                    .licensePlan(licensePlan)
                    .build();

            this.licenseProductRepository.save(licenseProduct);
        }
    }

    /**
     * 내 쿠폰 정보 리스트 조회
     *
     * @param userId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public ApiResponse<MyCouponInfoListResponse> getMyCouponInfoList(String userId, Pageable pageable) {
        Page<Coupon> couponList = couponRepository.findByUserId(userId, pageable);

        List<MyCouponInfoResponse> couponInfoList = couponList.stream().map(coupon -> {
            MyCouponInfoResponse myCouponInfo = new MyCouponInfoResponse();
            myCouponInfo.setId(coupon.getId());
            myCouponInfo.setName("2주 무료 사용 쿠폰");
            myCouponInfo.setStatus(coupon.getStatus());
            myCouponInfo.setRegisterDate(coupon.getCreatedDate());
            myCouponInfo.setExpiredDate(coupon.getExpiredDate());
            if (coupon.getStatus().equals(CouponStatus.USE)) {
                LicensePlan licensePlan = coupon.getLicensePlan();
                myCouponInfo.setStartDate(licensePlan.getCreatedDate());
                myCouponInfo.setExpiredDate(licensePlan.getExpiredDate());
            }
            return myCouponInfo;
        }).collect(Collectors.toList());

        PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
                .currentPage(pageable.getPageNumber())
                .currentSize(pageable.getPageSize())
                .totalPage(couponList.getTotalPages())
                .totalElements(couponList.getTotalElements())
                .build();

        return new ApiResponse<>(new MyCouponInfoListResponse(couponInfoList, pageMetadataResponse));
    }

    /**
     * 내 쿠폰 활성화
     *
     * @param couponActiveRequest
     * @return
     */
    public ApiResponse<MyCouponInfoListResponse> couponActiveHandler(CouponActiveRequest couponActiveRequest) {
        return null;
    }
}
