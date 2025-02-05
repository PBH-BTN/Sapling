package com.ghostchu.tracker.sapling.util;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class TorrentParser {

    private static final Bencode BENCODE = new Bencode(StandardCharsets.ISO_8859_1);

    public static TorrentInfo parse(byte[] bytes) {
        Map<String, Object> torrentMap = BENCODE.decode(bytes, Type.DICTIONARY);

        // 提取info字典
        Map<String, Object> info = (Map<String, Object>) torrentMap.get("info");
        if (info == null) {
            throw new IllegalArgumentException("无效种子文件，缺失 info 字典");
        }

        // 计算 infohash (SHA-1, BEP-3)
        var infohash = Hashing.sha1().hashBytes(BENCODE.encode(info));
        // 计算 infohash2 (SHA-256, BEP-52)
        var infohash2 = Hashing.sha256().hashBytes(BENCODE.encode(info));

        boolean v1Found = info.containsKey("files");
        boolean v2Found = info.containsKey("meta version") && "2".equals(info.get("meta version"));

        if (!v1Found && !v2Found) {
            throw new IllegalArgumentException("种子文件特征识别失败");
        }

        var fileTree = buildFileTree(info);
        var bfsResult = bfsFileTree(fileTree);
        return new TorrentInfo(
                getTrackers(torrentMap),
                (String) torrentMap.get("created by"),
                getCreationDate(torrentMap),
                (String) torrentMap.get("encoding"),
                fileTree,
                isPrivate(info),
                (String) info.get("publisher"),
                (String) torrentMap.get("comment"),
                v2Found,
                v1Found ? infohash.toString() : null,
                v2Found ? infohash2.toString() : null,
                v1Found ? infohash.asBytes() : null,
                v2Found ? infohash2.asBytes() : null,
                bfsResult.getKey(),
                bfsResult.getValue()
        );
    }

    public static Map.Entry<Long, Long> bfsFileTree(TorrentNode root) {
        if (root == null) {
            return Map.entry(0L, 0L);
        }
        long totalSize = 0;
        long totalFiles = 0;
        Queue<TorrentNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TorrentNode node = queue.poll();
            // 计算当前节点的文件大小
            if (node.getFileSize() != null) {
                totalSize += node.getFileSize();
            }
            // 如果是目录，加入子节点
            if (node.getChildren() != null) {
                queue.addAll(node.getChildren());
            } else {
                totalFiles++;
            }
        }
        return Map.entry(totalSize, totalFiles);
    }

    // 获取Tracker列表
    private static List<String> getTrackers(Map<String, Object> torrentMap) {
        List<String> trackers = new ArrayList<>();

        // 处理announce-list
        Object announceList = torrentMap.get("announce-list");
        if (announceList instanceof List) {
            for (Object tierObj : (List<?>) announceList) {
                if (tierObj instanceof List<?> tier) {
                    if (!tier.isEmpty() && tier.getFirst() instanceof String) {
                        trackers.add((String) tier.getFirst());
                    }
                }
            }
        }

        // 添加单announce
        if (trackers.isEmpty()) {
            Object announce = torrentMap.get("announce");
            if (announce instanceof String) {
                trackers.add((String) announce);
            }
        }

        return trackers.stream().distinct().collect(Collectors.toList());
    }

    // 转换创建时间
    private static OffsetDateTime getCreationDate(Map<String, Object> torrentMap) {
        Long creationDate = (Long) torrentMap.get("creation date");
        return creationDate != null ?
                OffsetDateTime.ofInstant(Instant.ofEpochSecond(creationDate), ZoneOffset.UTC) :
                null;
    }

    // 构建文件树
    private static TorrentNode buildFileTree(Map<String, Object> info) {
        String encoding = Optional.ofNullable((String) info.get("encoding")).orElse("UTF-8");
        Charset charset = Charset.forName(encoding);

        // 判断版本
        if (info.containsKey("file tree")) {
            // BTV2处理逻辑
            Map<String, Object> fileTree = (Map<String, Object>) info.get("file tree");
            return buildV2FileTree(fileTree, "", charset);
        } else if (info.containsKey("files")) {
            // BTV1处理逻辑
            return buildV1FileTree((List<Map<String, Object>>) info.get("files"), charset);
        } else {
            // 单文件情况
            TorrentNode fileNode = new TorrentNode();
            var iso8859Name = (String) info.get("name");
            fileNode.setName(new String(iso8859Name.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            fileNode.setDirectory(false);
            fileNode.setFileSize((Long) info.get("length"));
            return fileNode;
        }
    }

    // 构建V2文件树
    private static TorrentNode buildV2FileTree(Map<String, Object> nodeMap, String name, Charset charset) {
        TorrentNode node = new TorrentNode();
        node.setName(name);

        if (nodeMap.containsKey("length")) {
            // 文件节点
            node.setDirectory(false);
            node.setFileSize((Long) nodeMap.get("length"));
            node.setHash((String) nodeMap.get("pieces root"));
            node.setEd2kHash((String) nodeMap.get("ed2k"));
        } else {
            // 目录节点
            node.setDirectory(true);
            List<TorrentNode> children = new ArrayList<>();
            for (Map.Entry<String, Object> entry : nodeMap.entrySet()) {
                String childName = decodeString(entry.getKey().getBytes(StandardCharsets.ISO_8859_1), charset);
                Map<String, Object> childMap = (Map<String, Object>) entry.getValue();
                children.add(buildV2FileTree(childMap, childName, charset));
            }
            node.setChildren(children);
        }
        return node;
    }

    // 构建V1文件树
    private static TorrentNode buildV1FileTree(List<Map<String, Object>> files, Charset charset) {
        TorrentNode root = new TorrentNode();
        root.setName("");
        root.setDirectory(true);
        root.setChildren(new ArrayList<>());

        for (Map<String, Object> file : files) {
            List<String> pathComponents = new ArrayList<>(((List<String>) file.get("path")));

            long length = (Long) file.get("length");
            addV1FileToTree(root, pathComponents, 0, length);
        }
        return root;
    }

    private static void addV1FileToTree(TorrentNode currentNode, List<String> path, int depth, long length) {
        if (depth >= path.size()) return;

        String currentPart = path.get(depth);
        boolean isFile = (depth == path.size() - 1);

        TorrentNode child = currentNode.getChildren().stream()
                .filter(n -> n.getName().equals(currentPart))
                .findFirst()
                .orElseGet(() -> {
                    TorrentNode newNode = new TorrentNode();
                    newNode.setName(new String(currentPart.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                    newNode.setDirectory(!isFile);
                    if (!isFile) newNode.setChildren(new ArrayList<>());
                    currentNode.getChildren().add(newNode);
                    return newNode;
                });

        if (isFile) {
            child.setFileSize(length);
        } else {
            addV1FileToTree(child, path, depth + 1, length);
        }
    }

    // 检查私有种子
    private static boolean isPrivate(Map<String, Object> info) {
        return info.get("private") instanceof Long && (Long) info.get("private") == 1L;
    }

    // 字节数组转字符串（处理编码）
    private static String decodeString(byte[] bytes, Charset charset) {
        return new String(bytes, charset);
    }

    // 数据结构定义
    public record TorrentInfo(List<String> trackers, String createdBy, OffsetDateTime creationDate, String encoding,
                              TorrentNode fileTree, boolean isPrivate, String publisher, String comment, boolean btv2,
                              String infoHashV1, String infoHashV2, byte[] infoHashV1Bytes, byte[] infoHashV2Bytes,
                              long totalSize,
                              long files) {
        // 构造函数、getter省略
    }

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class TorrentNode {
        private String name;
        private boolean isDirectory;
        private List<TorrentNode> children;
        private String hash;
        private String ed2kHash;
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
                    .append(hash == null ? null : HexFormat.of().formatHex(hash.getBytes(StandardCharsets.ISO_8859_1)))
                    .append(", ed2kHash=")
                    .append(ed2kHash == null ? null : HexFormat.of().formatHex(ed2kHash.getBytes(StandardCharsets.ISO_8859_1)))
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
                return new ObjectMapper().writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}