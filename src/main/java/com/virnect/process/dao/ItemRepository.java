package com.virnect.process.dao;

import com.virnect.process.domain.Item;
import com.virnect.process.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-20
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByReport(Report report);
}
