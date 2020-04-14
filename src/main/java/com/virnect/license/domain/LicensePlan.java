package com.virnect.license.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @Column(name = "status")
    private PlanStatus planStatus = PlanStatus.INACTIVE;

    @OneToMany(mappedBy = "licensePlan")
    private List<License> licenseList = new ArrayList<>();
}
