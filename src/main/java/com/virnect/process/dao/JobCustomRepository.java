package com.virnect.process.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.process.domain.Job;
import com.virnect.process.domain.SubProcess;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.22
 */
public interface JobCustomRepository {

	Page<Job> getJobPage(String myUUID, Long subProcessId, String search, Pageable pageable);

	long deleteAllJobBySubProcessList(List<SubProcess> subProcessList);
}
