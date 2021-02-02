package com.virnect.serviceserver.dao;


import com.virnect.serviceserver.error.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;

@Getter
@Setter
@NoArgsConstructor
public class DataProcess<T> {
    T data;
    int code = -1;
    String message = "not assigned";

    public DataProcess(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        this.data = clazz.getDeclaredConstructor().newInstance();

    }

    @Deprecated
    private T getGenericInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return clazz.getDeclaredConstructor().newInstance();
    }

    public DataProcess(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public DataProcess(int code,  String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public DataProcess(T data) {
        this.code = ErrorCode.ERR_SUCCESS.getCode();
        this.message = ErrorCode.ERR_SUCCESS.getMessage();
        this.data = data;
    }

    public DataProcess(T data, ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = data;
    }

    public DataProcess(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = null;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /*public DataProcess<T> success(T data, int code) {
        return new DataProcess<>(data, code, null);
    }

    public DataProcess<T> fail(T data, int code, String message) {
        return new DataProcess<>(data, code, message);
    }

    public DataProcess<T> error(int code, String message) {
        return new DataProcess<>(null, code, message);
    }*/

    /*public void success(T data, int code) {
        this.data = data;
        this.code = code;
        this.message = null;
    }*/

    /*public void error(int code, String message) {
        this.data = null;
        this.code = code;
        this.message = message;
    }*/
}
