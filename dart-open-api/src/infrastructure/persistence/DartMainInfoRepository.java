package com.hong.diclosure.dart.infrastructure.persistence;


import org.xmldb.api.base.XMLDBException;

import java.util.Optional;

/**
 * DART 주요정보(MainInfo) 문서를 저장하고 조회하는 Repository
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public interface DartMainInfoRepository {

    /**
     * collection에 주요정보를 저장합니다.
     *
     * @param corpCode      고유번호
     * @param receiptNumber 보고서 코드
     * @param mainInfoType  주요정보 유형
     * @param content       보고서 내용
     */
    void saveMainInfoToCollection(String corpCode, String receiptNumber, String mainInfoType, String content) throws XMLDBException;

    /**
     * collection에서 주요정보를 조회합니다.
     *
     * @param corpCode      고유번호
     * @param receiptNumber 보고서 코드
     * @param mainInfoType 주요 정보 유형
     * @return 주요정보 내용
     */
    Optional<String> findMainInfoFromCollection(String corpCode, String receiptNumber, String mainInfoType) throws XMLDBException;
}
