package com.virnect.serviceserver.test.batch;

import com.virnect.file.repository.FileRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration
@TestPropertySource(properties = {"spring.batch.job."})
public class JobFilePagingTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private FileRepository fileRepository;

    @Test
    public void expiredTest() throws Exception {
        JobExecution execution = jobLauncherTestUtils.launchJob();


        //Assert.assertThat(execution.getStatus(), Assert.asser);
        //Assert.assertThat(execution.getStatus())

    }
}
