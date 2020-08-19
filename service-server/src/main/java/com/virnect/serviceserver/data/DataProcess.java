package com.virnect.serviceserver.data;

import com.virnect.data.error.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataProcess<T> {
    T data;
    int code = 200;
    String message = "complete";

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
