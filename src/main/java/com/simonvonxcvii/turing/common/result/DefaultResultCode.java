package com.simonvonxcvii.turing.common.result;

record DefaultResultCode(String code, String message, Boolean isSuccess) implements ResultCode {
}
