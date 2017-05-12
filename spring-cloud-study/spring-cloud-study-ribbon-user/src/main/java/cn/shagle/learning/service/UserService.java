package cn.shagle.learning.service;

import cn.shagle.learning.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Danlu on 2017/5/12.
 */
@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    public User findById(Long id) {
// http://服务提供者的serviceId/url
        return this.restTemplate.getForObject("http://provider-user/" + id, User.class);
    }
}
