package com.hong.diclosure.dart.domain;

/**
 * DART 정기보고서 코드를 나타내는 열거형.
 * 사업보고서, 분기보고서, 반기보고서 등의 정기보고서 유형을 정의한다.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public enum ReportCode {
    /**
     * 1분기보고서
     */
    QUARTERLY_1ST("11013", "1분기보고서"),

    /**
     * 반기보고서
     */
    SEMI_ANNUAL("11012", "반기보고서"),

    /**
     * 3분기보고서
     */
    QUARTERLY_3RD("11014", "3분기보고서"),

    /**
     * 사업보고서
     */
    ANNUAL("11011", "사업보고서");

    private final String code;
    private final String description;

    ReportCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 코드 문자열로부터 ReportCode를 조회한다.
     *
     * @param code 보고서 코드 (예: "11011", "11012")
     * @return 대응하는 ReportCode
     * @throws IllegalArgumentException 유효하지 않은 코드인 경우
     */
    public static ReportCode fromCode(String code) {
        for (ReportCode reportCode : values()) {
            if (reportCode.code.equals(code)) {
                return reportCode;
            }
        }
        throw new IllegalArgumentException("Invalid report code: " + code);
    }
}
