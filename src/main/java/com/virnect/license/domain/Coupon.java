package com.virnect.license.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "user_id")
    private String userId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CouponStatus  status = CouponStatus.UNUSE;

}
