package com.hong.diclosure.dart.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.hong.diclosure.dart.application.DartDisclosureService;
import com.hong.diclosure.dart.application.DartMainInfoService;
import com.hong.diclosure.dart.domain.DartDisclosureSearchInfo;
import com.hong.diclosure.dart.domain.DartMainInfo;
import com.hong.diclosure.dart.domain.MainInfoType;
import com.hong.diclosure.dart.presentation.annotation.DartApiResponses;
import com.hong.diclosure.dart.presentation.resource.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author 홍보람 (qhfka2854@gmail.com)
 */
@RestController
@RequestMapping("/api/v1/dart/companies")
@Validated
@Tag(name = "Dart open API", description = "Dart open API 연동")
public class DartDisclosureController {

    private final Logger log;
    private final DartDisclosureService dartDisclosureService;
    private final DartMainInfoService dartMainInfoService;

    public DartDisclosureController(DartDisclosureService dartDisclosureService, DartMainInfoService dartMainInfoService) {
        this.dartDisclosureService = dartDisclosureService;
        this.dartMainInfoService = dartMainInfoService;
        this.log = LoggerFactory.getLogger(DartDisclosureController.class);
    }

    @Operation(summary = "Open DART 공시 검색", description = "회사의 공시 목록을 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures")
    public DartDisclosureSearchResponse searchDisclosures(
            @RequestHeader("Company-Code") String corpCode,
            @Valid @ParameterObject DartDisclosureSearchRequest request) {
        log.info("[searchDisclosures] corpCode={}", request.corpCode());

        // Presentation Request를 Domain으로 변환
        DartDisclosureSearchInfo searchInfo = dartDisclosureService.searchDisclosures(request.toDomain(corpCode));

        return DartDisclosureSearchResponse.fromDomain(searchInfo);
    }

    @Operation(summary = "Open DART 공시 문서 내용 조회", description = "접수번호 기준으로 공시 원문을 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/{receiptNumber}/document")
    public DartDocumentData getDisclosureReport(
            @RequestHeader("Company-Code") String corpCode,
            @PathVariable String receiptNumber) {
        log.info("[downloadDocument] corpCode={}, receiptNumber={}", corpCode, receiptNumber);
        String document = dartDisclosureService.getReportContent(corpCode, receiptNumber);
        return new DartDocumentData(document);
    }

    @Operation(summary = "Open DART 증자(감자) 현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 증자(감자) 현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/capital-changes")
    public DartMainInfoResponse getCapitalChanges(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getCapitalChanges] corpCode={}, request={}", corpCode, request);

