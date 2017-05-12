package cn.shagle.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created by Danlu on 2017/5/12.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApplicationProvder {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationProvder.class, args);
    }
}
