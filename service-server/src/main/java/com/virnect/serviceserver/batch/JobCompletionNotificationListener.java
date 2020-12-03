package com.virnect.serviceserver.batch;

import com.virnect.serviceserver.infra.file.S3FileManagementService;
import com.virnect.serviceserver.utils.LogMessage;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

/*@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    private static final String TAG = JobCompletionNotificationListener.class.getSimpleName();



    @Override
    public void afterJob(JobExecution jobExecution) {
        //super.afterJob(jobExecution);
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            LogMessage.formedInfo(
                    TAG,
                    "batch jot complete notified: " + jobExecution.getJobInstance().getJobName(),
                    "afterJob"
            );
        }
    }
}*/