        // Application Service 호출 - 증자(감자) 현황 타입 지정
        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.CAPITAL_CHANGES));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 배당에 관한 사항 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 배당 현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/dividends")
    public DartMainInfoResponse getDividends(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getDividends] corpCode={}, request={}", corpCode, request);

        // Application Service 호출 - 배당 현황 타입 지정
        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.DIVIDENDS));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 최대주주 현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 최대주주 현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/major-shareholders")
    public DartMainInfoResponse getMajorShareholders(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getMajorShareholders] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.MAJOR_SHAREHOLDERS));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 자기주식 취득 및 처분 현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 자기주식 취득 및 처분 현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/treasury-stock")
    public DartMainInfoResponse getTreasuryStock(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getTreasuryStock] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.TREASURY_STOCK));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 최대주주 변동현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 최대주주 변동현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/major-shareholder-changes")
    public DartMainInfoResponse getMajorShareholderChanges(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getMajorShareholderChanges] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.MAJOR_SHAREHOLDER_CHANGES));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 소액주주 현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 소액주주 현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/minority-shareholders")
    public DartMainInfoResponse getMinorityShareholders(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getMinorityShareholders] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.MINORITY_SHAREHOLDERS));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 임원 현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 임원 현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/executives")
    public DartMainInfoResponse getExecutives(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getExecutives] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.EXECUTIVES));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 직원 현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 직원 현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/employees")
    public DartMainInfoResponse getEmployees(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getEmployees] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.EMPLOYEES));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 이사·감사의 개인별 보수현황(5억원 이상) 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 이사·감사의 개인별 보수현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/individual-executive-compensation")
    public DartMainInfoResponse getIndividualExecutiveCompensation(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getIndividualExecutiveCompensation] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.INDIVIDUAL_EXECUTIVE_COMPENSATION));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 이사·감사 전체의 보수현황(보수지급금액) 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 이사·감사 전체의 보수현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/total-executive-compensation")
    public DartMainInfoResponse getTotalExecutiveCompensation(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getTotalExecutiveCompensation] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.TOTAL_EXECUTIVE_COMPENSATION));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 개인별 보수지급 금액(5억이상 상위5인) 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 개인별 보수지급 금액 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/top5-individual-compensation")
    public DartMainInfoResponse getTop5IndividualCompensation(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getTop5IndividualCompensation] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.TOP5_INDIVIDUAL_COMPENSATION));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 타법인 출자현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 타법인 출자현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/other-corporate-investment")
    public DartMainInfoResponse getOtherCorporateInvestment(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getOtherCorporateInvestment] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.OTHER_CORPORATE_INVESTMENT));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 주식의 총수 현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 주식의 총수 현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/total-shares")
    public DartMainInfoResponse getTotalShares(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getTotalShares] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.TOTAL_SHARES));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 채무증권 발행실적 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 채무증권 발행실적 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/debt-securities-issuance")
    public DartMainInfoResponse getDebtSecuritiesIssuance(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getDebtSecuritiesIssuance] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.DEBT_SECURITIES_ISSUANCE));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 기업어음증권 미상환 잔액 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 기업어음증권 미상환 잔액 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/commercial-paper-balance")
    public DartMainInfoResponse getCommercialPaperBalance(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getCommercialPaperBalance] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.COMMERCIAL_PAPER_BALANCE));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 단기사채 미상환 잔액 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 단기사채 미상환 잔액 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/short-term-bonds-balance")
    public DartMainInfoResponse getShortTermBondsBalance(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getShortTermBondsBalance] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.SHORT_TERM_BONDS_BALANCE));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 회사채 미상환 잔액 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 회사채 미상환 잔액 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/corporate-bonds-balance")
    public DartMainInfoResponse getCorporateBondsBalance(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getCorporateBondsBalance] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.CORPORATE_BONDS_BALANCE));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 신종자본증권 미상환 잔액 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 신종자본증권 미상환 잔액 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/hybrid-securities-balance")
    public DartMainInfoResponse getHybridSecuritiesBalance(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getHybridSecuritiesBalance] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.HYBRID_SECURITIES_BALANCE));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 조건부 자본증권 미상환 잔액 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 조건부 자본증권 미상환 잔액 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/contingent-capital-balance")
    public DartMainInfoResponse getContingentCapitalBalance(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getContingentCapitalBalance] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.CONTINGENT_CAPITAL_BALANCE));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 회계감사인의 명칭 및 감사의견 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 회계감사인의 명칭 및 감사의견 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/audit-opinion")
    public DartMainInfoResponse getAuditOpinion(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getAuditOpinion] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.AUDIT_OPINION));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 감사용역체결현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 감사용역체결현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/audit-service-contract")
    public DartMainInfoResponse getAuditServiceContract(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getAuditServiceContract] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.AUDIT_SERVICE_CONTRACT));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 회계감사인과의 비감사용역 계약체결 현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 회계감사인과의 비감사용역 계약체결 현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/non-audit-service-contract")
    public DartMainInfoResponse getNonAuditServiceContract(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getNonAuditServiceContract] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.NON_AUDIT_SERVICE_CONTRACT));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 사외이사 및 그 변동현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 사외이사 및 그 변동현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/outside-directors")
    public DartMainInfoResponse getOutsideDirectors(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getOutsideDirectors] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.OUTSIDE_DIRECTORS));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 미등기임원 보수현황 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 미등기임원 보수현황 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/unregistered-executive-compensation")
    public DartMainInfoResponse getUnregisteredExecutiveCompensation(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getUnregisteredExecutiveCompensation] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.UNREGISTERED_EXECUTIVE_COMPENSATION));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 이사·감사 전체의 보수현황(주주총회 승인금액) 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 이사·감사 전체의 보수현황(주주총회 승인금액) 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/total-exec-comp-approved")
    public DartMainInfoResponse getTotalExecCompApproved(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getTotalExecCompApproved] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.TOTAL_EXEC_COMP_APPROVED));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 이사·감사 전체의 보수현황(보수지급금액 - 유형별) 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 이사·감사 전체의 보수현황(보수지급금액 - 유형별) 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/total-exec-comp-by-type")
    public DartMainInfoResponse getTotalExecCompByType(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getTotalExecCompByType] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.TOTAL_EXEC_COMP_BY_TYPE));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 공모자금의 사용내역 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 공모자금의 사용내역 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/public-offering-fund-use")
    public DartMainInfoResponse getPublicOfferingFundUse(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getPublicOfferingFundUse] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.PUBLIC_OFFERING_FUND_USE));

        return DartMainInfoResponse.fromDomain(response);
    }

    @Operation(summary = "Open DART 사모자금의 사용내역 조회", description = "고유번호, 사업연도, 보고서 코드를 기준으로 사모자금의 사용내역 데이터를 조회합니다.")
    @DartApiResponses
    @GetMapping("/disclosures/private-offering-fund-use")
    public DartMainInfoResponse getPrivateOfferingFundUse(
            @RequestHeader("Company-Code") String corpCode,
            @ParameterObject DartMainInfoRequest request) {
        log.info("[getPrivateOfferingFundUse] corpCode={}, request={}", corpCode, request);

        DartMainInfo response = dartMainInfoService.getMainInfo(request.toDomain(corpCode, MainInfoType.PRIVATE_OFFERING_FUND_USE));

        return DartMainInfoResponse.fromDomain(response);
    }
}
