package cn.shagle.learning.config;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danlu on 2017/7/7.
 */
@Configuration
@ConditionalOnClass({JedisConnection.class, RedisOperations.class, Jedis.class})
@EnableConfigurationProperties(TokenProperties.class)
public class RedisTokenConfig {

    /**
     * Redis connection configuration.
     */
    @Configuration
    @ConditionalOnClass(GenericObjectPool.class)
    protected static class RedisConnectionConfiguration {

        private final TokenProperties properties;

        public RedisConnectionConfiguration(TokenProperties properties) {
            this.properties = properties;
        }

        @Bean
        @ConditionalOnMissingBean(RedisConnectionFactory.class)
        public JedisConnectionFactory redisConnectionFactory()
                throws UnknownHostException {
            return applyProperties(createJedisConnectionFactory());
        }

        protected final JedisConnectionFactory applyProperties(
                JedisConnectionFactory factory) {
            configureConnection(factory);
            if (this.properties.isSsl()) {
                factory.setUseSsl(true);
            }
            factory.setDatabase(this.properties.getDatabase());
            if (this.properties.getTimeout() > 0) {
                factory.setTimeout(this.properties.getTimeout());
            }
            return factory;
        }

        private void configureConnection(JedisConnectionFactory factory) {
            if (StringUtils.hasText(this.properties.getUrl())) {
                configureConnectionFromUrl(factory);
            } else {
                factory.setHostName(this.properties.getHost());
                factory.setPort(this.properties.getPort());
                if (this.properties.getPassword() != null) {
                    factory.setPassword(this.properties.getPassword());
                }
            }
        }

        private void configureConnectionFromUrl(JedisConnectionFactory factory) {
            String url = this.properties.getUrl();
            if (url.startsWith("rediss://")) {
                factory.setUseSsl(true);
            }
            try {
                URI uri = new URI(url);
                factory.setHostName(uri.getHost());
                factory.setPort(uri.getPort());
                if (uri.getUserInfo() != null) {
                    String password = uri.getUserInfo();
                    int index = password.lastIndexOf(":");
                    if (index >= 0) {
                        password = password.substring(index + 1);
                    }
                    factory.setPassword(password);
                }
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException("Malformed 'spring.redis.url' " + url,
                        ex);
            }
        }


        private JedisConnectionFactory createJedisConnectionFactory() {
            JedisPoolConfig poolConfig = this.properties.getPool() != null
                    ? jedisPoolConfig() : new JedisPoolConfig();

            return new JedisConnectionFactory(poolConfig);
        }

        private JedisPoolConfig jedisPoolConfig() {
            JedisPoolConfig config = new JedisPoolConfig();
            TokenProperties.Pool props = this.properties.getPool();
            config.setMaxTotal(props.getMaxActive());
            config.setMaxIdle(props.getMaxIdle());
            config.setMinIdle(props.getMinIdle());
            config.setMaxWaitMillis(props.getMaxWait());
            return config;
        }

    }

    @Configuration
    protected static class RedisConfiguration {

        @Bean
        @ConditionalOnMissingBean(name = "redisTemplate")
        public RedisTemplate<Object, Object> redisTemplate(
                RedisConnectionFactory redisConnectionFactory)
                throws UnknownHostException {
            RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

        @Bean
        @ConditionalOnMissingBean(StringRedisTemplate.class)
        public StringRedisTemplate stringRedisTemplate(
                RedisConnectionFactory redisConnectionFactory)
                throws UnknownHostException {
            StringRedisTemplate template = new StringRedisTemplate();
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

    }

}
