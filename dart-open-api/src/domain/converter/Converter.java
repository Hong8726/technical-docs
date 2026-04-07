package com.hong.diclosure.dart.domain.converter;

/**
 * 데이터 변환 인터페이스.
 * 다양한 데이터 변환 전략을 구현할 수 있도록 추상화한다.
 *
 * @param <I> 입력 타입
 * @param <O> 출력 타입
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public interface Converter<I, O> {

    /**
     * 입력 데이터를 출력 데이터로 변환한다.
     *
     * @param input 변환할 데이터
     * @return 변환된 데이터
     */
    O convert(I input);
}
