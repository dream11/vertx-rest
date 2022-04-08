package com.dream11.rest.app.error;

import com.dream11.rest.exception.RestError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum RestAppError implements RestError {
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION", "Something went wrong", 500),
    UNSUPPORTED_REQUEST_FORMAT("UNSUPPORTED_REQUEST_FORMAT", "The request body could not be processed", 400),
    AEROSPIKE_NOT_CONNECTED("AEROSPIKE_NOT_CONNECTED", "Aerospike server is not connected", 503);

    final String errorCode;
    final String errorMessage;
    final int httpStatusCode;
}
