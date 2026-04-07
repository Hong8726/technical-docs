package com.hong.diclosure.dart.infrastructure.resource;

import com.hong.diclosure.dart.domain.DartMainInfo;

/**
 * DART 정기보고서 주요정보 조회 명령 객체.
 * Application Layer에서 사용하는 도메인 객체로, 유효성 검증 로직을 포함한다.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public record DartMainInfoClientRequest(
        String corpCode,
        String businessYear,
        String reportCode
) {

    public static DartMainInfoClientRequest from(DartMainInfo domain) {
        return new DartMainInfoClientRequest(
                domain.getCorporationCode(),
                domain.getBusinessYear(),
                domain.getReportCode().getCode()
        );
    }
}
