package cn.shagle.learning.hystrix;

import cn.shagle.learning.dto.User;
import cn.shagle.learning.service.UserFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 这边采取了和Spring Cloud官⽅⽂档相同的做法，将fallback类作为内部
 * 类放⼊Feign的接⼝中，当然也可以单独写⼀个fallback类。
 *
 * @author eacdy
 */
@Component
public class UserFeignClientFallback implements UserFeignClient {

    private static final Logger logger = LoggerFactory.getLogger(UserFeignClientFallback.class);

    @Override
    public User findByIdFeign(Integer id) {
        logger.error("异常发⽣，进⼊fallback⽅法，接收的参数：id = {}", id);
        User user = new User();
        user.setId(-1);
        user.setUsername("default username");
        user.setAge(0);
        return user;
    }
}
