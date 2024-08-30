package com.shop.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestResponse implements Serializable{
    public enum Title {
        EXIT,
        REGISTRATION, SUCCESSFUL_REGISTRATION, REGISTRATION_ERROR,
        LOG_IN, SUCCESSFUL_LOG_IN, LOG_IN_ERROR,
        CREATE_PRODUCT, REMOVE_CREATED_PRODUCT,
        GET_CREATED_PRODUCTS, GET_PRODUCTS, GET_CART, GET_ORDERS,
        TOP_UP_BALANCE,
        ADD_TO_CART, REMOVE_CART_PRODUCT,
        MAKE_ORDER, MAKE_ORDER_ERROR
    }
    private Title title;
    private final Map<String, Map<Class<?>, Object>> fields = new HashMap<>();

    public RequestResponse() {
    }

    public RequestResponse(Title title) {
        this.title = title;
    }

    public void setField(String key, Object value) {
        fields.put(key, Map.of(value.getClass(), value));
    }

    public <T> T getField(Class<T> clazz, String key) {
        return clazz.cast(fields.get(key).get(clazz));
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Set<String> getKeys() {
        return fields.keySet();
    }

    @Override
    public String toString() {
        return "RequestResponse{" +
                "title=" + title +
                ", fields=" + fields +
                '}';
    }
}
