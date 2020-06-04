package com.virnect.process.dao;

import com.virnect.process.domain.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.22
 */
public interface JobCustomRepository {

    Page<Job> getJobPage(Long subProcessId, String search, Pageable pageable);
}
