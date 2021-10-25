package com.virnect.workspace;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.virnect.workspace.domain.workspace.QWorkspace;
import com.virnect.workspace.domain.workspace.Workspace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QWorkspaceTest {

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Test
    void contextLoads() {

        QWorkspace qWorkspace = QWorkspace.workspace;

        Workspace result = jpaQueryFactory
                .selectFrom(qWorkspace)
                .fetchOne();

        assertThat(result.getId()).isEqualTo(138L);
    }

}