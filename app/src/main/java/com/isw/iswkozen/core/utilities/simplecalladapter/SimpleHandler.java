package com.isw.iswkozen.core.utilities.simplecalladapter;

public interface SimpleHandler<T> {
    void accept(T response, Throwable throwable);
}