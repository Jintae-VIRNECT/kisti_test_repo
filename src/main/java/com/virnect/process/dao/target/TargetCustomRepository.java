package com.virnect.process.dao.target;

import java.util.List;

import com.virnect.process.domain.Process;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-06-29
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface TargetCustomRepository {
	long deleteAllTargetByProcessList(List<Process> processList);
}
