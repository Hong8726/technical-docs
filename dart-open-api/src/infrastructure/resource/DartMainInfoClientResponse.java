package com.hong.diclosure.dart.infrastructure.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hong.diclosure.dart.domain.DartMainInfo;
import com.hong.diclosure.dart.domain.MainInfoType;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * DART API 공통 List 형태 응답 모델.
 * 정기보고서 주요정보 API들의 공통 응답 형식.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public record DartMainInfoClientResponse(
        @JsonProperty("status")
        String status,
        @JsonProperty("message")
        String message,
        @JsonProperty("list")
        List<LinkedHashMap<String, String>> list
) {
    public static DartMainInfo toDomain(String corporationCode, String receiptNumber, MainInfoType type, List<LinkedHashMap<String, String>> rawData) {
        return DartMainInfo.createFromResponse(corporationCode, receiptNumber, type, rawData);
    }
}
