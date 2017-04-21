package com.hejun.demo.service.inter.validator.annotation;

import java.lang.annotation.*;

/**
 * Created by hejun-FDC on 2017/4/21.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizId {
}
