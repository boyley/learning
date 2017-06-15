package cn.shagle.learning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Danlu on 2017/6/15.
 */

@ConfigurationProperties(
        prefix = "app",
        ignoreUnknownFields = false
)
public class AppProperties {

    private final AppProperties.Async async = new AppProperties.Async();


    public Async getAsync() {
        return async;
    }

    /**
     * 线程池配置
     */
    public static class Async {
        private int corePoolSize = 2;
        private int maxPoolSize = 50;
        private int queueCapacity = 10000;

        public Async() {
        }

        public int getCorePoolSize() {
            return this.corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return this.maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity() {
            return this.queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }
    }
}
