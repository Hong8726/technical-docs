package com.hong.diclosure.dart.infrastructure.exception;

import com.hong.diclosure.dart.domain.DartApiStatus;
import org.springframework.http.HttpStatus;

/**
 * DART Open API 호출 시 발생하는 예외.
 * API 응답 코드에 따라 적절한 메시지와 HTTP 상태를 설정한다.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public class DartApiException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;
    private final DartApiStatus dartApiStatus;

    /**
     * API 응답 코드로부터 DartApiException을 생성한다.
     *
     * @param apiStatus DART API 응답 코드
     */
    public DartApiException(String apiStatus) {
        this.dartApiStatus = DartApiStatus.fromCode(apiStatus);
        this.status = dartApiStatus.getHttpStatus();
        this.errorCode = dartApiStatus.getCode();
    }

    @Override
    public String getMessage() {
        return dartApiStatus.getMessage();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public DartApiStatus getDartApiStatus() {
        return dartApiStatus;
    }
}

