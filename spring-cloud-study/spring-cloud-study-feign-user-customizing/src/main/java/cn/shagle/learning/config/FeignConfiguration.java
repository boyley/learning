package cn.shagle.learning.config;

import feign.Contract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 该类为Feign的配置类
 * 注意：该类不能在主应用程序上下文的@ComponentScan中
 * Created by lenovo on 2017/5/28.
 */
@Configuration
public class FeignConfiguration {

    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }
}
