package com.indfinvestor.app.index.stats;

import com.indfinvestor.app.index.model.dto.IndexDetailsDto;
import com.indfinvestor.app.index.model.dto.IndexRollingReturns;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class IndexStatsGeneratorJobConfig {

    @Bean(name = "indexStatsItemReader")
    @StepScope
    public ItemReader<IndexDetailsDto> itemReader(
            final @Value("#{stepExecutionContext['indexDetails']}") IndexDetailsDto indexDetailsDto,
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            final @Value("#{jobParameters['startingYear']}") String startingYear) {
        return new IndexStatsItemReader(indexDetailsDto, namedParameterJdbcTemplate, startingYear);
    }

    @Bean(name = "indexStatsProcessor")
    @StepScope
    public IndexStatsProcessor itemProcessor(final @Value("#{jobParameters['startingYear']}") String startingYear) {
        return new IndexStatsProcessor(startingYear);
    }

    @Bean(name = "indexStatsItemWriter")
    @StepScope
    public IndexStatsItemWriter getItemWriter(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new IndexStatsItemWriter(namedParameterJdbcTemplate);
    }

    @Bean(name = "indexStatsTaskExecutor")
    public TaskExecutor taskExecutor() {
        var taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(10000);
        return taskExecutor;
    }

    @Bean(name = "indexStatsGeneratorJob")
    public Job job(
            final JobRepository jobRepository,
            final JdbcTransactionManager jdbcTransactionManager,
            final @Qualifier("indexStatsItemReader") ItemReader<IndexDetailsDto> itemReader,
            final @Qualifier("indexStatsProcessor") ItemProcessor<IndexDetailsDto, IndexRollingReturns> itemProcessor,
            final @Qualifier("indexStatsItemWriter") ItemWriter<IndexRollingReturns> itemWriter,
            final @Qualifier("indexDetailsPartitioner") Partitioner partitioner,
            final @Qualifier("indexStatsTaskExecutor") TaskExecutor taskExecutor) {

        var navStatsGenerationStep = new StepBuilder("indexStatsGenerationStep", jobRepository)
                .<IndexDetailsDto, IndexRollingReturns>chunk(5, jdbcTransactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();

        var partitionStep = new StepBuilder("indexStatsPartitionJobStep", jobRepository)
                .partitioner("indexStatsGenerationStep", partitioner)
                .step(navStatsGenerationStep)
                .gridSize(5)
                .taskExecutor(taskExecutor)
                .build();

        return new JobBuilder("indexStatsGeneratorJob", jobRepository)
                .start(partitionStep)
                .build();
    }
}
