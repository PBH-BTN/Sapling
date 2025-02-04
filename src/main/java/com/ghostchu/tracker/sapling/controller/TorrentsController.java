package com.ghostchu.tracker.sapling.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Torrents;
import com.ghostchu.tracker.sapling.service.ITorrentsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Controller
@RequestMapping("/torrents")
public class TorrentsController {
    @Autowired
    private ITorrentsService torrentsService;
    @Autowired
    private IUsersService usersService;
    @GetMapping
    public String torrentList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "50") int size,
            Model model) {
        // 获取分页数据
        IPage<Torrents> pageResult = torrentsService.getTorrentsByPage(page, size);

        // 准备模型数据
        model.addAttribute("torrents", pageResult.getRecords());
        model.addAttribute("pagination", pageResult);

        return "torrents";
    }
}
