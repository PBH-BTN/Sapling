package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Messages;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IMessagesService;
import com.ghostchu.tracker.sapling.util.HtmlSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/messages")
@SaCheckDisable
public class MessagesController {
    @Autowired
    private IMessagesService messageService;

    // 站内信列表
    @GetMapping
    @SaCheckPermission(Permission.MESSAGES_VIEW)
    public String messageList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        IPage<Messages> messages = messageService.pageMessages(page, size, StpUtil.getLoginIdAsLong(), true, false);
        model.addAttribute("messages", messages.convert(messageService::toVO));
        return "messages/list";
    }

    // 查看站内信详情
    @GetMapping("/{id}")
    @SaCheckPermission(value = {Permission.MESSAGES_VIEW, Permission.MESSAGES_VIEW_OTHER}, mode = SaMode.OR)
    public String messageDetail(@PathVariable Long id, Model model) {
        Messages message = messageService.getMessage(id);
        if (message.getOwner() != StpUtil.getLoginIdAsLong()) {
            StpUtil.checkPermission(Permission.MESSAGES_VIEW_OTHER);
        }
        model.addAttribute("message", messageService.toVO(message));
        return "messages/read";
    }

    @PostMapping("/{id}/markRead")
    @ResponseBody
    @SaCheckPermission(value = {Permission.MESSAGES_VIEW, Permission.MESSAGES_VIEW_OTHER}, mode = SaMode.OR)
    public void markAsRead(@PathVariable Long id) {
        Messages message = messageService.getMessage(id);
        if (message.getOwner() != StpUtil.getLoginIdAsLong()) {
            StpUtil.checkPermission(Permission.MESSAGES_VIEW_OTHER);
        }
        messageService.markAsRead(id);
    }

    // 删除站内信
    @DeleteMapping("/{id}")
    @ResponseBody
    @SaCheckPermission(value = {Permission.MESSAGES_DELETE, Permission.MESSAGES_DELETE_OTHER}, mode = SaMode.OR)
    public void deleteMessage(@PathVariable Long id) {
        Messages message = messageService.getMessage(id);
        if (message.getOwner() != StpUtil.getLoginIdAsLong()) {
            StpUtil.checkPermission(Permission.MESSAGES_DELETE_OTHER);
        }
        messageService.markAsDeleted(id);
    }

    // 发送站内信页面
    @GetMapping("/send")
    @SaCheckPermission(Permission.MESSAGES_SEND)
    public String sendMessagePage() {
        return "messages/send";
    }

    // 处理发送请求
    @PostMapping("/send")
    @SaCheckPermission(value = {Permission.MESSAGES_SEND, Permission.MESSAGES_SEND_BATCH}, mode = SaMode.OR)
    public String sendMessage(
            @RequestParam String mode,
            @RequestParam String receivers,
            @RequestParam String title,
            @RequestParam String content) {
        if ("batch".equals(mode)) {
            StpUtil.checkPermission(Permission.MESSAGES_SEND_BATCH);
            messageService.sendMessage(mode, StpUtil.getLoginIdAsLong(), List.of(), title, HtmlSanitizer.sanitize(content));
        } else {
            String[] rece = receivers.split(",");
            for (String s : rece) {
                if (!s.matches("\\d+")) {
                    throw new IllegalArgumentException("一个或者多个站内信接收者 ID 格式错误");
                }
            }
            List<Long> receIds = Arrays.stream(rece).map(Long::parseLong).distinct().toList();
            messageService.sendMessage(mode, StpUtil.getLoginIdAsLong(), receIds, title, HtmlSanitizer.sanitize(content));
        }

        return "redirect:/messages";
    }
}
