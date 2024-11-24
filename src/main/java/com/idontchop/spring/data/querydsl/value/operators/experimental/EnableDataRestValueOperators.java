package com.idontchop.spring.data.querydsl.value.operators.experimental;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.springframework.context.annotation.Import;

import com.idontchop.spring.data.querydsl.value.operators.experimental.config.ValueOperatorConfiguration;

import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ValueOperatorConfiguration.class)
public @interface EnableDataRestValueOperators {
    
}
