package com.ghostchu.tracker.sapling.controller.admin.site;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.WidgetsFormDTO;
import com.ghostchu.tracker.sapling.entity.Widgets;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.ICurrenciesService;
import com.ghostchu.tracker.sapling.service.IWidgetsService;
import com.ghostchu.tracker.sapling.vo.WidgetsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@Controller
@RequestMapping("/admin/site/widgets")
@SaCheckPermission(Permission.ADMIN_SITE_WIDGETS)
@SaCheckDisable
public class AdminSiteWidgetsController {
    @Autowired
    private IWidgetsService widgetsService;
    @Autowired
    private ICurrenciesService currenciesService;


    @GetMapping
    public String widgetList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Model model) {

        IPage<Widgets> widgetPage = widgetsService.pageWidgets(page, size, search);
        model.addAttribute("widgets", widgetPage.convert(widgetsService::toVO));
        model.addAttribute("currencies", currenciesService.pageCurrencies(1, 10000, null).convert(currenciesService::toVO).getRecords());
        return "admin/site/widgets";
    }

    @PostMapping
    @Transactional
    public String saveWidget(@ModelAttribute WidgetsFormDTO widgetVO) {
        Widgets widgets;
        if (widgetVO.getId() != null) {
            widgets = widgetsService.selectWidgetsByIdForUpdate(widgetVO.getId());
        } else {
            widgets = new Widgets();
        }
        widgets.setName(widgetVO.getName());
        widgets.setIcon(widgetVO.getIcon());
        widgets.setColor(widgetVO.getColor());
        widgets.setBuyable(widgetVO.isBuyable());
        widgets.setBuyCurrency(widgetVO.getBuyCurrency());
        widgets.setBuyBalance(widgetVO.getBuyBalance());
        widgets.setStock(widgetVO.getStock());
        widgets.setRestockCronExpression(widgetVO.getRestockCronExpression());
        widgets.setLastRestock(widgetVO.getLastRestock() != null ? widgetVO.getLastRestock() : OffsetDateTime.now());
        widgetsService.saveWidgets(widgets);
        return "redirect:/admin/site/widgets";
    }

    @DeleteMapping("/{id}")
    public String deleteWidget(@PathVariable Long id) {
        widgetsService.deleteWidget(id);
        return "redirect:/admin/site/widgets";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public WidgetsVO getWidget(@PathVariable Long id) {
        return widgetsService.toVO(widgetsService.getWidgetById(id));
    }
}