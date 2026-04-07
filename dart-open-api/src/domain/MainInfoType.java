package com.hong.diclosure.dart.domain;

/**
 * DART 정기보고서 주요정보 API 타입.
 * 각 타입은 호출할 엔드포인트를 정의한다.
 * DART Open API 개발가이드 참조: https://opendart.fss.or.kr/guide/main.do?apiGrpCd=DS002
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public enum MainInfoType {

    /**
     * 증자(감자) 현황 (apiId: 2019004)
     */
    CAPITAL_CHANGES("/irdsSttus.json"),

    /**
     * 배당에 관한 사항 (apiId: 2019005)
     */
    DIVIDENDS("/alotMatter.json"),

    /**
     * 자기주식 취득 및 처분 현황 (apiId: 2019006)
     */
    TREASURY_STOCK("/tesstkAcqsDspsSttus.json"),

    /**
     * 최대주주 현황 (apiId: 2019007)
     */
    MAJOR_SHAREHOLDERS("/hyslrSttus.json"),

    /**
     * 최대주주 변동현황 (apiId: 2019008)
     */
    MAJOR_SHAREHOLDER_CHANGES("/hyslrChgSttus.json"),

    /**
     * 소액주주 현황 (apiId: 2019009)
     */
    MINORITY_SHAREHOLDERS("/mrhlSttus.json"),

    /**
     * 임원 현황 (apiId: 2019010)
     */
    EXECUTIVES("/exctvSttus.json"),

    /**
     * 직원 현황 (apiId: 2019011)
     */
    EMPLOYEES("/empSttus.json"),

    /**
     * 이사·감사의 개인별 보수현황(5억원 이상) (apiId: 2019012)
     */
    INDIVIDUAL_EXECUTIVE_COMPENSATION("/hmvAuditIndvdlBySttus.json"),

    /**
     * 이사·감사 전체의 보수현황(보수지급금액 - 이사·감사 전체) (apiId: 2019013)
     */
    TOTAL_EXECUTIVE_COMPENSATION("/hmvAuditAllSttus.json"),

    /**
     * 개인별 보수지급 금액(5억이상 상위5인) (apiId: 2019014)
     */
    TOP5_INDIVIDUAL_COMPENSATION("/indvdlByPay.json"),

    /**
     * 타법인 출자현황 (apiId: 2019015)
     */
    OTHER_CORPORATE_INVESTMENT("/otrCprInvstmntSttus.json"),

    /**
     * 주식의 총수 현황 (apiId: 2020002)
     */
    TOTAL_SHARES("/stockTotqySttus.json"),

    /**
     * 채무증권 발행실적 (apiId: 2020003)
     */
    DEBT_SECURITIES_ISSUANCE("/detScritsIsuAcmslt.json"),

    /**
     * 기업어음증권 미상환 잔액 (apiId: 2020004)
     */
    COMMERCIAL_PAPER_BALANCE("/entrprsBilScritsNrdmpBlce.json"),

    /**
     * 단기사채 미상환 잔액 (apiId: 2020005)
     */
    SHORT_TERM_BONDS_BALANCE("/srtpdPsndbtNrdmpBlce.json"),

    /**
     * 회사채 미상환 잔액 (apiId: 2020006)
     */
    CORPORATE_BONDS_BALANCE("/cprndNrdmpBlce.json"),

    /**
     * 신종자본증권 미상환 잔액 (apiId: 2020007)
     */
    HYBRID_SECURITIES_BALANCE("/newCaplScritsNrdmpBlce.json"),

    /**
     * 조건부 자본증권 미상환 잔액 (apiId: 2020008)
     */
    CONTINGENT_CAPITAL_BALANCE("/cndlCaplScritsNrdmpBlce.json"),

    /**
     * 회계감사인의 명칭 및 감사의견 (apiId: 2020009)
     */
    AUDIT_OPINION("/accnutAdtorNmNdAdtOpinion.json"),

    /**
     * 감사용역체결현황 (apiId: 2020010)
     */
    AUDIT_SERVICE_CONTRACT("/adtServcCnclsSttus.json"),

    /**
     * 회계감사인과의 비감사용역 계약체결 현황 (apiId: 2020011)
     */
    NON_AUDIT_SERVICE_CONTRACT("/accnutAdtorNonAdtServcCnclsSttus.json"),

    /**
     * 사외이사 및 그 변동현황 (apiId: 2020012)
     */
    OUTSIDE_DIRECTORS("/outcmpnyDrctrNdChangeSttus.json"),

    /**
     * 미등기임원 보수현황 (apiId: 2020013)
     */
    UNREGISTERED_EXECUTIVE_COMPENSATION("/unrstExctvMendngSttus.json"),

    /**
     * 이사·감사 전체의 보수현황(주주총회 승인금액) (apiId: 2020014)
     */
    TOTAL_EXEC_COMP_APPROVED("/drctrAdtAllMendngSttusGmtsckConfmAmount.json"),

    /**
     * 이사·감사 전체의 보수현황(보수지급금액 - 유형별) (apiId: 2020015)
     */
    TOTAL_EXEC_COMP_BY_TYPE("/drctrAdtAllMendngSttusMendngPymntamtTyCl.json"),

    /**
     * 공모자금의 사용내역 (apiId: 2020016)
     */
    PUBLIC_OFFERING_FUND_USE("/pssrpCptalUseDtls.json"),

    /**
     * 사모자금의 사용내역 (apiId: 2020017)
     */
    PRIVATE_OFFERING_FUND_USE("/prvsrpCptalUseDtls.json");

    private final String endpoint;

    MainInfoType(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
