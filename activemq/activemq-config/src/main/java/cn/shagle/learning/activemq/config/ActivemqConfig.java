package cn.shagle.learning.activemq.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

/**
 * Created by Danlu on 2017/4/28.
 */
@ConfigurationProperties(prefix = "spring.activemq")
@Configuration
//@EnableJms
//@PropertySource("classpath:application.properties")
//@ConditionalOnProperty(prefix="spring.activemq", name="activemq", matchIfMissing=true)
public class ActivemqConfig {

    @Value("${spring.activemq.brokerUrl}")
    private String brokerUrl;

    @Value("${spring.activemq.pool.maxConnections}")
    private int maxConnections;

    @Value("${activemq.topic.name}")
    private String defaultDestination;


    /**
     * 真正可以产生Connection的ConnectionFactory，由对应的 JMS服务厂商提供
     *
     * @return
     */
    @Bean
    public ActiveMQConnectionFactory targetConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        factory.setTrustAllPackages(true);
        return factory;
    }

    /**
     * ActiveMQ为我们提供了一个PooledConnectionFactory，通过往里面注入一个ActiveMQConnectionFactory
     * 可以用来将Connection、Session和MessageProducer池化，这样可以大大的减少我们的资源消耗。
     * 要依赖于 activemq-pool包
     *
     * @param targetConnectionFactory
     * @return
     */
    @Bean
    public PooledConnectionFactory pooledConnectionFactory(ActiveMQConnectionFactory targetConnectionFactory) {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(targetConnectionFactory);
        pooledConnectionFactory.setMaxConnections(maxConnections);
        return pooledConnectionFactory;
    }


    /**
     * Spring用于管理真正的ConnectionFactory的ConnectionFactory
     *
     * @param pooledConnectionFactory
     * @return
     */
    @Bean
    public SingleConnectionFactory connectionFactory(PooledConnectionFactory pooledConnectionFactory) {
        SingleConnectionFactory singleConnectionFactory = new SingleConnectionFactory();
        singleConnectionFactory.setTargetConnectionFactory(pooledConnectionFactory);
        return singleConnectionFactory;
    }

    /**
     * Spring提供的JMS工具类，它可以进行消息发送、接收等
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setDefaultDestinationName(defaultDestination);
        return jmsTemplate;
    }

    @Bean
    public JmsListenerContainerFactory<?> queueListenerFactory(ConnectionFactory connectionFactory,
                                                               DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        //factory.setPubSubDomain(true);
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        return factory;
    }


    @Bean
    public JmsListenerContainerFactory<?> topicListenerFactory(ConnectionFactory connectionFactory,
                                                               DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true);
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        // You could still override some of Boot's default if necessary.
        return factory;
    }


//    @Bean // Serialize message content to json using TextMessage
//    public MessageConverter jacksonJmsMessageConverter() {
//        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setTargetType(MessageType.TEXT);
//        converter.setTypeIdPropertyName("_type");
//        return converter;
//    }

}
