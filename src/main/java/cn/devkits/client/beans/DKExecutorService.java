package cn.devkits.client.beans;


import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class DKExecutorService {
    @Bean
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors + 1); // 设置核心线程数
        executor.setMaxPoolSize(processors * 2); // 设置最大线程数
        executor.setQueueCapacity(processors * 10); // 设置队列容量
        executor.setThreadNamePrefix("DevKits-"); // 设置线程名前缀
        executor.initialize();
        return executor;
    }
}
