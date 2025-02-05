package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.model.StdMsg;
import com.ghostchu.tracker.sapling.service.IThanksService;
import com.ghostchu.tracker.sapling.service.ITorrentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Controller
@RequestMapping("/thanks")
public class ThanksController {
    @Autowired
    private IThanksService thanksService;
    @Autowired
    private ITorrentsService torrentsService;

    @PutMapping("/{id}")
    @ResponseBody
    public StdMsg<?> sayThanks(@PathVariable long id) {
        if (!thanksService.createThanks(StpUtil.getLoginIdAsLong(), id)) {
            throw new IllegalStateException("向种子发送感谢失败");
        }
        return new StdMsg<>(true, "感谢发送成功", null);
    }

}
