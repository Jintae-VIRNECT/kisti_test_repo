package com.virnect.workspace.domain.setting;

import com.virnect.workspace.domain.TimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-01
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "setting")
@Entity
@Getter
public class Setting extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private SettingName name; //유니크 걸기

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "product", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Builder
    public Setting(SettingName settingName, Status status, Product product, PaymentType paymentType) {
        this.name = settingName;
        this.status = status;
        this.product = product;
        this.paymentType = paymentType;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
}
