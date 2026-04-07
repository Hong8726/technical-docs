package com.hong.diclosure.dart.infrastructure.persistence;

import com.hong.support.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

import java.util.Optional;

/**
 * XML Database Repository의 추상 클래스.
 * 공통 의존성 및 기능을 제공한다.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public abstract class XmlDatabaseRepository {

    protected static final String RESOURCE_ID = "content";
    protected static final String RESOURCE_TYPE = "XMLResource";

    protected final Logger log;
    protected final Collection rootCollection;

    protected XmlDatabaseRepository(Collection collection) {
        this.rootCollection = collection;
        this.log = LoggerFactory.getLogger(getClass());
    }

    /**
     * 각 Repository가 사용할 기본 collection 경로를 반환한다.
     * 예: "dart/mainInfo" 또는 "dart/report"
     */
    protected abstract String getCollectionBasePath();

    /**
     * collection에 XML 문서를 저장합니다.
     *
     * @param collectionPath collection 경로 (예: "dart/mainInfo/00000001/12345")
     * @param contents       저장할 XML 내용
     * @throws XMLDBException XML DB 처리 중 오류 발생 시
     */
    protected void saveToCollection(String collectionPath, String contents) throws XMLDBException {
        log.debug("[saveToCollection] collectionPath={}", collectionPath);

        validateSaveInput(collectionPath, contents);

        Collection collection = getCollection(collectionPath);

        if(collection == null)
            collection = createCollection(collectionPath);

        storeResource(collection, contents);

        log.info("[saveToCollection] 문서 저장 완료: collectionPath={}", collectionPath);
    }

    /**
     * collection에서 XML 문서를 조회합니다.
     *
     * @param collectionPath collection 경로 (예: "dart/mainInfo/00000001/12345")
     * @return 조회된 XML 내용
     * @throws XMLDBException XML DB 처리 중 오류 발생 시
     */
    protected Optional<String> findFromCollection(String collectionPath) throws XMLDBException {
        log.debug("[findFromCollection] collectionPath={}", collectionPath);

        validateFindInput(collectionPath);

        Collection collection = getCollection(collectionPath);

        if(collection == null)
            collection = createCollection(collectionPath);

        return findResourceContent(collection);
    }

    /**
     * 저장 시 입력값을 검증합니다.
     */
    private void validateSaveInput(String collectionPath, String contents) {
        Preconditions.hasText(collectionPath, "collectionPath must not be empty");
        Preconditions.hasText(contents, "contents must not be empty");
    }

    /**
     * 조회 시 입력값을 검증합니다.
     */
    private void validateFindInput(String collectionPath) {
        Preconditions.hasText(collectionPath, "collectionPath must not be empty");
    }

    /**
     * collection에 XML 리소스를 저장합니다.
     */
    private void storeResource(Collection collection, String contents) throws XMLDBException {
        Preconditions.notNull(collection, "Collection must not be null");


        Resource resource = collection.createResource(RESOURCE_ID, RESOURCE_TYPE);
        resource.setContent(contents);
        collection.storeResource(resource);
    }

    /**
     * collection에서 리소스를 조회하고 내용을 반환합니다.
     *
     * @param collection 대상 Collection
     * @return 조회된 내용
     * @throws XMLDBException XML DB 처리 중 오류 발생 시
     */
    private Optional<String> findResourceContent(Collection collection) throws XMLDBException {
        Resource resource = collection.getResource(RESOURCE_ID);

        if (resource == null) {
            log.info("[findResourceContent] Resource not found in collection");
            return Optional.empty();
        }

        String content = resource.getContent().toString();

        log.info("[findResourceContent] Found content in collection");
        return Optional.of(content);
    }

    /**
     * 계층적으로 collection을 탐색합니다.
     * 예: "dart/mainInfo/00000001/12345" -> dart -> mainInfo -> 00000001 -> 12345
     */
    protected Collection getCollection(String collectionPath) throws XMLDBException {
        String[] pathSegments = collectionPath.split("/");
        Collection current = rootCollection;

        for (String segment : pathSegments) {

            Collection next = current.getChildCollection(segment);
            if (next == null) {
                log.warn("[getCollection] Collection segment not found: {}, full path: {}", segment, collectionPath);
                return null;
            }
            current = next;
        }

        return current;
    }

    /**
     * 계층적으로 collection을 생성합니다.
     * 예: "dart/mainInfo/00000001" -> dart 생성 -> dart에 mainInfo 생성 -> dart/mainInfo에 00000001 생성
     */
    private Collection createCollection(String collectionPath) throws XMLDBException {
        log.debug("[createCollection] collectionPath={}", collectionPath);

        String[] pathSegments = collectionPath.split("/");
        Collection current = rootCollection;
        StringBuilder currentPath = new StringBuilder();

        for (String segment : pathSegments) {

            // 경로 누적
            if (!currentPath.isEmpty()) {
                currentPath.append("/");
            }
            currentPath.append(segment);

            // 자식 컬렉션 확인
            Collection next = current.getChildCollection(segment);
            if (next == null) {
                // 없으면 생성
                CollectionManagementService cms = (CollectionManagementService) current.getService("CollectionManagementService", "1.0");
                cms.createCollection(segment);
                log.info("[createCollection] Collection segment created: {}", currentPath);

                next = current.getChildCollection(segment);
            }

            current = next;
        }
        log.info("[createCollection] Full collection path created: {}", collectionPath);
        return current;
    }
}
