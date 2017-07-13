package cn.shagle.learning.web.filter;

import cn.shagle.learning.web.http.MutableHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by lenovo on 2017/7/7.
 */
public class AccessTokenFilter extends OncePerRequestFilter {

    TokenExtractor tokenExtractor = new BearerTokenExtractor();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = tokenExtractor.extract(request);
        MutableHttpServletRequest req = null;
        if (authentication == null) {
            req = new MutableHttpServletRequest(request);
            String accessToken = OAuth2AccessToken.BEARER_TYPE + " " + UUID.randomUUID().toString();
            req.putHeader("Authorization", accessToken);
            response.addHeader("Authorization", accessToken);
        }
        filterChain.doFilter(req == null ? request : req, response);
    }
}
