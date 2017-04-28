package cn.shagle.learning.activemq.producer;

import cn.shagle.learning.activemq.message.MessagePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by Danlu on 2017/4/28.
 */
@Component
public class EmailProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${activemq.topic.name}")
    private String topic;
    @Value("${activemq.queue.name}")
    private String queue;

    public void send(final MessagePayload payload) {
        jmsTemplate.convertAndSend(topic, payload);
        jmsTemplate.convertAndSend(queue, payload);
    }
}
