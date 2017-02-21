package com.vocalabs.egtest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.LOCAL_VARIABLE})
public @interface EgMatchesContainer {
    EgMatches[] value();
}