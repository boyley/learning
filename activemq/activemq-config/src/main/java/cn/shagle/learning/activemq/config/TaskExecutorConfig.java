package cn.shagle.learning.activemq.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by lenovo on 2017/4/27.
 */
@EnableAsync
@Configuration
public class TaskExecutorConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        threadPoolTaskExecutor.setCorePoolSize(5);
        //线程池维护线程的最大数量
        threadPoolTaskExecutor.setMaxPoolSize(10);
        // 允许的空闲时间
        threadPoolTaskExecutor.setKeepAliveSeconds(200);
        //缓存队列
        threadPoolTaskExecutor.setQueueCapacity(25);
        //对拒绝task的处理策略
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}