package com.hong.diclosure.dart.infrastructure.client;

import com.hong.diclosure.dart.domain.DartMainInfo;
import com.hong.diclosure.dart.domain.MainInfoType;
import com.hong.diclosure.dart.infrastructure.config.DartApiProperties;
import com.hong.diclosure.dart.infrastructure.resource.DartMainInfoClientRequest;
import com.hong.diclosure.dart.infrastructure.resource.DartMainInfoClientResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * DART 정기보고서 주요정보 API 클라이언트.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
@Component
public class DartMainInfoClient extends DartClient<DartMainInfoClientRequest> {

    public DartMainInfoClient(RestTemplate dartRestTemplate,
                              DartApiProperties properties) {
        super(dartRestTemplate, properties);
    }

    /**
     * DART 정기보고서 주요정보를 조회하고 도메인 결과를 반환한다.
     * type에 따라 적절한 엔드포인트를 호출한다.
     *
     * @param domain 도메인
     * @return 도메인 결과
     */
    public DartMainInfo fetchMainInfo(DartMainInfo domain) {
        MultiValueMap<String, String> params = buildQueryParameters(DartMainInfoClientRequest.from(domain));
        String uri = UriComponentsBuilder.fromPath(domain.getType().getEndpoint())
                .queryParams(params)
                .build()
                .toUriString();

        log.debug("[fetchMainInfo] type={} uri={}", domain.getType(), uri);

        DartMainInfoClientResponse response = callDartApi(uri, DartMainInfoClientResponse.class);
        validateResponseStatus(response.status());

        return convertToDomain(response, domain.getType());
    }

    /**
     * Infrastructure Response를 Domain Result로 변환한다.
     */
    private DartMainInfo convertToDomain(DartMainInfoClientResponse response, MainInfoType type) {
        // 첫 번째 row에서 메타데이터 추출
        LinkedHashMap<String, String> firstRow = response.list().getFirst();
        String rceptNo = firstRow.getOrDefault(RECEIPT_NO, "");
        String corpCode = firstRow.getOrDefault(CORP_CODE, "");

        // 메타 필드 제외하고 필터링 (rcept_no, corp_cls, corp_code, corp_name 제외)
        List<LinkedHashMap<String, String>> filteredList = response.list().stream()
                .map(row -> {
                    LinkedHashMap<String, String> filteredRow = new LinkedHashMap<>(row);
                    filteredRow.remove("rcept_no");
                    filteredRow.remove("corp_cls");
                    filteredRow.remove("corp_code");
                    filteredRow.remove("corp_name");
                    return filteredRow;
                })
                .toList();

        return DartMainInfoClientResponse.toDomain(corpCode, rceptNo, type, filteredList);
    }

    /**
     * Domain Command를 Infrastructure query parameters로 변환한다.
     */
    @Override
    protected MultiValueMap<String, String> buildQueryParameters(DartMainInfoClientRequest command) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        addCertificationKey(params);
        params.add(CORP_CODE, command.corpCode());
        params.add("bsns_year", command.businessYear());
        params.add("reprt_code", command.reportCode());

        return params;
    }
}
