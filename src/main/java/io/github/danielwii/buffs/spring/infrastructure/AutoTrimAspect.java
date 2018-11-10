package io.github.danielwii.wyf.infrastructure;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
public class AutoTrimAspect {

    @Pointcut(value = "@annotation(autoTrim) && args(joinPoint)")
    private void autoTrimPointcut(AutoTrim autoTrim, ProceedingJoinPoint joinPoint) {
    }

    @Before(value = "autoTrimPointcut(autoTrim, joinPoint)", argNames = "autoTrim,joinPoint")
    public void autoTrim(AutoTrim autoTrim, ProceedingJoinPoint joinPoint) {
         log.info("JoinPoint is [{}]", joinPoint);
    }

}
