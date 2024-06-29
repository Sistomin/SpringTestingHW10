package ru.gb.SpringTesting.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(1) // устанавливаем приоритет для данного аспекта равным 1 (выполнение до всех остальных аспектов)
@Component
@Slf4j
public class ExceptionAspect {

    // Для обработки методов с указанной аннотацией
    @Pointcut("@annotation(ru.gb.springdemo.aspect.RecoverException)")
    public void exceptionPointcut() {
    }

    @Around("exceptionPointcut()")
    public Object exceptionHandler(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = null;
        Class<? extends RuntimeException>[] notSkippedExceptions = extractExceptions(joinPoint);

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable exception) {

            for (Class<? extends RuntimeException> exceptionClass : notSkippedExceptions) {
                if (exceptionClass.isAssignableFrom(exception.getClass())) {
                    throw exception;
                }
            }

            return result;
        }
    }

    // Вспомогательный метод для извлечения из аннотации значений параметра
    private Class<? extends RuntimeException>[] extractExceptions(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        RecoverException annotation = methodSignature.getMethod().getAnnotation(RecoverException.class);
        return annotation.noRecoverFor();
    }

}
