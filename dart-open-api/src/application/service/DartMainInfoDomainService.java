package com.hong.diclosure.dart.application.service;


import com.hong.diclosure.dart.application.DartMainInfoService;
import com.hong.diclosure.dart.domain.DartMainInfo;
import com.hong.diclosure.dart.domain.converter.Converter;
import com.hong.diclosure.dart.infrastructure.client.DartMainInfoClient;
import com.hong.diclosure.dart.infrastructure.persistence.DartMainInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xmldb.api.base.XMLDBException;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author 홍보람 (qhfka2854@gmail.com)
 */

@Service
class DartMainInfoDomainService implements DartMainInfoService {

    private final DartMainInfoClient dartMainInfoClient;
    private final DartMainInfoRepository dartMainInfoRepository;
    private final Converter<List<LinkedHashMap<String, String>>, String> htmlConverter;
    private final Logger log;

    public DartMainInfoDomainService(DartMainInfoClient dartMainInfoClient,
                                     DartMainInfoRepository dartMainInfoRepository,
                                     Converter<List<LinkedHashMap<String, String>>, String> htmlConverter) {
        this.dartMainInfoClient = dartMainInfoClient;
        this.dartMainInfoRepository = dartMainInfoRepository;
        this.htmlConverter = htmlConverter;
        this.log = LoggerFactory.getLogger(DartMainInfoDomainService.class);
    }

    @Override
    public DartMainInfo getMainInfo(DartMainInfo dartMainInfo) {
        log.debug("[getMainInfo] type={} dartMainInfo={}", dartMainInfo.getType(), dartMainInfo);
        try {
            return dartMainInfoRepository
                    .findMainInfoFromCollection(dartMainInfo.getCorporationCode(), dartMainInfo.getReceiptNumber(), dartMainInfo.getType().toString())
                    .map(content -> {
                        log.info("[getMainInfo] Found in ExistDB: type={} corpCode={}, receiptNumber={}", dartMainInfo.getType(), dartMainInfo.getCorporationCode(), dartMainInfo.getReceiptNumber());
                        dartMainInfo.setContents(content);
                        return dartMainInfo;
                    })
                    .orElseGet(() -> {
                        log.info("[getMainInfo] Not found in DB, fetching from API and saving: type={}", dartMainInfo.getType());
                        DartMainInfo result = dartMainInfoClient.fetchMainInfo(dartMainInfo);
                        result.setContents(convertRawDataToHtml(result));
                        saveToRepository(result);
                        return result;
                    });
        } catch (XMLDBException e) {
            log.error("[getMainInfo] XMLDBException occurred: {}", e.getMessage(), e);
            DartMainInfo result = dartMainInfoClient.fetchMainInfo(dartMainInfo);
            result.setContents(convertRawDataToHtml(result));
            return result;
        }
    }

    /**
     * rawData를 HTML로 변환하고 contents에 설정합니다.
     */
    private String convertRawDataToHtml(DartMainInfo dartMainInfo) {
        return htmlConverter.convert(dartMainInfo.getRawData());
    }

    /**
     * API 응답 결과를 DB에 저장합니다.
     */
    private void saveToRepository(DartMainInfo dartMainInfo) {
        try {
            dartMainInfoRepository.saveMainInfoToCollection(
                    dartMainInfo.getCorporationCode(),
                    dartMainInfo.getReceiptNumber(),
                    dartMainInfo.getType().toString(),
                    dartMainInfo.getContents()
            );
            log.info("[saveToRepository] Saved to DB: corpCode={}, receiptNumber={}, type={}",
                    dartMainInfo.getCorporationCode(), dartMainInfo.getReceiptNumber(), dartMainInfo.getType());
        } catch (XMLDBException e) {
            log.warn("[saveToRepository] Failed to save to DB: {}", e.getMessage());
        }
    }
}
