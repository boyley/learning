package cn.shagle.learning.activemq.consumer;

/**
 * Created by Danlu on 2017/4/28.
 */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootApplication(scanBasePackages = "cn.shagle.learning")
@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    public void sendEmailTest() {
        System.out.println("sendEmail");
    }
}
