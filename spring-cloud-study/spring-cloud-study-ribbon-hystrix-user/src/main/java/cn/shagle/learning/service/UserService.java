package cn.shagle.learning.service;

import cn.shagle.learning.dto.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Danlu on 2017/5/12.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 使⽤@HystrixCommand注解指定当该⽅法发⽣异常时调⽤的⽅法
     * @param id id
     * @return 通过id查询到的⽤户
     */
    @HystrixCommand(fallbackMethod = "fallback")
    public User findById(Long id) {
// http://服务提供者的serviceId/url
        return this.restTemplate.getForObject("http://provider-user/" + id, User.class);
    }

    /**
     * hystrix fallback⽅法
     *
     * @param id id
     * @return 默认的⽤户
     */
    public User fallback(Long id) {
        logger.error("异常发⽣，进⼊fallback⽅法，接收的参数：id = {}", id);
        User user = new User();
        user.setId(-1);
        user.setUsername("default username");
        user.setAge(0);
        return user;
    }
}
