package com.virnect.license.application;

import com.virnect.license.dao.*;
import com.virnect.license.domain.*;
import com.virnect.license.dto.request.CouponActiveRequest;
import com.virnect.license.dto.request.CouponRegisterRequest;
import com.virnect.license.dto.request.EventCouponRequest;
import com.virnect.license.dto.response.*;
import com.virnect.license.dto.response.admin.AdminCouponInfoListResponse;
import com.virnect.license.dto.response.admin.AdminCouponInfoResponse;
import com.virnect.license.dto.rest.UserInfoRestResponse;
import com.virnect.license.exception.LicenseServiceException;
import com.virnect.license.global.common.ApiResponse;
import com.virnect.license.global.common.PageMetadataResponse;
import com.virnect.license.global.error.ErrorCode;
import com.virnect.license.infra.mail.EmailMessage;
import com.virnect.license.infra.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ProductRepository productRepository;
    private final LicenseTypeRepository licenseTypeRepository;
    private final LicensePlanRepository licensePlanRepository;
    private final LicenseProductRepository licenseProductRepository;
    private final LicenseRepository licenseRepository;
    private final UserRestService userRestService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;


    /**
     * 이벤트 쿠폰 생성
     *
     * @param eventCouponRequest - 쿠폰 생성 요청 데이터
     * @return - 쿠폰 생성 정보
     */
    @Transactional
    public ApiResponse<EventCouponResponse> generateEventCoupon(EventCouponRequest eventCouponRequest) {
        // 1. 요청 사용자 정보 확인
        ApiResponse<UserInfoRestResponse> userInfoApiResponse = this.userRestService.getUserInfoByUserId(eventCouponRequest.getUserId());
        if (userInfoApiResponse.getCode() != 200 || userInfoApiResponse.getData().getEmail() == null) {
            log.info("User service error response: [{}]", userInfoApiResponse.getMessage());
            throw new LicenseServiceException(ErrorCode.ERR_CREATE_COUPON);
        }

        String LICENSE_TYPE_OF_2WEEK_FREE_COUPON = "2_WEEK_FREE_TRIAL_LICENSE";
        String COUPON_NAME = "2주 무료 사용 쿠폰";
        String serialKey = this.generateCouponSerialKey();

//         Check Duplicate Register Request
        boolean isAlreadyRegisterEventCoupon = this.licenseProductRepository.existsByLicenseType_NameAndAndCoupon_UserId(LICENSE_TYPE_OF_2WEEK_FREE_COUPON, eventCouponRequest.getUserId());

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
                .name(COUPON_NAME)
                .serialKey(serialKey)
                .userId(eventCouponRequest.getUserId())
                .build();

        this.couponRepository.save(coupon);

        LicenseType licenseType = this.licenseTypeRepository.findByName(LICENSE_TYPE_OF_2WEEK_FREE_COUPON)
                .orElseThrow(() -> new LicenseServiceException(ErrorCode.ERR_CREATE_COUPON));

        // 모든 제품군의 경우
//        for (String productName : eventCouponRequest.getProducts()) {
//            Product product = this.productRepository.findByProductType_NameAndName("BASIC", productName)
//                    .orElseThrow(() -> new LicenseServiceException(ErrorCode.ERR_CREATE_COUPON));
//
//            CouponProduct couponProduct = CouponProduct.builder()
//                    .product(product)
//                    .coupon(coupon)
//                    .licenseType(licenseType)
//                    .build();
//
//            this.couponProductRepository.save(couponProduct);
//        }

        // 메이크와 뷰 제품 (2020-04-15: MAKE VIEW 제품군만 출시일정 잡힘)

        Product product = this.productRepository.findByProductType_NameAndName("BASIC", "MAKE")
                .orElseThrow(() -> new LicenseServiceException(ErrorCode.ERR_CREATE_COUPON));

        LicenseProduct licenseProduct = LicenseProduct.builder()
                .product(product)
                .licenseType(licenseType)
                .coupon(coupon)
                .quantity(1)
                .build();

//        CouponProduct couponProduct = CouponProduct.builder()
//                .product(product)
//                .coupon(coupon)
//                .licenseType(licenseType)
//                .build();
//        this.couponProductRepository.save(couponProduct);
        this.licenseProductRepository.save(licenseProduct);

        UserInfoRestResponse userInfoRestResponse = userInfoApiResponse.getData();
        EmailMessage message = new EmailMessage();
        message.setSubject("2주 무료 라이선스 쿠폰 발급 : 시리얼 코드를 확인하세요");
        message.setTo(userInfoRestResponse.getEmail());
        message.setMessage("Serial Key : " + serialKey);
        emailService.sendEmail(message);

        return new ApiResponse<>(new EventCouponResponse(true, coupon.getSerialKey(), coupon.getCreatedDate()));
    }

    /**
     * 이벤트 쿠폰 등록
     *
     * @param couponRegisterRequest
     * @return
     */

    @Transactional
    public ApiResponse<MyCouponInfoResponse> couponRegister(CouponRegisterRequest couponRegisterRequest) {
        // 1. 쿠폰 조회
        Coupon coupon = this.couponRepository.findByUserIdAndSerialKey(couponRegisterRequest.getUserId(), couponRegisterRequest.getCouponSerialKey())
                .orElseThrow(() -> new LicenseServiceException(ErrorCode.ERR_COUPON_NOT_FOUND));

        // 2. 쿠폰 등록 여부 검사 (등록일이 존재하는 경우)
        if (coupon.getRegisterDate() != null) {
            throw new LicenseServiceException(ErrorCode.ERR_COUPON_REGISTER_ALREADY_REGISTER);
        }

        // 3. 쿠폰 만료일 검사
        if (coupon.isExpired()) {
            throw new LicenseServiceException(ErrorCode.ERR_COUPON_EXPIRED);
        }

        // 4. 쿠폰 등록 일자 수정
        coupon.setRegisterDate(LocalDateTime.now());

        this.couponRepository.save(coupon);

        MyCouponInfoResponse registerCouponInfo = new MyCouponInfoResponse();
        registerCouponInfo.setId(coupon.getId());
        registerCouponInfo.setName(coupon.getName());
        registerCouponInfo.setStatus(coupon.getStatus());
        registerCouponInfo.setExpiredDate(coupon.getExpiredDate());
        registerCouponInfo.setRegisterDate(coupon.getCreatedDate());
        registerCouponInfo.setSerialKey(coupon.getSerialKey());

        return new ApiResponse<>(registerCouponInfo);
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
        Page<Coupon> couponList = couponRepository.findByUserIdAndRegisterDateIsNotNull(userId, pageable);

        List<MyCouponInfoResponse> couponInfoList = couponList.stream().map(coupon -> {
            MyCouponInfoResponse myCouponInfo = new MyCouponInfoResponse();
            myCouponInfo.setId(coupon.getId());
            myCouponInfo.setName(coupon.getName());
            myCouponInfo.setStatus(coupon.getStatus());
            myCouponInfo.setRegisterDate(coupon.getCreatedDate());
            myCouponInfo.setExpiredDate(coupon.getExpiredDate());
            myCouponInfo.setSerialKey(coupon.getSerialKey());
            if (coupon.getStatus().equals(CouponStatus.USE)) {
                myCouponInfo.setStartDate(coupon.getStartDate());
                myCouponInfo.setEndDate(coupon.getEndDate());
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
    @Transactional
    public ApiResponse<MyCouponInfoResponse> couponActiveHandler(CouponActiveRequest couponActiveRequest) {
        // 1. 활성화 할 쿠폰 찾기
        Coupon coupon = this.couponRepository.findByUserIdAndIdAndRegisterDateIsNotNull(couponActiveRequest.getUserId(), couponActiveRequest.getCouponId())
                .orElseThrow(() -> new LicenseServiceException(ErrorCode.ERR_COUPON_NOT_FOUND));

        // 2. 이미 사용된 쿠폰의 경우
        if (coupon.isUsed()) {
            throw new LicenseServiceException(ErrorCode.ERR_COUPON_ALREADY_ACTIVATED);
        }

        // 3. 쿠폰 기한이 만료된 경우
        if (coupon.isExpired()) {
            throw new LicenseServiceException(ErrorCode.ERR_COUPON_EXPIRED);
        }

        // 1. 라이선스 플랜 생성
        LicensePlan licensePlan = LicensePlan.builder()
                .userId(couponActiveRequest.getUserId())
                .workspaceId(couponActiveRequest.getWorkspaceId())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(coupon.getDuration()))
                .planStatus(PlanStatus.ACTIVE)
                .build();

        this.licensePlanRepository.save(licensePlan);

        coupon.setLicensePlan(licensePlan);
        coupon.setStatus(CouponStatus.USE);
        coupon.setStartDate(licensePlan.getStartDate());
        coupon.setEndDate(licensePlan.getEndDate());

        this.couponRepository.save(coupon);

        // 5. 쿠폰 기반으로 라이선스 정보 등록
        licenseRegisterByCouponProduct(coupon, licensePlan);

        // 6. 응답 데이터 설정
        MyCouponInfoResponse myCouponInfoResponse = new MyCouponInfoResponse();
        myCouponInfoResponse.setSerialKey(coupon.getSerialKey());
        myCouponInfoResponse.setId(coupon.getId());
        myCouponInfoResponse.setName(coupon.getName());
        myCouponInfoResponse.setStatus(coupon.getStatus());
        myCouponInfoResponse.setRegisterDate(coupon.getRegisterDate());
        myCouponInfoResponse.setExpiredDate(coupon.getExpiredDate());
        myCouponInfoResponse.setStartDate(licensePlan.getStartDate());
        myCouponInfoResponse.setEndDate(licensePlan.getEndDate());
        return new ApiResponse<>(myCouponInfoResponse);
    }

    /**
     * 쿠폰에 등록된 정보로 라이선스 생성
     *
     * @param coupon      쿠폰 정보
     * @param licensePlan - 신규 라이선스 플랜 정보
     */
    private void licenseRegisterByCouponProduct(Coupon coupon, LicensePlan licensePlan) {
        List<LicenseProduct> couponProductList = coupon.getCouponProductList();

        // 2. 쿠폰 기반으로 쿠폰에 관련된 상품 정보 입력
        for (LicenseProduct couponProduct : couponProductList) {
//            // 2-1. 쿠폰 상품 조회
//            Product product = couponProduct.getProduct();
//            // 2-2. 쿠폰에 부여된 라이선스 타입 조회
//            LicenseType licenseType = couponProduct.getLicenseType();
//            // 2-3. 라이선스 타입에 맞는 상품 등록
//            LicenseProduct licenseProduct = LicenseProduct.builder()
//                    .product(product)
//                    .licenseType(licenseType)
//                    .price(product.getPrice())
//                    .quantity(1)
//                    .licensePlan(licensePlan)
//                    .build();
//
//            this.licenseProductRepository.save(licenseProduct);
            couponProduct.setLicensePlan(licensePlan);
            // 2-4. 라이선스 상품별 사용가능한 라이선스 생성
            for (int i = 0; i < couponProduct.getQuantity(); i++) {
                License license = License.builder()
                        .status(LicenseStatus.UNUSE)
                        .serialKey(UUID.randomUUID().toString().toUpperCase())
                        .licenseProduct(couponProduct)
                        .build();
                this.licenseRepository.save(license);
            }
        }
    }

    /**
     * 쿠폰 시리얼 키 생성 (0,1은 O,I로 치환)
     *
     * @return - 시리얼 코드
     */
    private String generateCouponSerialKey() {
        return UUID.randomUUID().toString().replace("0", "O").replace("1", "I").toUpperCase();
    }

    /**
     * 내 라이선스 플랜 정보 조회
     *
     * @param userId      - 사용자 식별자
     * @param workspaceId - 워크스페이스 식별자
     * @return - 내 라이선스 플랜 정보
     */
    public ApiResponse<MyLicensePlanInfoResponse> getMyLicensePlanInfo(String userId, String workspaceId) {
        LicensePlan licensePlan = this.licensePlanRepository.findByUserIdAndWorkspaceIdAndPlanStatus(userId, workspaceId, PlanStatus.ACTIVE)
                .orElseThrow(() -> new LicenseServiceException(ErrorCode.ERR_LICENSE_PLAN_NOT_FOUND));


        return null;
    }

    /**
     * 내 라이선스 플랜의 라이선스 정보 목록 가져오기
     *
     * @param userId      - 사용자 식별자
     * @param workspaceId - 워크스페이스 식별자
     * @param status      - 라이선스 상태 (ALL, USE, UNUSE)
     * @return - 라이선스 정보 목록
     */
    public ApiResponse<MyLicenseInfoListResponse> getMyLicenseInfoList(String userId, String workspaceId, String status) {
        LicensePlan licensePlan = this.licensePlanRepository.findByUserIdAndWorkspaceIdAndPlanStatus(userId, workspaceId, PlanStatus.ACTIVE)
                .orElseThrow(() -> new LicenseServiceException(ErrorCode.ERR_LICENSE_PLAN_NOT_FOUND));

        if (status.equals("ALL")) {
            List<LicenseProduct> licenseProductList = licensePlan.getLicenseProductList();
        } else {
            // QueryParameter Validator 구현 필요
            try {
                LicenseStatus licenseStatus = LicenseStatus.valueOf(status);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    @Transactional(readOnly = true)
    public ApiResponse<AdminCouponInfoListResponse> getAllCouponInfo(Pageable pageable) {
        Page<Coupon> couponList = couponRepository.findAllCouponInfo(pageable);
        List<AdminCouponInfoResponse> adminCouponInfoList = couponList.stream()
                .map(c -> {
                    AdminCouponInfoResponse adminCouponInfoResponse = modelMapper.map(c, AdminCouponInfoResponse.class);
                    adminCouponInfoResponse.setProducts(c.getCouponProductList().stream().distinct().map(cp -> cp.getProduct().getName()).toArray(String[]::new));
                    return adminCouponInfoResponse;
                })
                .collect(Collectors.toList());

        PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
                .currentPage(pageable.getPageNumber())
                .currentSize(pageable.getPageSize())
                .totalPage(couponList.getTotalPages())
                .totalElements(couponList.getTotalElements())
                .build();

        return new ApiResponse<>(new AdminCouponInfoListResponse(adminCouponInfoList, pageMetadataResponse));
    }
}
