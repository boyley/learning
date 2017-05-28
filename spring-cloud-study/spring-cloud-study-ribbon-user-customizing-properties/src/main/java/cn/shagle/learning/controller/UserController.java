package cn.shagle.learning.controller;

import cn.shagle.learning.dto.User;
import cn.shagle.learning.service.UserService;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Danlu on 2017/5/12.
 */
@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("/ribbon/{id}")
    public User findById(@PathVariable Long id) {
        return this.userService.findById(id);
    }

    @GetMapping("/log-instance")
    public void logUserInstance() {
        // 打印当前选择的是哪个节点
        ServiceInstance serviceInstance = this.loadBalancerClient.choose("provider-user");
        logger.info("{}:{}:{}",serviceInstance.getServiceId(),serviceInstance.getHost(),serviceInstance.getPort());
    }
}
