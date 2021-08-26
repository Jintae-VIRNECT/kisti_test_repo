package com.virnect.workspace.dao.workspace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import com.virnect.workspace.dao.workspace.WorkspaceRepository;
import com.virnect.workspace.domain.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import com.virnect.workspace.exception.WorkspaceException;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class WorkspaceRepositoryTests {

    @Autowired
    WorkspaceRepository workspaceRepository;

    @Test
    public void findByUuid() {
        // given
        String workspaceId = "MnefhHZkRReFX";

        // when
        Optional<Workspace> workspace =  workspaceRepository.findByUuid(workspaceId);

        // then
        assertThat(workspace.isPresent()).isTrue();
        assertThat(workspace.get().getUuid()).isEqualTo(workspaceId);
    }

}
