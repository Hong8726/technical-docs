package com.logappender.rabbit;

import ch.qos.logback.classic.spi.ILoggingEvent;

record LogMessage(String traceId, String logger, String level, String service, String message, String timestamp) {
    /**
     * 로그 이벤트로부터 LogMessage를 만들기 위한 빌더를 생성한다.
     */
    static Builder builder(ILoggingEvent event) {
        return new Builder(event);
    }

    /**
     * LogMessage 생성을 보조하는 단순 빌더이다.
     * 필수 값은 로그 이벤트로부터 가져오고, 서비스와 타임스탬프는 외부에서 설정한다.
     */
    static class Builder {

        String traceId;

        private final ILoggingEvent event;

        private String service;

        private String level;

        private String timestamp;

        /**
         * 로그 이벤트를 받아 빌더를 초기화한다.
         */
        Builder(ILoggingEvent event) {
            this.event = event;
        }

        /**
         * 트레이스 ID를 설정한다.
         */
        Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        /**
         * 서비스명을 설정한다.
         */
        Builder service(String service) {
            this.service = service;
            return this;
        }

        /**
         * 로그 레벨을 설정한다.
         */
        Builder level(String level) {
            this.level = level;
            return this;
        }

        /**
         * 타임스탬프를 설정한다.
         * ISO-8601 형태의 문자열 사용을 권장한다.
         */
        Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * 설정된 값으로 LogMessage를 생성한다.
         */
        LogMessage build() {
            return new LogMessage(
                    traceId,
                    event.getLoggerName(),
                    event.getLevel().toString(),
                    service,
                    event.getFormattedMessage(),
                    timestamp
            );
        }
    }
}
