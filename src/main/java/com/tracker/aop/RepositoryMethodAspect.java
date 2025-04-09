package com.tracker.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class RepositoryMethodAspect {

    private static final ThreadLocal<MethodCallInfo> CURRENT_METHOD = new ThreadLocal<>();

    @Pointcut("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
    public void jpaRepositoryMethod() {}

    @Around("jpaRepositoryMethod()")
    public Object trackRepositoryMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            saveMethodInfo(joinPoint);
            return joinPoint.proceed();
        } finally {
            CURRENT_METHOD.remove();
        }
    }

    public static MethodCallInfo getCurrentMethod() {
        return CURRENT_METHOD.get();
    }

    private void saveMethodInfo(ProceedingJoinPoint joinPoint) {
        Class<?> repositoryClass = joinPoint.getTarget().getClass();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        Class<?>[] interfaces = repositoryClass.getInterfaces();
        String actualRepositoryClassName = repositoryClass.getName();
        for (Class<?> iface : interfaces) {
            if (iface.getName().contains("JpaRepository")) {
                actualRepositoryClassName = iface.getName();
                break;
            }
        }

        MethodCallInfo methodCallInfo = new MethodCallInfo(
                actualRepositoryClassName,
                method.getName(),
                args
        );

        CURRENT_METHOD.set(methodCallInfo);
    }
}
