package com.virnect.process.dao;

import com.virnect.process.domain.SmartTool;
import com.virnect.process.domain.SmartToolItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-20
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface SmartToolItemRepository extends JpaRepository<SmartToolItem, Long> {
    List<SmartToolItem> findBySmartTool(SmartTool smartTool);
}
