package io.github.danielwii.buffs.spring.jooq;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class RecordExistsAspect {

    @Autowired(required = false)
    private List<IRecordRepository> repositories = Collections.emptyList();

    @Pointcut(value = "@annotation(checkpoint) && args(arg,..)")
    private void softDeleteCheckpointPointcut(SoftDeleteCheckpoint checkpoint, Object arg) {
        //
    }

    @Before(value = "softDeleteCheckpointPointcut(checkpoint, arg)", argNames = "joinPoint,checkpoint,arg")
    public void softDeleteCheckpoint(JoinPoint joinPoint, SoftDeleteCheckpoint checkpoint, Object arg) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Optional<IRecordRepository> optional = repositories.stream().filter(repository -> {
            Type[] genericTypes = ((ParameterizedType) repository.getClass()
                .getSuperclass()
                .getGenericInterfaces()[0]).getActualTypeArguments();
            return genericTypes[1].getTypeName().equals(checkpoint.record().getTypeName());
        }).findAny();

        if (optional.isPresent()) {
            Long id;
            if (arg instanceof Long) {
                id = (Long) arg;
            } else {
                id = (Long) arg.getClass().getMethod("getId").invoke(arg);
            }

            if (id != null) {
                log.info("softDeleteCheckpoint-existSelector: {} - {}", arg, id);
                optional.get().verifyExistSelector(id);
            } else {
                log.info("softDeleteCheckpoint: id is null");
            }
        }
    }

}
