package com.gym.management.gymmanager;

import com.gym.management.gymmanager.logging.LoggingAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoggingAspectTest {

    @Mock
    private Logger logger;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @InjectMocks
    private LoggingAspect loggingAspect;

    private Method dummyMethod;

    @BeforeEach
    public void setUp() throws NoSuchMethodException {
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        dummyMethod = DummyClass.class.getMethod("testMethod");
        when(methodSignature.getMethod()).thenReturn(dummyMethod);
    }

    @Test
    public void testLogBeforeMethodCall() {
        String[] args = new String[]{"test", "123"};
        when(joinPoint.getArgs()).thenReturn(args);

        loggingAspect.logBeforeMethodCall(joinPoint);

        verify(logger).info("Вызов метода: {} с аргументами: {}", "testMethod", Arrays.toString(args));
    }

    @Test
    public void testLogAfterReturning() {
        Object result = "Some result";

        loggingAspect.logAfterReturning(joinPoint, result);

        verify(logger).info("Метод {} успешно завершен. Возвращено: {}", "testMethod", result);
    }

    @Test
    public void testLogAfterThrowing() {
        Throwable ex = new RuntimeException("Test exception");

        loggingAspect.logAfterThrowing(joinPoint, ex);

        verify(logger).error("Метод {} выбросил исключение: {}", "testMethod", ex.getMessage(), ex);
    }

    // Фиктивный класс с методом для рефлексии
    static class DummyClass {
        public void testMethod() {
        }
    }
}
