package com.hong.diclosure.dart.presentation.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.hong.diclosure.presentation.exception.ControllerExceptionResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DART API 공통 응답 스펙을 정의하는 커스텀 어노테이션.
 * 모든 DART API 엔드포인트에서 공통으로 발생할 수 있는 HTTP 응답 코드와 오류 상황을 정의합니다.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "204", description = "데이터 없음 (err_code: 013)"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (err_code: 021, 100)",
                content = @Content(schema = @Schema(implementation = ControllerExceptionResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패 (err_code: 010)",
                content = @Content(schema = @Schema(implementation = ControllerExceptionResponse.class))),
        @ApiResponse(responseCode = "403", description = "접근 권한 없음 또는 사용 불가 키 (err_code: 011, 012, 101, 901)",
                content = @Content(schema = @Schema(implementation = ControllerExceptionResponse.class))),
        @ApiResponse(responseCode = "404", description = "대상 데이터를 찾을 수 없음 (err_code: 014)",
                content = @Content(schema = @Schema(implementation = ControllerExceptionResponse.class))),
        @ApiResponse(responseCode = "429", description = "요청 한도 초과 (err_code: 020)",
                content = @Content(schema = @Schema(implementation = ControllerExceptionResponse.class))),
        @ApiResponse(responseCode = "502", description = "외부 연동 실패 (err_code: 900, 기타 미정의 오류)",
                content = @Content(schema = @Schema(implementation = ControllerExceptionResponse.class))),
        @ApiResponse(responseCode = "503", description = "외부 서비스 점검 중 (err_code: 800)",
                content = @Content(schema = @Schema(implementation = ControllerExceptionResponse.class)))
})
public @interface DartApiResponses {
}
