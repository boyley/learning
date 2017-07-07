package cn.shagle.learning.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoDefaultConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by lenovo on 2017/7/6.
 */
@Configuration
@EnableOAuth2Sso
public class Oauth2SsoConfig extends OAuth2SsoDefaultConfiguration {

    public Oauth2SsoConfig(ApplicationContext applicationContext, OAuth2SsoProperties sso) {
        super(applicationContext, sso);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/**/*.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
                .csrf()
                .csrfTokenRepository(csrfTokenRepository())
                .and()
                .securityContext().securityContextRepository(securityContextRepository()).and()
                .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
                .addFilterBefore(tokenFilter(), SecurityContextPersistenceFilter.class);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new RedisSecurityContextRepository();
    }

    private Filter tokenFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                String tokenValue = extractToken(request);
                if (tokenValue == null) {
                    request.setAttribute(OAuth2AccessToken.ACCESS_TOKEN, UUID.randomUUID().toString());
                }
                filterChain.doFilter(request, response);
            }


            private String extractToken(HttpServletRequest request) {
                // first check the header...
                String token = extractHeaderToken(request);

                // bearer type allows a request parameter as well
                if (token == null) {
                    logger.debug("Token not found in headers. Trying request parameters.");
                    token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
                    if (token == null) {
                        logger.debug("Token not found in request parameters.  Not an OAuth2 request.");
                    } else {
                        request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, OAuth2AccessToken.BEARER_TYPE);
                    }
                }

                return token;
            }

            private String extractHeaderToken(HttpServletRequest request) {
                Enumeration<String> headers = request.getHeaders("Authorization");
                while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
                    String value = headers.nextElement();
                    if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
                        String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
                        // Add this here for the auth details later. Would be better to change the signature of this method.
                        request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE,
                                value.substring(0, OAuth2AccessToken.BEARER_TYPE.length()).trim());
                        int commaIndex = authHeaderValue.indexOf(',');
                        if (commaIndex > 0) {
                            authHeaderValue = authHeaderValue.substring(0, commaIndex);
                        }
                        return authHeaderValue;
                    }
                }

                return null;
            }
        };
    }

    private Filter csrfHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrf != null) {
                    Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                    String token = csrf.getToken();
                    if (cookie == null || token != null && !token.equals(cookie.getValue())) {
                        cookie = new Cookie("XSRF-TOKEN", token);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }
}

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class WorkaroundRestTemplateCustomizer implements UserInfoRestTemplateCustomizer {

    @Override
    public void customize(OAuth2RestTemplate template) {
        template.setInterceptors(new ArrayList<>(template.getInterceptors()));
    }

}