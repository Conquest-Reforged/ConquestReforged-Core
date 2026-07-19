package com.conquestrefabricated.core.asset.annotation;



import com.conquestrefabricated.core.block.builder.SpecialOffsetType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SpecialOffset {

    SpecialOffsetType offsetType();
}
