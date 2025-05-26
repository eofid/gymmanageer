package com.gym.management.gymmanager.logging;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger;

    public LoggingAspect(Logger logger) {
        this.logger = logger;
    }

    public LoggingAspect() {
        this(LoggerFactory.getLogger(LoggingAspect.class));
    }

    @Before("execution(* com.gym.management.gymmanager.service..*(..))")
    public void logBeforeMethodCall(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        logger.info("Вызов метода: {} с аргументами: {}", signature.getMethod().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.gym.management.gymmanager.service..*(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        logger.info("Метод {} успешно завершен. Возвращено: {}", signature.getMethod().getName(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.gym.management.gymmanager..*(..))",
            throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        logger.error("Метод {} выбросил исключение: {}", signature.getMethod().getName(),
                ex.getMessage(), ex);
    }
}
