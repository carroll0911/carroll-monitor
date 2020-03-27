package com.carroll.monitor.data.collector.annotation;

import com.carroll.monitor.common.utils.ParseUtils;
import com.carroll.monitor.data.collector.component.DataSender;
import com.carroll.monitor.data.collector.component.SendFlagHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 业务监控注解AOP
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class MonitorAop {

    @Autowired
    private DataSender sender;

    /**
     * 定义Aop切点，拦截Monitor注解的方法。
     */
    @Pointcut("@annotation(com.carroll.monitor.data.collector.annotation.Monitor)")
    public void pointcut() {
        //切入点
    }

    /**
     * 环绕通知
     *
     * @param joinPoint
     * @return 拦截方法执行结果
     */
    @SuppressWarnings("unused")
    @Around("pointcut()")
    private Object aroundInterceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = parseMethodName(method);
        SendFlagHolder.setStartTime(methodName, System.currentTimeMillis());
        Monitor monitor = method.getAnnotation(Monitor.class);
        SendFlagHolder.addCount(monitor.tag());
        Object result = null;
        Object[] params = joinPoint.getArgs();
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            send(monitor.tag(), false, params, String.format("%s:%s", e.getClass().getName(), e.getMessage()), parseTarget(monitor.target(), method, params), monitor.timeoutMs(), SendFlagHolder.getUsedTime(methodName), methodName);
            SendFlagHolder.reduceCount(monitor.tag());
            throw e;
        }
        analize(monitor, result, method, params, result, parseTarget(monitor.target(), method, params), SendFlagHolder.getUsedTime(methodName), methodName);
        SendFlagHolder.reduceCount(monitor.tag());
        return result;
    }

    private void analize(Monitor monitor, Object result, Method method, Object[] params, Object response, String target, Long useTimeMs, String joinpoint) {
        Class returnClazz = method.getReturnType();
        String field = monitor.field();
        if (Void.TYPE.equals(returnClazz)) {
            send(monitor.tag(), true, target, monitor.timeoutMs(), useTimeMs, joinpoint);
            return;
        }
        if (null == result) {
            send(monitor.tag(), StringUtils.isEmpty(field), params, response, target, monitor.timeoutMs(), useTimeMs, joinpoint);
            return;
        }
        if (result.equals(true)) {
            send(monitor.tag(), true, target, monitor.timeoutMs(), useTimeMs, joinpoint);
            return;
        }
        if (result.equals(false)) {
            send(monitor.tag(), false, params, response, target, monitor.timeoutMs(), useTimeMs, joinpoint);
            return;
        }
        Double temp = ParseUtils.parseDouble(result);
        if (temp != null) {
            send(monitor.tag(), temp > 0, params, response, target, monitor.timeoutMs(), useTimeMs, joinpoint);
            return;
        }
        if (StringUtils.isEmpty(field)) {
            send(monitor.tag(), true, target, monitor.timeoutMs(), useTimeMs, joinpoint);
            return;
        }
        Map resultMap = ParseUtils.parseObj2Map(result);
        Object flag = resultMap.get(monitor.field());
        if (flag == null) {
            log.info("操作结果中不存在{},或字段为空", field);
            return;
        }

        if (flag.equals(true)) {
            send(monitor.tag(), true, target, monitor.timeoutMs(), useTimeMs, joinpoint);
            return;
        }
        if (flag.equals(false)) {
            send(monitor.tag(), false, params, response, target, monitor.timeoutMs(), useTimeMs, joinpoint);
            return;
        }
        temp = ParseUtils.parseDouble(flag);
        if (temp == null) {
            log.info("操作结果中{}字段只能为布尔值，数字或数字字符串", field);
            return;
        } else {
            send(monitor.tag(), temp > 0, params, response, target, monitor.timeoutMs(), useTimeMs, joinpoint);
            return;
        }
    }

    private void send(String tag, Boolean success, String target, Long timeoutMs, Long useTimeMs, String joinpoint) {
        this.send(tag, success, null, null, target, timeoutMs, useTimeMs, joinpoint);
    }

    private void send(String tag, Boolean success, Object[] params, Object response, String target, Long timeoutMs, Long useTimeMs, String joinpoint) {

        log.debug("send monitor data:{}---{}", tag, success);
        if (success != null) {
            sender.send(tag, success, params, response, target, true, useTimeMs, timeoutMs, joinpoint);
        }
    }

    /**
     * 获取target
     * target 定义在注解上，支持SPEL表达式
     *
     * @return
     */
    private String parseTarget(String target, Method method, Object[] args) {
        if (StringUtils.isEmpty(target)) {
            return null;
        }
        try {
            //获取被拦截方法参数名列表(使用Spring支持类库)
            LocalVariableTableParameterNameDiscoverer u =
                    new LocalVariableTableParameterNameDiscoverer();
            String[] paraNameArr = u.getParameterNames(method);

            //使用SPEL进行key的解析
            ExpressionParser parser = new SpelExpressionParser();
            //SPEL上下文
            StandardEvaluationContext context = new StandardEvaluationContext();
            //把方法参数放入SPEL上下文中
            for (int i = 0; i < paraNameArr.length; i++) {
                context.setVariable(paraNameArr[i], args[i]);
            }
            return parser.parseExpression(target).getValue(context, String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    private String parseMethodName(Method method) {
        String className = method.getDeclaringClass().getName();
        StringBuilder resb = new StringBuilder(className);
        Parameter[] params = method.getParameters();
        for (Parameter parameter : params) {
            resb.append("#").append(parameter.getType().getName());
        }
        return resb.toString();
    }
}
