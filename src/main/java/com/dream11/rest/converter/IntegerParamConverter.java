package com.dream11.rest.converter;

import jakarta.ws.rs.ext.ParamConverter;

import java.lang.annotation.Annotation;

public class IntegerParamConverter extends BaseParamConverter implements ParamConverter<Integer> {

    public IntegerParamConverter(Annotation[] annotations) {
        super(annotations);
    }

    @Override
    public Integer fromString(String s) {
        return this.parseParam(s, Integer::parseInt);
    }

    @Override
    public String toString(Integer i) {
        return i.toString();
    }
}
