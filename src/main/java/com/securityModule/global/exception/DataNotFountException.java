package com.securityModule.global.exception;

import com.securityModule.data.enums.ResponseCode;

public class DataNotFountException extends BusinessException {

    public DataNotFountException() {
        super(ResponseCode.DATA_NOT_FOUND);
    }

    public DataNotFountException(String msg) {
        super(ResponseCode.DATA_NOT_FOUND.getCode(), msg);
    }
}
