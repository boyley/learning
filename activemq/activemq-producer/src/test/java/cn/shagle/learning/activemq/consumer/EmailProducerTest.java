package cn.shagle.learning.activemq.consumer;

import cn.shagle.learning.activemq.message.MessagePayload;
import cn.shagle.learning.activemq.producer.EmailProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Danlu on 2017/4/28.
 */
@RunWith(SpringRunner.class)
@SpringBootApplication(scanBasePackages = "cn.shagle.learning")
@SpringBootTest
public class EmailProducerTest {

    @Autowired
    private EmailProducer emailProducer;

    @Test
    public void sendMailTest() {
        MessagePayload payload = new MessagePayload("136397156@qq.com", "测试标题", "测试内容");
        emailProducer.send(payload);
    }
}
