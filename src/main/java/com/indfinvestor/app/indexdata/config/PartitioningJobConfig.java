package com.indfinvestor.app.indexdata.config;

import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.partition.PartitionHandler;
//import org.springframework.batch.core.partition.support.Partitioner;
//import org.springframework.batch.core.partition.support.SimplePartitioner;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.Chunk;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
//@Configuration
public class PartitioningJobConfig {


//    @Bean
//    public Job indexDataFetchJob(JobRepository jobRepository, Step step1Manager) {
//        return new JobBuilder("indexDataFetchJob", jobRepository)
//                .start(step1Manager)
//                .listener(new LoggingSkipListener())
//                .build();
//    }
//
//    @Bean
//    public Step step1Manager(JobRepository jobRepository, Step partitionStep) {
//        return new StepBuilder("step1.manager", jobRepository)
//                .<String, String>partitioner("step1", partitioner())
//                .step(partitionStep)
//                .gridSize(10)
//                .taskExecutor(taskExecutor())
//                .build();
//    }
//
//
//    @Bean
//    public Partitioner partitioner() {
//        return new SimplePartitioner();
//    }
//
//
//    @Bean
//    public Step partitionStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        return new StepBuilder("partitionStep", jobRepository)
//                .<String, String>chunk(1, transactionManager)
//                .reader(partitionedReader(null, null))  // pass ExecutionContext
//                .processor(itemProcessor())
//                .writer(itemWriter())                               // number of partitions
//                .build();
//    }
//
//
//    /**
//     * Simple example of a custom Partitioner that divides work into 4 partitions.
//     */
//
//    @Bean
//    @StepScope
//    public ItemReader<String> partitionedReader(
//            @Value("#{stepExecutionContext['start']}") Integer start,
//            @Value("#{stepExecutionContext['end']}") Integer end) {
//        return new PartitionedItemReader(start, end);
//    }
//
//    @Bean
//    public ItemProcessor<String, String> itemProcessor() {
//        return new SampleItemProcessor();
//    }
//
//    @Bean
//    public ItemWriter<String> itemWriter() {
//        return new SampleItemWriter();
//    }
//
//    @Bean
//    public TaskExecutor taskExecutor() {
//        return new SimpleAsyncTaskExecutor();
//    }
//
//    // Sample stub classes
//    static class PartitionedItemReader implements ItemReader<String> {
//        private final int start;
//        private final int end;
//        private int current;
//
//        PartitionedItemReader(Integer start, Integer end) {
//            this.start = (start == null) ? 0 : start;
//            this.end = (end == null) ? 0 : end;
//            this.current = this.start;
//        }
//
//        @Override
//        public String read() {
//            return "jubin";
//        }
//    }
//
//    static class SampleItemProcessor implements ItemProcessor<String, String> {
//        @Override
//        public String process(String item) {
//            // Transform or enrich the item
//            return item.toUpperCase();
//        }
//    }
//
//    static class SampleItemWriter implements ItemWriter<String> {
//
//        @Override
//        public void write(Chunk<? extends String> chunk) throws Exception {
//
//        }
//    }
}
