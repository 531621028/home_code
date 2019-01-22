package com.hk.exception;

import com.hk.common.constants.GlobalConstants;

/**
 * Created by kang on 2019/1/8.
 */
public class ClientException extends RuntimeException{
    private int code;

    public ClientException(String message) {
        super(message);
        this.code = GlobalConstants.ErrorCode.APP_DEFAULT_CODE;
    }
    public ClientException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
