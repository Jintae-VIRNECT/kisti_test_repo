package com.virnect.license.domain;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-License
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.09
 */

@Entity
@Getter
@Setter
@Audited
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    @Column(name = "company")
    private String company;

    @Column(name = "department")
    private String department;

    @Column(name = "position")
    private String position;

    @Column(name = "company_email")
    private String companyEmail;

    @Column(name = "call_number")
    private String callNumber;

    @Column(name = "company_site")
    private String companySite;

    @Column(name = "company_category")
    private String companyCategory;

    @Column(name = "company_service")
    private String companyService;

    @Column(name = "comany_worker")
    private Integer companyWorker;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "personal_info_policy", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status personalInfoPolicy = Status.REJECT;

    @Column(name = "market_info_receive", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status marketInfoReceive = Status.REJECT;

    @Column(name = "expired_at")
    private LocalDateTime expiredDate;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "period_type")
    @Enumerated(EnumType.STRING)
    private CouponPeriodType couponPeriodType;

    @Column(name = "serial_key", nullable = false)
    private String serialKey;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CouponStatus status = CouponStatus.UNUSE;

    @OneToMany(mappedBy = "coupon")
    List<CouponProduct> couponProductList = new ArrayList<>();

    @OneToOne(mappedBy = "coupon", fetch = FetchType.LAZY)
    LicensePlan licensePlan;

    @Builder
    public Coupon(String company, String department, String position, String companyEmail,
                  String callNumber, String companySite, String companyCategory,
                  String companyService, Integer companyWorker, String content,
                  Status personalInfoPolicy, Status marketInfoReceive, LocalDateTime expiredDate, Long duration,
                  CouponPeriodType periodType, CouponStatus couponStatus, String userId, String serialKey) {
        this.company = company;
        this.department = department;
        this.position = position;
        this.companyEmail = companyEmail;
        this.callNumber = callNumber;
        this.companySite = companySite;
        this.companyCategory = companyCategory;
        this.companyService = companyService;
        this.companyWorker = companyWorker;
        this.content = content;
        this.personalInfoPolicy = personalInfoPolicy;
        this.marketInfoReceive = marketInfoReceive;
        this.duration = duration;
        this.couponPeriodType = periodType;
        this.status = couponStatus;
        this.userId = userId;
        this.expiredDate = expiredDate;
        this.serialKey = serialKey;
    }

}
