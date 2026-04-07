package com.hong.diclosure.dart.domain;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DART Open API 응답 상태 코드를 나타내는 열거형.
 * DART API의 각 응답 코드에 대응하는 메시지와 HTTP 상태를 정의한다.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public enum DartApiStatus {
    /**
     * 정상 응답
     */
    SUCCESS("000", "정상", HttpStatus.OK),

    /**
     * 등록되지 않은 API 키
     */
    UNREGISTERED_KEY("010", "등록되지 않은 키입니다.", HttpStatus.UNAUTHORIZED),

    /**
     * 사용할 수 없는 API 키 (일시적으로 사용 중지된 키)
     */
    UNAVAILABLE_KEY("011", "사용할 수 없는 키입니다. 오픈API에 등록되었으나, 일시적으로 사용 중지된 키를 통하여 검색하는 경우 발생합니다.", HttpStatus.UNAUTHORIZED),

    /**
     * 접근할 수 없는 IP 주소
     */
    FORBIDDEN_IP("012", "접근할 수 없는 IP입니다.", HttpStatus.FORBIDDEN),

    /**
     * 조회된 데이터가 없음
     */
    NO_DATA("013", "조회된 데이타가 없습니다.", HttpStatus.NO_CONTENT),

    /**
     * 파일이 존재하지 않음
     */
    FILE_NOT_FOUND("014", "파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    /**
     * 요청 제한 초과 (일반적으로 20,000건 이상)
     */
    REQUEST_LIMIT_EXCEEDED("020", "요청 제한을 초과하였습니다. 일반적으로는 20,000건 이상의 요청에 대하여 이 에러 메시지가 발생되나, 요청 제한이 다르게 설정된 경우에는 이에 준하여 발생됩니다.", HttpStatus.TOO_MANY_REQUESTS),

    /**
     * 조회 가능한 회사 개수 초과 (최대 100건)
     */
    COMPANY_LIMIT_EXCEEDED("021", "조회 가능한 회사 개수가 초과하였습니다.(최대 100건)", HttpStatus.BAD_REQUEST),

    /**
     * 필드의 부적절한 값
     */
    INVALID_FIELD_VALUE("100", "필드의 부적절한 값입니다. 필드 설명에 없는 값을 사용한 경우에 발생하는 메시지입니다.", HttpStatus.BAD_REQUEST),

    /**
     * 부적절한 접근
     */
    INVALID_ACCESS("101", "부적절한 접근입니다.", HttpStatus.BAD_REQUEST),

    /**
     * 시스템 점검으로 인한 서비스 중지
     */
    SYSTEM_MAINTENANCE("800", "시스템 점검으로 인한 서비스가 중지 중입니다.", HttpStatus.SERVICE_UNAVAILABLE),

    /**
     * 정의되지 않은 오류
     */
    UNDEFINED_ERROR("900", "정의되지 않은 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * 사용자 계정의 개인정보 보유기간 만료
     */
    EXPIRED_ACCOUNT("901", "사용자 계정의 개인정보 보유기간이 만료되어 사용할 수 없는 키입니다. 관리자 이메일(opendart@fss.or.kr)로 문의하시기 바랍니다.", HttpStatus.UNAUTHORIZED);

    private static final Map<String, DartApiStatus> CODE_MAP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            DartApiStatus::getCode,
                            Function.identity()
                    ));

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    DartApiStatus(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    /**
     * API 응답 코드로부터 대응하는 DartApiStatus를 조회한다.
     * 일치하는 코드가 없는 경우 UNDEFINED_ERROR를 반환한다.
     *
     * @param code DART API 응답 코드
     * @return 대응하는 DartApiStatus
     */
    public static DartApiStatus fromCode(String code) {
        return CODE_MAP.getOrDefault(code, UNDEFINED_ERROR);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
