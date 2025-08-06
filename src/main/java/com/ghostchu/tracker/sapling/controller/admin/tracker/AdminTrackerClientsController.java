package com.ghostchu.tracker.sapling.controller.admin.tracker;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ghostchu.tracker.sapling.entity.Clients;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.model.StdMsg;
import com.ghostchu.tracker.sapling.service.IClientsService;
import com.ghostchu.tracker.sapling.vo.ClientsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/tracker/clients")
@SaCheckPermission(Permission.ADMIN_TRACKER_CLIENTS)
@SaCheckDisable
public class AdminTrackerClientsController {
    @Autowired
    private IClientsService clientsService;

    // 查看列表
    @GetMapping
    public String listClients(Model model) {
        // 伪代码：从数据库获取列表
        List<ClientsVO> clients = clientsService.getAllClients().stream().map(clientsService::toVO).toList();
        model.addAttribute("clients", clients);
        return "admin/tracker/clients";
    }

    // 查看单个（用于编辑）
    @GetMapping("/{id}")
    @ResponseBody
    public ClientsVO getClient(@PathVariable Long id) {
        // 伪代码：从数据库获取
        return clientsService.toVO(clientsService.getClientById(id));
    }

    // 创建
    @PostMapping
    public String createClient(@ModelAttribute ClientsVO client) {
        // 伪代码：保存到数据库
        clientsService.createClient(client.getName(), client.getAgentPattern(), client.getPeerPattern(), client.getComment());
        return "redirect:/admin/tracker/clients";
    }

    // 更新
    @PostMapping("/{id}")
    public String updateClient(@PathVariable Long id, @ModelAttribute ClientsVO client) {
        // 伪代码：更新数据库
        Clients c = clientsService.getClientById(id);
        c.setName(client.getName());
        c.setAgentPattern(client.getAgentPattern());
        c.setPeerPattern(client.getPeerPattern());
        c.setComment(client.getComment());
        c.setAllowed(client.isAllowed());
        clientsService.updateClient(c);
        return "redirect:/admin/tracker/clients";
    }

    //
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        // 伪代码：删除操作
        clientsService.deleteClientById(id);
        return ResponseEntity.ok().body(new StdMsg<>(true, "规则已删除！", null));
    }
}
