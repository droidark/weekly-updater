package xyz.krakenkat.weeklyupdater;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.transaction.PlatformTransactionManager;
import xyz.krakenkat.reader.dto.ItemDTO;
import xyz.krakenkat.weeklyupdater.dto.TitleDTO;
import xyz.krakenkat.weeklyupdater.processor.IssueItemProcessor;
import xyz.krakenkat.weeklyupdater.util.Constants;
import xyz.krakenkat.weeklyupdater.writer.IssueWriter;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {

    private final MongoOperations mongoOperations;

    @Value("${weekly-updater.publisher}")
    private String publisher;

    @Value("${weekly-updater.titles}")
    private String[] titles;

    // READER FROM SQL DATABASE
    @Bean
    public JdbcCursorItemReader<TitleDTO> reader(@Qualifier("postgresSQLDataSource") DataSource dataSource) {
        String preparedTitles = Arrays.stream(titles).map(s -> "'" + s + "'").collect(Collectors.joining(","));
        return new JdbcCursorItemReaderBuilder<TitleDTO>()
                .name("titleReader")
                .dataSource(dataSource)
                .sql(String.format(Constants.QUERY, publisher, preparedTitles))
                .rowMapper(((rs, rowNum) -> TitleDTO.builder()
                        .key(rs.getString(1))
                        .title(rs.getString(2))
                        .whakoomUrl(rs.getString(3))
                        .publisherUrl(rs.getString(4))
                        .build()))
                .build();
    }

    @Bean
    public IssueItemProcessor processor() {
        return new IssueItemProcessor(mongoOperations);
    }

    @Bean
    public IssueWriter writer() { return new IssueWriter(mongoOperations); }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcCursorItemReader<TitleDTO> reader) {
        return new StepBuilder("step1", jobRepository)
                .<TitleDTO, List<ItemDTO>>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job importerUserJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }
}
