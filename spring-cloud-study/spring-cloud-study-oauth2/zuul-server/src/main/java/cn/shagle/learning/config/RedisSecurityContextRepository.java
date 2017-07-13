package cn.shagle.learning.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.ClassUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by lenovo on 2017/7/6.
 */
public class RedisSecurityContextRepository implements SecurityContextRepository {

    private final static Logger logger = LoggerFactory.getLogger(RedisSecurityContextRepository.class);

    private boolean disableUrlRewriting = false;
    private boolean isServlet3 = ClassUtils.hasMethod(ServletRequest.class, "startAsync");

    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    @Autowired
    private RedisTemplate<String, SecurityContext> redisTemplate;

    private long cacheTime=30000;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        final boolean debug = logger.isDebugEnabled();
        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpServletResponse response = requestResponseHolder.getResponse();

        String tokenValue = extractToken(request);
        SecurityContext context = readSecurityContextFromSession(tokenValue);

        if (context == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No SecurityContext was available from the Redis: "
                        + tokenValue + ". " + "A new one will be created.");
            }
            context = generateNewContext();

        }

        SaveToRedisResponseWrapper wrappedResponse = new SaveToRedisResponseWrapper(
                response, request, context);
        requestResponseHolder.setResponse(wrappedResponse);

        if (isServlet3) {
            requestResponseHolder.setRequest(new Servlet3SaveToRedisRequestWrapper(request, wrappedResponse));
        }
        return context;
    }


    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        SaveToRedisResponseWrapper responseWrapper = WebUtils
                .getNativeResponse(response,
                        SaveToRedisResponseWrapper.class);
        if (responseWrapper == null) {
            throw new IllegalStateException(
                    "Cannot invoke saveContext on response "
                            + response
                            + ". You must use the HttpRequestResponseHolder.response after invoking loadContext");
        }
        if (!responseWrapper.isContextSaved()) {
            responseWrapper.saveContext(context);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String tokenValue = extractToken(request);
        if (tokenValue == null) {
            return false;
        }

        return this.loadSecurityContext(tokenValue) == null;
    }


    private Object loadSecurityContext(String tokenValue) {
        return redisTemplate.opsForValue().get(tokenValue);
    }

    private void saveContext(String tokenValue, SecurityContext securityContext) {
        redisTemplate.opsForValue().set(tokenValue,securityContext,cacheTime, TimeUnit.MILLISECONDS);
    }

    private static class Servlet3SaveToRedisRequestWrapper extends
            HttpServletRequestWrapper {
        private final SaveContextOnUpdateOrErrorResponseWrapper response;

        public Servlet3SaveToRedisRequestWrapper(HttpServletRequest request,
                                                 SaveContextOnUpdateOrErrorResponseWrapper response) {
            super(request);
            this.response = response;
        }

        @Override
        public AsyncContext startAsync() {
            response.disableSaveOnResponseCommitted();
            return super.startAsync();
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest,
                                       ServletResponse servletResponse) throws IllegalStateException {
            response.disableSaveOnResponseCommitted();
            return super.startAsync(servletRequest, servletResponse);
        }
    }

    final class SaveToRedisResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {
        private final HttpServletRequest request;
        private final SecurityContext contextBeforeExecution;
        private final Authentication authBeforeExecution;

        @Autowired
        public void onResponseCommitted() {
           super.onResponseCommitted();
        }

        public SaveToRedisResponseWrapper(HttpServletResponse response,
                                          HttpServletRequest request,
                                          SecurityContext context) {
            super(response, disableUrlRewriting);
            this.request = request;
            this.contextBeforeExecution = context;
            this.authBeforeExecution = context.getAuthentication();
        }

        @Override
        protected void saveContext(SecurityContext context) {
            final Authentication authentication = context.getAuthentication();
            if (authentication == null || trustResolver.isAnonymous(authentication) && !context.getAuthentication().isAuthenticated()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("SecurityContext is empty or contents are anonymous - context will not be stored in Redis.");
                }
                return;
            }
            String tokenValue = extractToken(request);
            if (tokenValue != null) {
                if (contextChanged(context) || RedisSecurityContextRepository.this.loadSecurityContext(tokenValue) == null) {
                    RedisSecurityContextRepository.this.saveContext(tokenValue, context);
                    if (logger.isDebugEnabled()) {
                        logger.debug("SecurityContext '" + context
                                + "' stored to Redis: '" + tokenValue);
                    }
                }
            }
        }

        private boolean contextChanged(SecurityContext context) {
            return context != contextBeforeExecution
                    || context.getAuthentication() != authBeforeExecution;
        }
    }

    private SecurityContext generateNewContext() {
        return SecurityContextHolder.createEmptyContext();
    }

    private SecurityContext readSecurityContextFromSession(String tokenValue) {
        final boolean debug = logger.isDebugEnabled();
        if (tokenValue == null) {
            if (debug) {
                logger.debug("No tokenValue currently exists");
            }

            return null;
        }
        Object contextFromRedis = loadSecurityContext(tokenValue);

        if (contextFromRedis == null) {
            if (debug) {
                logger.debug("Redis returned null object for SPRING_SECURITY_CONTEXT");
            }

            return null;
        }

        // We now have the security context object from the session.
        if (!(contextFromRedis instanceof SecurityContext)) {
            if (logger.isWarnEnabled()) {
                logger.warn(tokenValue
                        + " did not contain a SecurityContext but contained: '"
                        + contextFromRedis
                        + "'; are you improperly modifying the Redis directly "
                        + "(you should always use SecurityContextHolder) or using the HttpSession attribute "
                        + "reserved for this class?");
            }

            return null;
        }

        if (debug) {
            logger.debug("Obtained a valid SecurityContext from "
                    + tokenValue + ": '" + contextFromRedis + "'");
        }

        // Everything OK. The only non-null return from this method.

        return (SecurityContext) contextFromRedis;

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
        String headers = request.getHeader("Authorization");
        while (headers != null) { // typically there is only one (most servers enforce that)
            String value = headers;
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

}
