package com.indfinvestor.app.index.nse.bulkload;

import com.indfinvestor.app.index.model.entity.dto.IndexNavDetails;
import com.indfinvestor.app.index.service.IndexDetailsService;
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
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Slf4j
@Configuration
public class NseBulkFileNavTransformerJobConfig {

    @Bean(name = "nseFilePartitioner")
    @StepScope
    public Partitioner getPartitioner(final @Value("#{jobParameters['inputPath']}") String inputPath) {
        return new NseFilePartitioner(inputPath);
    }

    @Bean(name = "nseCsvItemReader")
    @StepScope
    public ItemReader<IndexNavDetails> csvItemReader(@Value("#{stepExecutionContext[fileName]}") String filename) {
        return new NseCsvItemReader(filename);
    }

    @Bean(name = "nseFileItemProcessor")
    @StepScope
    public NseFileItemProcessor itemProcessor() {
        return new NseFileItemProcessor();
    }

    @Bean(name = "nseItemValidator")
    public BeanValidatingItemProcessor<IndexNavDetails> itemValidator() throws Exception {
        BeanValidatingItemProcessor<IndexNavDetails> validator = new BeanValidatingItemProcessor<>();
        validator.setFilter(true);
        validator.afterPropertiesSet();
        return validator;
    }

    @Bean(name = "nseFileItemWriter")
    @StepScope
    public NseFileItemWriter getItemWriter(
            final IndexDetailsService indexDetailsService,
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new NseFileItemWriter(indexDetailsService, namedParameterJdbcTemplate);
    }

    @Bean(name = "nseBulkFileNavTransformerJob")
    public Job job(
            final JobRepository jobRepository,
            final JdbcTransactionManager jdbcTransactionManager,
            final @Qualifier("nseCsvItemReader") ItemReader<IndexNavDetails> itemReader,
            final @Qualifier("nseFileItemProcessor") ItemProcessor<IndexNavDetails, IndexNavDetails> itemProcessor,
            final @Qualifier("nseFileItemWriter") ItemWriter<IndexNavDetails> itemWriter,
            final @Qualifier("nseFilePartitioner") Partitioner partitioner)
            throws Exception {

        var step1 = new StepBuilder("step1", jobRepository)
                .<IndexNavDetails, IndexNavDetails>chunk(5, jdbcTransactionManager)
                .reader(itemReader)
                .processor(itemValidator())
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();

        var partitionStep = new StepBuilder("partitionJobStep", jobRepository)
                .partitioner("step1", partitioner)
                .step(step1)
                .gridSize(4)
                .taskExecutor(new VirtualThreadTaskExecutor())
                .build();

        return new JobBuilder("nseBulkNavTransformerJob", jobRepository)
                .start(partitionStep)
                .build();
    }
}
