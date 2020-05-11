package com.virnect.license.domain;

import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    @Column(name = "start_at")
    private LocalDateTime startDate;

    @Column(name = "expired_at")
    private LocalDateTime endDate;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "storage_amount", nullable = false)
    private Long storageAmount;

    @Column(name = "download_hit", nullable = false)
    private Long downloadHit;

    @Column(name = "call_time", nullable = false)
    private Long callTime;

    // nullable false 로 나중에 바꾸기
    @Column(name = "workspace_id")
    private String workspaceId;

    @Column(name = "modified_user", nullable = false)
    @LastModifiedBy
    private String modifiedUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PlanStatus planStatus = PlanStatus.INACTIVE;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "licensePlan")
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @OneToMany(mappedBy = "licensePlan", fetch = FetchType.LAZY)
    private List<LicenseProduct> licenseProductList;

    @Builder
    public LicensePlan(String userId, String workspaceId, LocalDateTime startDate, LocalDateTime endDate, PlanStatus planStatus, Coupon coupon, Long downloadHit, Long storageAmount, Long callTime) {
        this.userId = userId;
        this.workspaceId = workspaceId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.planStatus = planStatus;
        this.coupon = coupon;
        this.downloadHit = downloadHit;
        this.callTime = callTime;
        this.storageAmount = storageAmount;
        this.modifiedUser = userId;
    }
}
