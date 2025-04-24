package com.gym.management.gymmanager.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 📌 Логирование всех вызовов методов в сервисах
    @Before("execution(* com.gym.management.gymmanager.service..*(..))")
    public void logBeforeMethodCall(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        logger.info("Вызов метода: {} с аргументами: {}", signature.getMethod().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    // 📌 Логирование успешных возвращаемых значений
    @AfterReturning(pointcut = "execution(* com.gym.management.gymmanager.service..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Метод {} успешно завершен. Возвращено: {}", joinPoint.getSignature().getName(), result);
    }

    // ⚠️ Логирование исключений
    @AfterThrowing(pointcut = "execution(* com.gym.management.gymmanager..*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.error("Метод {} выбросил исключение: {}", joinPoint.getSignature().getName(), ex.getMessage(), ex);
    }
}
