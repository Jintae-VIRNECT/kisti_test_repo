package com.virnect.workspace.dao.setting;

import com.virnect.workspace.domain.setting.PaymentType;
import com.virnect.workspace.domain.setting.Setting;
import com.virnect.workspace.domain.setting.SettingName;
import com.virnect.workspace.domain.setting.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByName(SettingName settingName);

    List<Setting> findByStatusAndPaymentType(Status status, PaymentType paymentType);
}
