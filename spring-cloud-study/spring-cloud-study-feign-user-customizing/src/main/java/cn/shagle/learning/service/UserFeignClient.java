package cn.shagle.learning.service;

/**
 * Created by Danlu on 2017/5/15.
 */

import cn.shagle.learning.config.FeignConfiguration;
import cn.shagle.learning.dto.User;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * 使⽤@FeignClient 的configuration=FeignConfiguration.class,指定feign的配置类
 *
 * @author eacdy
 */
@FeignClient(name = "provider-user", configuration = FeignConfiguration.class)
public interface UserFeignClient {

    /**
     * 使用feign自带的注解@RequestLine
     * @see https://github.com/OpenFeign/feign
     * @param id
     * @return
     */
    @RequestLine("GET /{id}")
    public User findByIdFeign(@Param("id") Long id);
}
