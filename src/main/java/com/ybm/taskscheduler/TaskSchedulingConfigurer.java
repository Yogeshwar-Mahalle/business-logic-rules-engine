/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.taskscheduler;

import com.ybm.taskRepo.dbRepository.SchedulerLockRepository;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.sql.DataSource;
import java.util.concurrent.Executor;

/*
 ┌───────────── second (0-59)
 │ ┌───────────── minute (0 - 59)
 │ │ ┌───────────── hour (0 - 23)
 │ │ │ ┌───────────── day of the month (1 - 31)
 │ │ │ │ ┌───────────── month (1 - 12) (or JAN-DEC)
 │ │ │ │ │ ┌───────────── day of the week (0 - 7)
 │ │ │ │ │ │          (or MON-SUN -- 0 or 7 is Sunday)
 │ │ │ │ │ │
 * * * * * *
*/

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
@EnableAsync
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class TaskSchedulingConfigurer implements AsyncConfigurer, SchedulingConfigurer {

    @Value( "${threadpooltaskexecutor.corePoolSize:5}" )
    private Integer corePoolSize;
    @Value( "${threadpooltaskexecutor.maxPoolSize:100}" )
    private Integer maxPoolSize;
    @Value( "${threadpooltaskexecutor.queueCapacity:500}" )
    private Integer queueCapacity;
    SchedulerLockRepository schedulerLockRepository;

    public TaskSchedulingConfigurer(SchedulerLockRepository schedulerLockRepository) {
        this.schedulerLockRepository = schedulerLockRepository;
    }


    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("BLRuleEngine-");
        executor.initialize();
        return executor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(corePoolSize);
        threadPoolTaskScheduler.setThreadNamePrefix("BLRuleEngine-taskscheduler-pool-");
        threadPoolTaskScheduler.initialize();

        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }


    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime() // Works on Postgres, MySQL, MariaDb, MS SQL, Oracle, DB2, HSQL and H2
                        .build()
        );
    }

    /*@Transactional
    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void scrapSourceWebsite() {
        long currentTime = System.currentTimeMillis();
        long scheduledRate = Duration.of(1, ChronoUnit.HOURS).toMillis(); // 1h
        schedulerLockRepository.findByTaskIdAndLastExecutionLessThan("scrap_website", currentTime - scheduledRate)
                .ifPresent(scrapingTask -> {
                    // Execute scraping task...
                    scrapingTask.setLastExecution(System.currentTimeMillis());
                });
    }

    @Transactional
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void moveDeletedToColdStorage() {
        long currentTime = System.currentTimeMillis();
        long scheduledRate = Duration.of(6, ChronoUnit.HOURS).toMillis(); // 6h
        schedulerLockRepository.findByTaskIdAndLastExecutionLessThan("move_to_cold", currentTime - scheduledRate)
                .ifPresent(moveToCold -> {
                    // Execute migration of deleted to cold storage...
                    moveToCold.setLastExecution(System.currentTimeMillis());
                });
    }*/

}
