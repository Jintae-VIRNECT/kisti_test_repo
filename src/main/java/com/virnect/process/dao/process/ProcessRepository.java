package com.virnect.process.dao.process;

import com.virnect.process.domain.Process;
import com.virnect.process.dto.response.ProcessesStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProcessRepository extends JpaRepository<Process, Long>, ProcessCustomRepository {
    Optional<Process> findById(Long processId);

    List<Process> findByContentUUID(String contentUUID);

    List<Process> findByWorkspaceUUID(String workspaceUUID);
}