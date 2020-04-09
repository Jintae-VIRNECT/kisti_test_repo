package com.virnect.license.dao;

import com.virnect.license.domain.LicensePlan;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-License
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.09
 */
public interface LicensePlanRepository extends JpaRepository<LicensePlan, Long> {
}
