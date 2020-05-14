package com.virnect.process.dao.process;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProcessCustomRepository {

    Process findByTargetDataAndState(String targetData, State state);

    //List<Process> getProcessListSearchUser(String workspaceUUID, String title, List<String> userUUIDList);

    Page<Process> getProcessPageSearchUser(String workspaceUUID, String title, List<String> userUUIDList, Pageable pageable);
}
