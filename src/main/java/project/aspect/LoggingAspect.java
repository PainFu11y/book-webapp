package project.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.epam.rd.autocode.spring.project.service..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info("Entering method: {} with arguments: {}", method, args);
    }


    @AfterReturning(pointcut = "execution(* com.epam.rd.autocode.spring.project.service..*(..))", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String method = joinPoint.getSignature().toShortString();
        log.info("Exiting method: {} with result: {}", method, result);
    }


    @AfterThrowing(pointcut = "execution(* com.epam.rd.autocode.spring.project.service..*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();
        log.error("Exception in method: {} with message: {}", method, ex.getMessage());
    }
}