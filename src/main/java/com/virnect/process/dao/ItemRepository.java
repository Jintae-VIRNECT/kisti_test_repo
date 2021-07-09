package com.virnect.process.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.process.domain.Item;
import com.virnect.process.domain.Report;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-20
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface ItemRepository extends JpaRepository<Item, Long>, ItemCustomRepository {
	List<Item> findByReportIn(List<Report> reportList);

	List<Item> findByReport(Report report);

	void deleteAllByReport(Report report);
}
