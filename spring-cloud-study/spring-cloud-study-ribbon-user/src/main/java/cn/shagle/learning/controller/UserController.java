package cn.shagle.learning.controller;

import cn.shagle.learning.dto.User;
import cn.shagle.learning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Danlu on 2017/5/12.
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/ribbon/{id}")
    public User findById(@PathVariable Long id) {
        return this.userService.findById(id);
    }
}
