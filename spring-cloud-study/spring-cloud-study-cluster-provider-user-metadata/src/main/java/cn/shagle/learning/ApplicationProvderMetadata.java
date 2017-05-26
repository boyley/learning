package cn.shagle.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @EnableEurekaClient 等同于 @EnableDiscoveryClient
 * @EnableEurekaClient是Eureka自带的主键表明该注解是Eureka的Client，该注解是spring-cloud-netflix的注解，只能和Eureka一起工作
 * @EnableDiscoveryClient 在spring cloud中，服务组件有多种选择，列如Zookeeper，Consule等，@EnableDiscoveryClient为各种服务组件提供了支持
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApplicationProvderMetadata {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationProvderMetadata.class, args);
    }
}
