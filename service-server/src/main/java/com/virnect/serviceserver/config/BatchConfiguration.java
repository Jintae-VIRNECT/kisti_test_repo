package com.virnect.serviceserver.config;

import com.virnect.file.dao.File;
import com.virnect.serviceserver.batch.FileItemProcessor;
import com.virnect.serviceserver.batch.FileItemReader;
import com.virnect.serviceserver.batch.FileItemWriter;
import com.virnect.serviceserver.batch.JobCompletionNotificationListener;
import com.virnect.serviceserver.utils.LogMessage;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private static final String jovs = "expireFileJob";

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FileItemReader fileItemReader() {
        return new FileItemReader();
    }

    @Bean
    public FileItemProcessor fileItemProcessor() {
        return new FileItemProcessor();
    }

    @Bean
    public FileItemWriter fileItemWriter() {
        return new FileItemWriter();
    }

    /**
     * User when read flat file (e.g: .txt, .db, .api..etc)
     * @return
     */
    /*@Bean
    public FlatFileItemReader<File> fileItemReader() {
        return new FlatFileItemReaderBuilder<File>()
                .name("fileItemReader")
                .resource(new ClassPathResource(""))
                .delimited()
                .names("")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<File>() {{
                    setTargetType(File.class);
                }})
                .build();
    }



    @Bean
    public JdbcBatchItemWriter<File> itemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<File>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("")
                .dataSource(dataSource)
                .build();
    }*/

    /*@Bean
    public Job deleteFileJob(JobCompletionNotificationListener listener, @Qualifier("step") Step step) {
        String jobName = "deleteFileJob";
        return jobBuilderFactory.get(jobName)
                .preventRestart()
                .listener(listener)
                .flow(step)
                .end()
                .build();
    }*/

    @Bean
    public Job expireFileJob(JobCompletionNotificationListener listener, @Qualifier("step") Step step) {
        //LogMessage.formedInfo();
        String jobName = "expireFileJob";
        return jobBuilderFactory.get(jobName)
                .preventRestart()
                .listener(listener)
                .flow(step)
                .end()
                .build();
    }

    @Bean
    public Step step(FileItemReader reader,
                     FileItemProcessor processor,
                     FileItemWriter writer) {
        return stepBuilderFactory.get("step1")
                .<File, File> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    /*@Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step)
                .end()
                .build();
    }*/

    /*@Bean
    public Step step(JdbcBatchItemWriter<File> writer) {
        return stepBuilderFactory.get("step1")
                .<File, File> chunk(10)
                .reader(fileItemReader())
                .processor(fileItemProcessor())
                .writer(writer)
                .build();
    }*/


}
