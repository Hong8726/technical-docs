package com.hong.diclosure.dart.presentation.resource;

import com.hong.diclosure.dart.domain.DartMainInfo;

/**
 * DART 정기보고서 주요정보 조회 응답.
 * Controller에서 클라이언트에게 반환하는 DTO.
 *
 * @param rceptNo 접수번호
 * @param corpCode 고유번호
 * @param contents HTML 테이블 형태의 조회 내용
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public record DartMainInfoResponse(
        String rceptNo,
        String corpCode,
        String contents
) {
    /**
     * Domain Result를 Presentation Response로 변환한다.
     * 도메인 엔티티 리스트를 HTML 테이블 형태로 변환한다.
     */
    public static DartMainInfoResponse fromDomain(DartMainInfo result) {

        return new DartMainInfoResponse(
                result.getReceiptNumber(),
                result.getCorporationCode(),
                result.getContents()
        );
    }
}
