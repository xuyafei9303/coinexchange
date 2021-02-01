package com.ixyf.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.ixyf.model.WebLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;

@Component
@Order(1)
@Slf4j
@Aspect
public class WebLogAspect {

    /**
     * 日志记录
     * 环绕通知 ： 方法执行前后
     */
    @Pointcut("execution(* com.ixyf.controller.*.*(..))") // controller里面所有类所有方法都有该切面
    public void webLog() {};

    /**
     * 记录日志的环绕通知
     */
    @Around("webLog()")
    public Object recodeWebLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        final WebLog webLog = new WebLog();
        long start = System.currentTimeMillis();
        // 执行方法调用
        result = joinPoint.proceed(joinPoint.getArgs());

        long end = System.currentTimeMillis();
        webLog.setSpendTime((int)(end - start) / 1000); // 接口请求花费的时间
        // 获取当前请求的request对象
        final ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        final HttpServletRequest request = servletRequestAttributes.getRequest();
        // 获取安全的上下文
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String url = request.getRequestURL().toString();
        webLog.setUri(request.getRequestURI());
        webLog.setUrl(url);
        webLog.setIp(request.getRemoteAddr()); // TODO 获取IP地址
        webLog.setBasePath(StrUtil.removeSuffix(url, URLUtil.url(url).getPath())); // http://ip:port
        webLog.setUsername(authentication == null ? "anonymous" : authentication.getPrincipal().toString()); // 获取用户id 如果没有用户则默认匿名用户
        // 一些简单的描述 适用于swagger
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取类的名称
        final String clazzName = joinPoint.getTarget().getClass().getName();
        final Method method = signature.getMethod();
        // 获取@ApiOperation
        final ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        webLog.setDescription(apiOperation == null ? "no desc" : apiOperation.value());
        webLog.setMethod(clazzName + "." + method.getName() + "()"); // com.ixyf.controller.LoginController.login()
        webLog.setParameter(getMethodParameters(method, joinPoint.getArgs()));
        webLog.setResult(result);
        log.info(JSON.toJSONString(webLog, true));

        return result;
    }

    /**
     * 获取方法的执行参数
     * @param method
     * @param args
     * @return {key:参数名，value:参数值}
     */
    private Object getMethodParameters(Method method, Object[] args) {
        final HashMap<String, Object> methodsParameterWithValue = new HashMap<>();
        final LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        // 方法的形参名称
        final String[] parameterNames = localVariableTableParameterNameDiscoverer.getParameterNames(method);
        for (int i = 0; i < Objects.requireNonNull(parameterNames).length; i++) {
            if (parameterNames[i].equals("password") || parameterNames[i].equals("file")) {
                methodsParameterWithValue.put(parameterNames[i], "受限的支持类型");
            } else {
                methodsParameterWithValue.put(parameterNames[i], args[i]);
            }
        }
        return methodsParameterWithValue;
    }


}
