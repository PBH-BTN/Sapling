package com.ghostchu.tracker.sapling.permission;

public enum Permission {
    NEWS_VIEW("news:view"), // 查看站点公告
    NEWS_CREATE("news:create"), // 创建站点公告
    NEWS_DELETE("news:delete"), // 删除站点公告
    INBOX_VIEW("inbox:view"), // 查看站内信
    INBOX_CREATE("inbox:create"), // 创建站内信
    INBOX_DELETE("inbox:delete"), // 删除站内信
    INBOX_SAVE("inbox:save"), // 保存站内信
    BITBUCKET_VIEW("bitbucket:view"), // 查看上传的文件
    BITBUCKET_UPLOAD("bitbucket:upload"), // 上传文件
    BITBUCKET_DELETE("bitbucket:delete.self"), // 删除自己上传的文件
    TORRENT_VIEW("torrent:view"), // 查看种子
    TORRENT_PUBLISH("torrent:submit"), // 发布种子
    TORRENT_QUEUE("torrent:queue"), // 提交种子到待审队列
    TORRENT_DELETE_SELF("torrent:delete.self"), // 删除自己的种子
    TORRENT_EDIT_SELF("torrent:edit.self"), // 编辑种子
    TORRENT_EDIT("torrent:edit"), // 编辑所有人的种子
    TORRENT_COMMENT("torrent:comment"), // 评论种子
    TORRENT_RESEED_REQUEST("torrent:reseed.request"), // 请求补种
    TORRENT_BYPASS_ANONYMOUS("torrent:bypass.anonymous"), // 免除匿名
    TAG_VIEW("tag:view"), // 查看标签
    TAG_CREATE("tag:create"), // 创建标签
    TAG_DELETE("tag:delete"), // 删除标签
    TAG_EDIT("tag:edit"), // 编辑标签
    REPORT_VIEW("report:view"), // 查看举报列表
    REPORT_CREATE("report:create"), // 创建举报
    REPORT_RESOLVE("report:resolve"), // 处理举报
    REPORT_DELETE("report:delete"), // 删除举报
    THANKS_VIEW("thanks:view"), // 查看感谢列表
    THANKS_CREATE("thanks:create"), // 说谢谢
    USER_VIEW_SELF("user:view.self"), // 查看自己的用户信息
    USER_VIEW("user:view"), // 查看所有人的用户信息
    USER_VIEW_BYPASS("user:view.bypass"), // 绕过隐私限制
    USER_VIEW_SECRET("user:view.secret"), // 查看用户隐私信息（如 IP 地址等）
    USER_EDIT_SELF("user:edit.self"), // 编辑自己的用户信息
    USER_EDIT("user:edit"), // 编辑所有人的用户信息
    USER_WARNING("user:warning"), // 给用户警告
    USER_BAN("user:ban"), // 封禁用户
    PEERS_VIEW("peers:view"), // 查看种子的peer列表
    RANKS_VIEW("ranks:view"), // 查看排行榜
    LOGS_VIEW("logs:view"), // 查看日志
    SECRET_LOGS_VIEW("logs:view.secret"), // 查看隐私日志
    PERMISSION_GROUP_DOWNGRADE("permission.group.downgrade.bypass"), // 免除降级
    PERMISSION_GROUP_EDIT("permission.group.edit"), // 编辑权限组设定
    ADMIN_PANEL("admin") // 访问站点后台设置

    ;

    private final String node;

    Permission(String node) {
        this.node = node;
    }

    public String node(){
        return node;
    }

    public String getNode() {
        return node;
    }
}
