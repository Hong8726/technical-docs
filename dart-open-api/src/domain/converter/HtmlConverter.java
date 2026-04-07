package com.hong.diclosure.dart.domain.converter;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DART API 응답을 HTML 테이블로 변환하는 컨버터 구현체.
 * List<LinkedHashMap>을 HTML String으로 변환하는 역할을 수행한다.
 *
 * @author 홍보람 (qhfka2854@gmail.com)
 */
@Component
public class HtmlConverter implements Converter<List<LinkedHashMap<String, String>>, String> {

    /**
     * DART API 응답을 HTML 테이블로 변환
     *
     * @param list DART API 응답
     * @return HTML 테이블 문자열
     */
    @Override
    public String convert(List<LinkedHashMap<String, String>> list) {
        if (list == null || list.isEmpty()) {
            return "<table></table>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<table>\n");

        // 바디 생성
        html.append("  <tbody>\n");
        for (Map<String, String> row : list) {
            html.append("    <tr>\n");
            row.keySet().forEach(key -> html.append("      <td>").append(row.getOrDefault(key, "")).append("</td>\n"));
            html.append("    </tr>\n");
        }
        html.append("  </tbody>\n");
        html.append("</table>");

        return html.toString();
    }
}
