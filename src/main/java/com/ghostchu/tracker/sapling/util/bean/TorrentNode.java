package com.ghostchu.tracker.sapling.util.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ghostchu.tracker.sapling.config.JacksonConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public final class TorrentNode implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private boolean isDirectory;
    private List<TorrentNode> children;
    private String hashHex;
    private String ed2kHashHex;
    private Long fileSize;

    // getter/setter省略
    // 重写 toString() 方法，生成可爱的 ASCII 树状结构喵～
    @Override
    public String toString() {
        return buildAsciiTree("", true);
    }

    // 辅助方法，递归构建树形结构喵～
    private String buildAsciiTree(String prefix, boolean isTail) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix)
                .append(isTail ? "└── " : "├── ")
                .append(name)
                .append(" (dir=")
                .append(isDirectory)
                .append(", hash=")
                .append(hashHex)
                .append(", ed2kHash=")
                .append(ed2kHashHex)
                .append(", fileSize=")
                .append(fileSize)
                .append(")\n");
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size() - 1; i++) {
                sb.append(children.get(i).buildAsciiTree(prefix + (isTail ? "    " : "│   "), false));
            }
            sb.append(children.getLast()
                    .buildAsciiTree(prefix + (isTail ? "    " : "│   "), true));
        }
        return sb.toString();
    }

    // 使用 Jackson 生成 JSON 字符串喵～
    public String toJson() {
        try {
            return JacksonConfig.objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
