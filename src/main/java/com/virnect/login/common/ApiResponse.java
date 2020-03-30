package com.virnect.login.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Login
 * @email practice1356@gmail.com
 * @description Api Response Message Wrapper Class
 * @since 2020.03.16
 */
@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    T data;
    int code = 200;
    String message = "complete";
    public ApiResponse(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "data=" + data +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}

