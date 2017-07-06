package cn.shagle.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * Created by lenovo on 2017/6/27.
 */
@SpringBootApplication
@EnableAuthorizationServer
public class AuthApplication {
    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}
