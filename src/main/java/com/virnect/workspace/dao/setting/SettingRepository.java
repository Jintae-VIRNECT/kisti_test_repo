package com.virnect.workspace.dao.setting;

import com.virnect.workspace.domain.setting.*;
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
    Optional<Setting> findByNameAndStatus(SettingName settingName, Status status);

    List<Setting> findByStatusAndPaymentType(Status status, PaymentType paymentType);
    List<Setting> findByStatusAndProduct(Status status, Product product);
}
