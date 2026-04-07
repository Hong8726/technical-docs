package com.hong.disclosure.user.support;

import com.hong.disclosure.user.infrastructure.EventType;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Emittable {
    String routingKey() default "";

    EventType type();
}