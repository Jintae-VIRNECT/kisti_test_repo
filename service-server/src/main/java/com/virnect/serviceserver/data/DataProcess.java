package com.virnect.serviceserver.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataProcess<T> {
    T data;
    int code;
    String message;

    public DataProcess(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public DataProcess<T> success(T data, int code) {
        return new DataProcess<>(data, code, null);
    }

    public DataProcess<T> fail(T data, int code, String message) {
        return new DataProcess<>(data, code, message);
    }

    public DataProcess<T> error(int code, String message) {
        return new DataProcess<>(null, code, message);
    }

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
