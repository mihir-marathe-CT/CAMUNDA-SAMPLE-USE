package io.flowing.trip.saga.camunda.springboot.conf;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAdvice {
    @Around("@annotation(io.flowing.trip.saga.camunda.springboot.conf.TrackExecutionTime)")
    public Object executionTime(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object object = point.proceed();
        long endTime = System.currentTimeMillis();

        log.error("Class Name: " + point.getSignature().getDeclaringTypeName()
            + ". Method Name: " + point.getSignature().getName()
            + ". Time taken for Execution is : " + (endTime - startTime) + "ms");
        return object;
    }
}
