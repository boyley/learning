package cn.shagle.learning.activemq.consumer;

import cn.shagle.learning.activemq.message.MessagePayload;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

/**
 * 邮件组件
 * Created by Danlu on 2017/4/28.
 */
@Component
public class EmailService {

    @JmsListener(destination = "${activemq.topic.name}", containerFactory = "topicListenerFactory")
    public void receiveTopicMessage(MessagePayload email) {
        System.out.println("topic Received <" + email + ">");
    }

    @JmsListener(destination = "${activemq.queue.name}", containerFactory = "queueListenerFactory")
    public void receiveQueueMessage(MessagePayload email) {
        System.out.println("queue Received <" + email + ">");
    }
}
