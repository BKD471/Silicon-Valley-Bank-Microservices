package com.example.loansservices.aspect;

import com.example.loansservices.dto.Dto;
import com.example.loansservices.dto.LoansDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;


import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

@Aspect
@Component
@Slf4j
public class LoggerAspect {

    @Around(value = "execution(com.example.loansservices.dto.Dto  com.example.loansservices.service.*.*(..))")
    public Dto log(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("<---------------------------------->"+joinPoint.
                getSignature().
                toString() + " method executions starts---------------------------------------------------------------->");
        Instant start = Instant.now();
        Object result = joinPoint.proceed();
        Instant end = Instant.now();
        long timeElapsedInMs = Duration.between(start, end).toMillis();
        log.info(String.format("<-----------------Time elapsed to execute %s in Ms is %s------------------------------------->", joinPoint.getSignature().toString(), timeElapsedInMs));
        log.info("<--------------------------------->"+joinPoint.getSignature().toString() + "method execution stops------------------------------------>");
        return  (Dto)result;
    }

    @Around(value = "execution(java.util.List com.example.loansservices.service.*.*(..))")
    public List<Dto> logForMethodsWithReturnTypeList(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("<---------------------------------->"+joinPoint.
                getSignature().
                toString() + " method executions starts---------------------------------------------------------------->");
        log.info("<------------------------Preparing the list---------------------------------------------------------->");
        Instant start = Instant.now();
        Object result = joinPoint.proceed();
        Instant end = Instant.now();
        long timeElapsedInMs = Duration.between(start, end).toMillis();
        log.info(String.format("<-----------------Time elapsed to execute %s in Ms is %s------------------------------------->", joinPoint.getSignature().toString(), timeElapsedInMs));
        log.info("<--------------------------------->"+joinPoint.getSignature().toString() + "method execution stops------------------------------------>");
        return (List<Dto>) result;
    }
    @AfterThrowing(value = "execution(* com.example.loansservices.service.*.*(..))", throwing = "e")
    public void logException(JoinPoint joinPoint, Exception e) {
        log.error("<-----------------------------------------" + e.getMessage() + " from ------------------------------->"
                + joinPoint.getSignature().toString());
    }
}
