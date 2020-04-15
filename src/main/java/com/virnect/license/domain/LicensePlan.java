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
@Table(name = "license_plan")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LicensePlan extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "license_plan_id")
    private Long id;

    @Column(name = "expired_at")
    private LocalDateTime expiredDate;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PlanStatus planStatus = PlanStatus.INACTIVE;

    @OneToMany(mappedBy = "licensePlan")
    private List<License> licenseList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    Coupon coupon;

    @Builder
    public LicensePlan(String userId, LocalDateTime expiredDate, PlanStatus planStatus, Coupon coupon) {
        this.userId = userId;
        this.expiredDate = expiredDate;
        this.planStatus = planStatus;
        this.coupon = coupon;
    }
}
