package com.hong.diclosure.dart.presentation.resource;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import com.hong.diclosure.dart.domain.DartMainInfo;
import com.hong.diclosure.dart.domain.MainInfoType;

/**
 * DART 정기보고서 주요정보 조회 요청.
 * 증자/감자 현황, 배당 현황, 최대주주 현황 등 정기보고서 내 주요정보 조회 시 공통으로 사용된다.
 * 유효성 검증은 Domain 레벨에서 수행된다.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public record DartMainInfoRequest(

        @Parameter(description = "고유번호 8자리", example = "00688996", required = true, in = ParameterIn.HEADER)
        String corpCode,

        @Schema(description = "사업연도 4자리", example = "2024", requiredMode = Schema.RequiredMode.REQUIRED)
        String businessYear,

        @Schema(description = "보고서 코드", example = "11011", requiredMode = Schema.RequiredMode.REQUIRED)
        String reportCode,

        @Schema(description = "접수번호", example = "20250526000374", requiredMode = Schema.RequiredMode.REQUIRED)
        String receiptNumber
) {

    /**
     * Presentation Request를 Domain 객체로 변환한다.
     * Controller에서 엔드포인트별로 type을 지정하여 호출한다.
     */
    public DartMainInfo toDomain(String corpCode, MainInfoType type) {
        return DartMainInfo.createForRequest(corpCode, businessYear, reportCode, receiptNumber, type);
    }
}
