package com.hong.disclosure.user.infrastructure;

/**
 * 이벤트 유형을 정의하는 열거형.
 * 생성(CREATED), 수정(UPDATED), 삭제(DELETED)와 같은 이벤트 유형을 나타낸다.
 * 시스템 내에서 발생하는 다양한 이벤트의 유형을 구분하는 데 사용한다.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
public enum EventType {
    CREATED,
    UPDATED,
    DELETED
}
