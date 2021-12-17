package com.lq.plugin.init;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class TypeToken<T> {

    private Type type;

    protected TypeToken() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) genericSuperclass;
        type = parameterized.getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }

    public static void main(String[] args) {

        ParameterizedType type = (ParameterizedType)new TypeToken<List<DeepLinkClassInfo>>() {
        }.getType();

        System.out.println(type);
        System.out.println(type.getActualTypeArguments()[0]);
    }
}
