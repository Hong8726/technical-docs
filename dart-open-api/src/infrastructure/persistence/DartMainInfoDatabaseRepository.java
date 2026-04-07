package com.hong.diclosure.dart.infrastructure.persistence;


import org.springframework.stereotype.Repository;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import java.util.Optional;

/**
 * DART 주요정보(MainInfo) 문서 저장/조회 Repository 구현체
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
@Repository
class DartMainInfoDatabaseRepository extends XmlDatabaseRepository implements DartMainInfoRepository {

    private static final String MAIN_INFO_COLLECTION_PATH = "dart/mainInfo";

    DartMainInfoDatabaseRepository(Collection collection) {
        super(collection);
    }

    @Override
    protected String getCollectionBasePath() {
        return MAIN_INFO_COLLECTION_PATH;
    }

    @Override
    public void saveMainInfoToCollection(String corpCode, String receiptNumber, String mainInfoType, String content) throws XMLDBException {
        String collectionPath = buildCollectionPath(corpCode, receiptNumber, mainInfoType);
        saveToCollection(collectionPath, content);
    }

    @Override
    public Optional<String> findMainInfoFromCollection(String corpCode, String receiptNumber, String mainInfoType) throws XMLDBException {
        String collectionPath = buildCollectionPath(corpCode, receiptNumber, mainInfoType);
        return findFromCollection(collectionPath);
    }

    /**
     * collection 경로를 생성합니다.
     */
    protected String buildCollectionPath(String corpCode, String receiptNumber, String mainInfoType) {
        return String.format("%s/%s/%s/%s", getCollectionBasePath(), corpCode, receiptNumber, mainInfoType);
    }
}
