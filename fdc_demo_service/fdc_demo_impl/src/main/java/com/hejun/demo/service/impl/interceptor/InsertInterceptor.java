package com.hejun.demo.service.impl.interceptor;

import com.fdc.platform.common.yfid.GenIdService;
import com.hejun.demo.service.inter.validator.annotation.BizId;
import com.hejun.demo.service.inter.validator.annotation.Domain;
import com.hejun.demo.service.inter.validator.annotation.Nofixed;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class InsertInterceptor {

    private final static GenIdService genIdService = new GenIdService();

    public void before(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            String createName = "android";
            Class cla = arg.getClass();
            if (cla.isAnnotationPresent(Domain.class)) {
                try {
                    if (!cla.isAnnotationPresent(Nofixed.class)) {
                        if (MethodUtils.invokeMethod(arg, "getGmtCreated", null) == null) {
                            MethodUtils.invokeMethod(arg, "setGmtCreated", new Date());
                        }
                        if (MethodUtils.invokeMethod(arg, "getGmtModified", null) == null) {
                            MethodUtils.invokeMethod(arg, "setGmtModified", new Date());
                        }
                        if (MethodUtils.invokeMethod(arg, "getCreatedBy", null) == null) {
                            MethodUtils.invokeMethod(arg, "setCreatedBy", createName);
                        }
                        if (MethodUtils.invokeMethod(arg, "getModifiedBy", null) == null) {
                            MethodUtils.invokeMethod(arg, "setModifiedBy", createName);
                        }
                    }
                    Field[] fields = cla.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(BizId.class)) {
                            String name = field.getName();
                            String getMethodName = "get" + StringUtils.capitalize(name);
                            if (StringUtils.isEmpty((String) MethodUtils.invokeMethod(arg, getMethodName, null))) {
                                String setMethodName = "set" + StringUtils.capitalize(name);
                                String id = genIdService.nextStrKeyId();
                                MethodUtils.invokeMethod(arg, setMethodName, id);
                            }
                        }
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

