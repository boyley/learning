package cn.shagle.learning.security.model;



import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by lenovo on 2017/6/18.
 */
public class UserContext {
    private final String username;
    private final List<String> authorities;

    private UserContext(String username, List<String> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public static UserContext create(String username, List<String> authorities) {
        if (StringUtils.isBlank(username)) throw new IllegalArgumentException("Username is blank: " + username);
        return new UserContext(username, authorities);
    }

    public String getUsername() {
        return username;
    }

    public List<String> getAuthorities() {
        return authorities;
    }
}