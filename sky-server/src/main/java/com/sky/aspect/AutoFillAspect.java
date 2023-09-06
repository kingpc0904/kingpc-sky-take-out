package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MemberSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类，实现公共字段填充逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut(){}

    /**
     * 前置通知，在通知中进行公共字段的填充
     */
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段的填充...");

        //获取到当前被拦截的方法上的数据库操作类型
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();//获取方法签名对象
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解
        OperationType operationType = autoFill.value();//获取数据库操作类型

        //获取到当前被拦截的方法的参数-实体参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }

        Object entity = args[0];
        //准备要填充的数据
        LocalDateTime now = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();

        //根据不同的数据库操作类型，为对应的属性通过反射赋值
        if (operationType == OperationType.INSERT){
            try {
                //为四个公共字段赋值
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
                setCreateUser.invoke(entity,id);
                setUpdateUser.invoke(entity,id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (operationType == OperationType.UPDATE){
            Method setUpdateTime = null;
            try {
                //为两个公共字段赋值
                setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}