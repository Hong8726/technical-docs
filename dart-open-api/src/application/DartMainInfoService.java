package com.hong.diclosure.dart.application;


import com.hong.diclosure.dart.domain.DartMainInfo;

/**
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public interface DartMainInfoService {

    /**
     * DART 정기보고서 주요정보를 조회한다.
     *
     * @param request 조회 요청 객체
     * @return 조회 결과
     */
    DartMainInfo getMainInfo(DartMainInfo request);
}
