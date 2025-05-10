package com.indfinvestor.app.indexdata.source;

import com.indfinvestor.app.config.BatchJobConfiguration;
import com.indfinvestor.app.indexdata.config.PartitioningJobConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBatchTest
@SpringJUnitConfig(classes = {PartitioningJobConfig.class, BatchJobConfiguration.class})
public class PartitionJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Test
    public void testJob() throws Exception {

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        System.out.println(jobExecution.getStatus().isRunning());
        while (jobExecution.getStatus().isRunning()){
            System.out.println(jobExecution.getStatus().isRunning());
            Thread.sleep(10000);
        }


        Assertions.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }
}