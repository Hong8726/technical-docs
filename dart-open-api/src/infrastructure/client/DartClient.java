package com.hong.diclosure.dart.infrastructure.client;

import com.hong.diclosure.dart.infrastructure.config.DartApiProperties;
import com.hong.diclosure.dart.infrastructure.exception.DartApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * DART API 클라이언트의 추상 클래스.
 * 공통 의존성 및 기능을 제공한다.
 *
 * @param <T> 각 클라이언트가 사용하는 요청 타입
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public abstract class DartClient<T> {

    // 파라미터 상수
    protected static final String CERT_KEY = "crtfc_key";
    protected static final String CORP_CODE = "corp_code";
    protected static final String RECEIPT_NO = "rcept_no";

    // API 상태 코드
    protected static final String STATUS_SUCCESS = "000";
    protected static final String STATUS_ERROR_DEFAULT = "900";

    protected final Logger log;
    protected final RestTemplate restTemplate;
    protected final DartApiProperties properties;

    protected DartClient(RestTemplate restTemplate, DartApiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.log = LoggerFactory.getLogger(getClass());
    }

    /**
     * 공통 파라미터(API 인증키)를 추가한다.
     *
     * @param params 파라미터 맵
     */
    protected void addCertificationKey(MultiValueMap<String, String> params) {
        params.add(CERT_KEY, properties.getApiKey());
    }

    /**
     * DART API를 호출하고 응답을 반환한다.
     * 응답이 null인 경우 DartApiException을 발생시킨다.
     *
     * @param uri          URI 경로
     * @param responseType 응답 타입 클래스
     * @param <R>          응답 타입
     * @return API 응답 객체
     * @throws DartApiException 응답이 null인 경우
     */
    protected <R> R callDartApi(String uri, Class<R> responseType) {
        return Optional.ofNullable(
                restTemplate.getForObject(uri, responseType)
        ).orElseThrow(() -> new DartApiException(STATUS_ERROR_DEFAULT));
    }

    /**
     * DART API 응답 상태를 검증한다.
     * 성공 상태가 아닌 경우 DartApiException을 발생시킨다.
     *
     * @param status API 응답 상태 코드
     * @throws DartApiException 상태 코드가 성공이 아닌 경우
     */
    protected void validateResponseStatus(String status) {
        String statusCode = Optional.ofNullable(status).orElse(STATUS_ERROR_DEFAULT);
        if (STATUS_SUCCESS.equals(statusCode)) {
            return;
        }
        throw new DartApiException(statusCode);
    }

    /**
     * 요청 객체를 쿼리 파라미터로 변환한다.
     * 각 클라이언트는 이 메서드를 오버라이드하여 자신의 요청 타입에 맞게 구현한다.
     *
     * @param request 요청 객체
     * @return 쿼리 파라미터 맵
     */
    protected abstract MultiValueMap<String, String> buildQueryParameters(T request);
}
