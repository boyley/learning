package cn.shagle.learning.eureka.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Created by Danlu on 2017/5/12.
 */
@EnableEurekaServer
@SpringBootApplication
public class ApplicationEurekaServerAuthenticating {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ApplicationEurekaServerAuthenticating.class).web(true).run(args);
    }
}
