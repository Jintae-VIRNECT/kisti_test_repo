package com.virnect.workspace;

import com.virnect.workspace.dao.setting.SettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Order(2)
public class SettingSyncRunner implements ApplicationRunner {
    private final SettingRepository settingRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {/*
        if (SettingName.values().length != settingRepository.count()) {
            for (SettingName value : SettingName.values()) {
                if (!settingRepository.findByName(value).isPresent()) {
                    Setting setting = Setting.builder()
                            .settingName(value)
                            .status(Status.ACTIVE)
                            .paymentType(PaymentType.FREE)
                            .product(value.getProduct())
                            .build();
                    log.info("[SETTING INFO SYNC] new Setting name : {}, status : {}, paymentType : {}", setting.getName(), setting.getStatus(), setting.getPaymentType());
                    settingRepository.save(setting);
                }
            }
        }*/
    }
}
