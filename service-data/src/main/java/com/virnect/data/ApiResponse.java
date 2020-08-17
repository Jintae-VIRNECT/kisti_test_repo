package com.virnect.data;

import com.virnect.data.error.ErrorCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    @ApiModelProperty(value = "API 응답 데이터를 갖고 있는 객체.", dataType = "object")
    T data;
    ///Map<String, Object> data = new HashMap<>();
    @ApiModelProperty(value = "API 처리 결과 상태 코드 값, 200이면 정상 처리 완료.", dataType = "int", example = "200")
    int code = 200;
    @ApiModelProperty(value = "API 처리 결과에 대한 메시지", dataType = "string", example = "success")
    String message = "complete";

    public ApiResponse(T data) {
        this.data = data;
    }

    public void setErrorResponseData(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public void setErrorResponseData(T data, int code) {
        this.data = data;
        this.code = code;
    }

    public void setErrorResponseData(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public void setErrorResponseData(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void setErrorResponseData(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = null;
    }

    /*@Override
    public String toString() {
        return String.format("ApiResponse{data=%s, code=%d, message='%s'}", data, code, message);
    }*/
}
