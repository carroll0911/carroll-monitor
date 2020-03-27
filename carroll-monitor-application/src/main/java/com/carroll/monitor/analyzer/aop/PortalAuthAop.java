package com.carroll.monitor.analyzer.aop;

import com.carroll.auth.entity.UserTokenEntity;
import com.carroll.auth.user.UserTokenUtils;
import com.carroll.cache.RedisUtil;
import com.carroll.monitor.analyzer.config.PassportConf;
import com.carroll.monitor.analyzer.dto.UserCacheDto;
import com.carroll.monitor.analyzer.enums.Role;
import com.carroll.monitor.analyzer.service.IOperatorService;
import com.carroll.monitor.analyzer.utils.BizContext;
import com.carroll.spring.rest.starter.BaseException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Aspect
@Component
@Order(3)
public class PortalAuthAop {
    private static final Logger log = LoggerFactory.getLogger(PortalAuthAop.class);

    private static final String PREFIX_FORMAT = "\n";
    private static final String COOKIE_TOKEN_KEY = "token";
    private static final String PUID_KEY = "puid";
    private static final String REDIS_TOKEN_SUFFIX = "_portal_token";
    private static final String TOKEN_ERROR = "40002";
    private static final String TOKEN_ISNULL = "40003";
    private static final String TOKEN_ERROR_MSG = "token超时，请重新登录";
    private static final String TOKEN_ISNULL_MSG = "token不能为空";

    @Autowired
    private PassportConf passportConf;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IOperatorService operatorService;

    /**
     * 定义拦截规则：拦截org.springframework.web.bind.annotation.RequestMapping注解的方法。
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) && execution(* com.carroll.monitor.analyzer.controller..*.*(..))")
    public void controllerMethodPointcut() {
        // 登录验证拦截pointcut
    }

    /**
     * 前置通知
     *
     * @param joinPoint
     * @return JsonResult（被拦截方法的执行结果，或需要登录的错误提示。）
     */
    @Before("controllerMethodPointcut()") //指定拦截器规则；也可以直接把规则的内容写进这里
    @SuppressWarnings("unused")
    private void beforeInterceptor(JoinPoint joinPoint) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String requestPath = request.getRequestURI();

        if (requestPath.indexOf("v2/grafana") > -1) {
            return;
        }
        log.info("passportConf: ".concat(passportConf.getExcludes().toString()));
        log.info("request uri: ".concat(requestPath));
        if (!passportConf.getExcludes().contains(requestPath)) {
            String token = request.getHeader(PUID_KEY);
            if (null == token) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        String cookieName = cookie.getName();
                        if (COOKIE_TOKEN_KEY.equals(cookieName)) {
                            token = cookie.getValue();
                            break;
                        }
                    }
                }
            }
            if (null == token) {
                throw new BaseException(TOKEN_ISNULL, TOKEN_ISNULL_MSG);
            }
            boolean isLogin = verifyToken(token);
            if (!isLogin) {
                throw new BaseException(TOKEN_ERROR, TOKEN_ERROR_MSG);
            }
        }
    }


    private boolean verifyToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        if (token.equalsIgnoreCase(passportConf.getAuthToken())) {
            UserCacheDto cacheDto = new UserCacheDto();
            cacheDto.setRole(Role.SUPPER);
            BizContext.setData(BizContext.MONITOR_USER_CACHE, cacheDto);
            return true;
        }
        try {
            UserTokenEntity tokenEntity = UserTokenUtils.parseToken(token);
            if (tokenEntity != null) {
                BizContext.setData(BizContext.MONITOR_USER_CACHE, operatorService.getUserCache(tokenEntity.getPhone()));
                String redisToken = (String) redisUtil.get(passportConf.getTokenCacheName(), tokenEntity.getPhone() + REDIS_TOKEN_SUFFIX);
                boolean result = token.equals(redisToken);
                if (result) {
                    redisUtil.set(tokenEntity.getPhone() + REDIS_TOKEN_SUFFIX, redisToken, passportConf.getTokenExpireTime());
                }
                return result;
            }
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }
        return false;
    }

    /**
     * 异常通知
     *
     * @param jp
     * @param ex
     */
    @AfterThrowing(value = "controllerMethodPointcut()", throwing = "ex")
    @SuppressWarnings("unused")
    private void afterThrowing(JoinPoint jp, Exception ex) {
        log.error("{}{}", PREFIX_FORMAT, jp.getSignature().getName(), ex);
    }

}