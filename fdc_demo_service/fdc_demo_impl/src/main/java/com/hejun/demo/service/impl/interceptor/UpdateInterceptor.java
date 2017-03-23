package com.hejun.demo.service.impl.interceptor;

import com.fdc.platform.common.validator.annotation.Domain;
import org.apache.commons.beanutils.MethodUtils;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class UpdateInterceptor {

    public void before(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            String createName = "test";
            Class cla = arg.getClass();
            if (cla.isAnnotationPresent(Domain.class)) {
                try {
                    if (MethodUtils.invokeMethod(arg, "getGmtModified", null) == null) {
                        MethodUtils.invokeMethod(arg, "setGmtModified", new Date());
                    }
                    if (MethodUtils.invokeMethod(arg, "getModifiedBy", null) == null) {
                        MethodUtils.invokeMethod(arg, "setModifiedBy", createName);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
