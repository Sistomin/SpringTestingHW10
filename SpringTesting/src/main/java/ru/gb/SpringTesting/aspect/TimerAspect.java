package ru.gb.SpringTesting.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(2) // устанавливаем приоритет для данного аспекта равным 2 (выполнение после аспекта с номером 1)
@Component
@Slf4j
public class TimerAspect {

    // Для обработки методов с указанной аннотацией
    @Pointcut("@annotation(ru.gb.springdemo.aspect.Timer)")
    public void methodLoggingPointcut() {
    }

    // Для обработки бинов (классов) с указанной аннотацией
    @Pointcut("within(@ru.gb.springdemo.aspect.Timer *)")
    public void classLoggingPointcut() {
    }

    @Around("methodLoggingPointcut() || classLoggingPointcut()")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;

        log.info("Method: {} - {} # {} millis", joinPoint.getTarget().getClass().getName(),
                joinPoint.getSignature().getName(), elapsedTime);

        return result;
    }

}
