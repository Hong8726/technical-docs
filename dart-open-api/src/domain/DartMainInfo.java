package com.hong.diclosure.dart.domain;

import com.hong.support.Preconditions;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * DART 정기보고서 주요정보 도메인 엔티티.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public class DartMainInfo {

    private final String corporationCode;
    private final String businessYear;
    private final ReportCode reportCode;
    private final String receiptNumber;
    private final MainInfoType type;
    private String contents;
    private List<LinkedHashMap<String, String>> rawData;

    public String getCorporationCode() {
        return corporationCode;
    }

    public String getBusinessYear() {
        return businessYear;
    }

    private DartMainInfo(String corporationCode, String businessYear, String reportCode, String receiptNumber, MainInfoType type) {
        this.corporationCode = corporationCode;
        this.businessYear = businessYear;
        this.reportCode = reportCode != null ? ReportCode.fromCode(reportCode) : null;
        this.receiptNumber = receiptNumber;
        this.type = type;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public String getContents() {
        return contents;
    }

    public ReportCode getReportCode() {
        return reportCode;
    }

    private DartMainInfo(Builder builder) {
        this.corporationCode = builder.corporationCode;
        this.businessYear = builder.businessYear;
        this.reportCode = builder.reportCode;
        this.receiptNumber = builder.receiptNumber;
        this.type = builder.type;
        this.contents = builder.contents;
    }

    public static DartMainInfo createForRequest(String corporationCode, String businessYear, String reportCode, String receiptNumber, MainInfoType type) {
        Preconditions.hasText(corporationCode, "corporationCode must not be empty");
        Preconditions.hasText(businessYear, "businessYear must not be empty");
        Preconditions.hasText(reportCode, "reportCode must not be empty");
        Preconditions.notNull(type, "type must not be null");

        if (!corporationCode.matches("\\d{8}")) {
            throw new IllegalArgumentException("corporationCode must be 8 digits");
        }
        if (!businessYear.matches("\\d{4}")) {
            throw new IllegalArgumentException("businessYear must be 4 digits");
        }
        int year = Integer.parseInt(businessYear);
        int currentYear = java.time.Year.now().getValue();
        if (year < 1900 || year > currentYear + 1) {
            throw new IllegalArgumentException(
                    "businessYear must be between 1900 and " + (currentYear + 1));
        }
        return new DartMainInfo(corporationCode, businessYear, reportCode, receiptNumber, type);
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public static DartMainInfo createFromResponse(String corporationCode, String receiptNumber, MainInfoType type, List<LinkedHashMap<String, String>> rawData) {
        Preconditions.hasText(corporationCode, "corporationCode must not be empty");
        Preconditions.hasText(receiptNumber, "receiptNumber must not be empty");
        Preconditions.notNull(type, "type must not be null");
        Preconditions.notNull(rawData, "rawData must not be null");
        DartMainInfo mainInfo = new DartMainInfo(corporationCode, null, null, receiptNumber, type);
        mainInfo.rawData = rawData;
        return mainInfo;
    }

    public List<LinkedHashMap<String, String>> getRawData() {
        return rawData;
    }

    public MainInfoType getType() {
        return type;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String corporationCode;
        private String businessYear;
        private ReportCode reportCode;
        private String receiptNumber;
        private MainInfoType type;
        private String contents;

        public DartMainInfo.Builder corporationCode(String corporationCode) {
            this.corporationCode = corporationCode;
            return this;
        }

        public DartMainInfo.Builder businessYear(String businessYear) {
            this.businessYear = businessYear;
            return this;
        }

        public DartMainInfo.Builder reportCode(String reportCode) {
            this.reportCode = ReportCode.fromCode(reportCode);
            return this;
        }

        public DartMainInfo.Builder receiptNumber(String receiptNumber) {
            this.receiptNumber = receiptNumber;
            return this;
        }

        public DartMainInfo.Builder type(MainInfoType type) {
            this.type = type;
            return this;
        }

        public DartMainInfo.Builder contents(String contents) {
            this.contents = contents;
            return this;
        }


        public DartMainInfo build() {
            return new DartMainInfo(this);
        }
    }
}
