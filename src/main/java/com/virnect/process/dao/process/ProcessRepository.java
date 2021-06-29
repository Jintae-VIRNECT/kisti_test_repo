package com.virnect.process.dao.process;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.process.domain.Process;

public interface ProcessRepository extends JpaRepository<Process, Long>, ProcessCustomRepository {
	Optional<Process> findById(Long processId);

	List<Process> findByContentUUID(String contentUUID);

	List<Process> findByWorkspaceUUID(String workspaceUUID);

	void deleteByWorkspaceUUID(String workspaceUUID);
}