package com.ghostchu.tracker.sapling.gvar;

// thymeleaf 模板里可能另有权限检查代码，这里的不一定全有引用使用
public class Permission {
    public static final String NEWS_VIEW = "news.view"; // 查看站点公告

    public static final String NEWS_CREATE = "news.create"; // 创建站点公告

    public static final String NEWS_DELETE = "news.delete"; // 删除站点公告

    public static final String INBOX_VIEW = "inbox.view"; // 查看站内信

    public static final String INBOX_CREATE = "inbox.create"; // 创建站内信

    public static final String INBOX_DELETE = "inbox.delete"; // 删除站内信

    public static final String INBOX_SAVE = "inbox.save"; // 保存站内信

    public static final String BITBUCKET_VIEW = "bitbucket.view"; // 查看上传的文件

    public static final String BITBUCKET_UPLOAD = "bitbucket.upload"; // 上传文件

    public static final String BITBUCKET_DELETE = "bitbucket.delete.self"; // 删除自己上传的文件

    public static final String TORRENT_VIEW = "torrent.view"; // 查看种子
    public static final String TORRENT_VIEW_INVISIBLE = "torrent.view.invisible"; // 查看未公开种子
    public static final String TORRENT_VIEW_DELETED = "torrent.view.deleted"; // 查看已删除种子

    public static final String TORRENT_SUBMIT = "torrent.submit"; // 发布种子

    public static final String TORRENT_QUEUE = "torrent.queue"; // 提交种子到待审队列
    public static final String TORRENT_DOWNLOAD = "torrent.download"; // 下载种子

    public static final String TORRENT_DELETE = "torrent.delete.self"; // 删除种子
    public static final String TORRENT_DELETE_OTHER = "torrent.delete.other"; // 删除他人的种子

    public static final String TORRENT_EDIT = "torrent.edit"; // 编辑种子
    public static final String TORRENT_EDIT_OTHER = "torrent.edit.other"; // 编辑他人种子

    public static final String TORRENT_COMMENT = "torrent.comment"; // 评论种子

    public static final String TORRENT_RESEED_REQUEST = "torrent.reseed.request"; // 请求补种

    public static final String TORRENT_BYPASS_ANONYMOUS = "torrent.bypass.anonymous"; // 免除匿名

    public static final String TAG_VIEW = "tag.view"; // 查看标签

    public static final String TAG_CREATE = "tag.create"; // 创建标签

    public static final String TAG_DELETE = "tag.delete"; // 删除标签

    public static final String TAG_EDIT = "tag.edit"; // 编辑标签

    public static final String REPORT_VIEW = "report.view"; // 查看举报列表

    public static final String REPORT_CREATE = "report.create"; // 创建举报

    public static final String REPORT_RESOLVE = "report.resolve"; // 处理举报

    public static final String REPORT_DELETE = "report.delete"; // 删除举报

    public static final String THANKS_VIEW = "thanks.view"; // 查看感谢列表

    public static final String THANKS_CREATE = "thanks.create"; // 说谢谢

    public static final String USER_VIEW_SELF = "user.view.self"; // 查看自己的用户信息

    public static final String USER_VIEW = "user.view"; // 查看所有人的用户信息

    public static final String USER_VIEW_BYPASS = "user.view.bypass"; // 绕过隐私限制

    public static final String USER_VIEW_SECRET = "user.view.secret"; // 查看用户隐私信息（如 IP 地址等）

    public static final String USER_EDIT_SELF = "user.edit.self"; // 编辑自己的用户信息

    public static final String USER_EDIT = "user.edit"; // 编辑所有人的用户信息

    public static final String USER_WARNING = "user.warning"; // 给用户警告

    public static final String USER_BAN = "user.ban"; // 封禁用户

    public static final String PEERS_VIEW = "peers.view"; // 查看种子的peer列表

    public static final String RANKS_VIEW = "ranks.view"; // 查看排行榜

    public static final String LOGS_VIEW = "logs.view"; // 查看日志

    public static final String SECRET_LOGS_VIEW = "logs.view.secret"; // 查看隐私日志

    public static final String PERMISSION_GROUP_DOWNGRADE = "permission.group.downgrade.bypass"; // 免除降级

    public static final String PERMISSION_GROUP_EDIT = "permission.group.edit"; // 编辑权限组设定

    public static final String ADMIN_PANEL = "admin"; // 访问站点后台设置
}
