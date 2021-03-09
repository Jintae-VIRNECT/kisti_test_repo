package com.virnect.process.dao.process;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.Process;
import com.virnect.process.global.common.PageRequest;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-02-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@SpringBootTest
class ProcessCustomRepositoryImplTest {

	@Autowired
	ProcessRepository processRepository;

	@DisplayName("내 작업 목록 조회할때 다른 워크스페이스에 소속된 작업들까지 조회되는 현상")
	@Test
	@Transactional
	void getMytask() {
		List<Conditions> conditionsList = new ArrayList<>();
		conditionsList.add(Conditions.FAILED);
		PageRequest pageRequest = new PageRequest();
		pageRequest.setSize(10);
		pageRequest.setSort("updatedDate,desc");
		pageRequest.setPage(1);
		Page<Process> processPage = processRepository.getMyTask(
			conditionsList, "4ea61b4ad1dab12fb2ce8a14b02b7460", "4d6eab0860969a50acbfa4599fbb5ae8", null,
			pageRequest.of(), null
		);
		System.out.println(processPage.getTotalPages());
		System.out.println(processPage.getTotalElements());

	}

}