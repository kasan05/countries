package info.globe.countries.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(info.globe.countries.controller..*))")
    public void selectAllControllers() {}

    @Around("selectAllControllers()")
    public Object logAroundController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
       try{
           HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

           logRequest(request,proceedingJoinPoint);
           Object result = proceedingJoinPoint.proceed();

           HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

           logResponse(result,request,response,proceedingJoinPoint);
           return  result;
       } catch (Exception e) {
          LOGGER.error("Exception:{} occurred while processing REQUEST",e.getMessage());
          throw  e;
       }

    }

    private void logRequest(HttpServletRequest request,ProceedingJoinPoint proceedingJoinPoint){
        String method = request.getMethod();
        StringBuffer uri = request.getRequestURL();
        Map<String,String> pathVariables = (Map<String,String>) request.getAttribute(org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        LOGGER.info("REQUEST:{} for URI:{} is received with [PathVariables:{}]",method,uri,
                pathVariables!=null?pathVariables.toString():"null");
    }
    private void logResponse(Object result,HttpServletRequest request,HttpServletResponse response,ProceedingJoinPoint proceedingJoinPoint) throws ExecutionException, InterruptedException {
        Object responseBody = null;
        if (result instanceof CompletableFuture) {
            ResponseEntity<?> responseEntity =  (ResponseEntity<?>) ((CompletableFuture<?>) result).get();
             responseBody = responseEntity.getBody();
        }
        LOGGER.info("RESPONSE:{} for URI:{} is received with [status:{},responseBody:{}]",request.getMethod(),request.getRequestURL(),
                response.getStatus(),responseBody);
    }
}

