package cn.shagle.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.security.Principal;

/**
 * Created by lenovo on 2017/6/27.
 */
@SpringBootApplication
@EnableResourceServer
@EnableDiscoveryClient
@RestController
@SessionAttributes("authorizationRequest")
public class AuthApplication {
    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}
