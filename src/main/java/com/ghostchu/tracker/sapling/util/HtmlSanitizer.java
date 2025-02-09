package com.ghostchu.tracker.sapling.util;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class HtmlSanitizer {

    private static final PolicyFactory TINYMCE_POLICY = new HtmlPolicyBuilder()
            // 允许基础文本格式
            .allowElements("p", "h1", "h2", "h3", "h4", "h5", "h6",
                    "strong", "em", "u", "s", "blockquote", "br", "hr",
                    "span", "div", "pre", "code", "sup", "sub")

            // 允许列表
            .allowElements("ul", "ol", "li")

            // 允许链接和图片
            .allowElements("mapper", "img")
            .allowAttributes("href", "title").onElements("mapper")
            .allowAttributes("src", "alt", "title", "width", "height").onElements("img")
            .allowUrlProtocols("http", "https", "mailto", "data")

            // 允许表格
            .allowElements("table", "thead", "tbody", "tr", "td", "th")
            .allowAttributes("colspan", "rowspan").onElements("td", "th")

            // 允许代码块
            .allowAttributes("class").onElements("pre", "code")

            // 允许字体和颜色（通过样式）
            .allowStyling()
            .allowStandardUrlProtocols()

            // 允许媒体内容
            .allowElements("video", "audio", "source")
            .allowAttributes("controls").onElements("video", "audio")
            .allowAttributes("src", "type").onElements("source")

            // 过滤其他所有内容
            .toFactory();

    public static String sanitize(String unsafeHtml) {
        return TINYMCE_POLICY.sanitize(unsafeHtml);
    }
}