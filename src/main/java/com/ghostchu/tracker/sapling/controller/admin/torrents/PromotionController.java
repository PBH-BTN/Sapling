package com.ghostchu.tracker.sapling.controller.admin.torrents;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ghostchu.tracker.sapling.entity.Promotions;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.service.IPromotionsService;
import com.ghostchu.tracker.sapling.service.ISettingsService;
import com.ghostchu.tracker.sapling.vo.PromotionsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/torrents/promotions")
@SaCheckPermission(Permission.ADMIN_TORRENTS_PROMOTIONS)
@SaCheckDisable
public class PromotionController {
    @Autowired
    private IPromotionsService promotionService;
    @Autowired
    private ISettingsService settingsService;

    // 显示管理页面
    @GetMapping
    public String promotionsPage(Model model) {
        model.addAttribute("promotions", promotionService.getPromotions());
        return "admin/torrents/promotions";
    }

    // 处理全局设置更新
    @PostMapping("/global")
    public String updateGlobalPromotion(
            @RequestParam boolean enabled,
            @RequestParam Long promotionId,
            RedirectAttributes redirectAttributes) {
        Boolean enabledSitePromotion = Boolean.parseBoolean(settingsService.getValue(Setting.TORRENTS_PROMOTIONS_GLOBAL_ENABLED).orElseThrow());
        Long promotionIdSetting = Long.parseLong(settingsService.getValue(Setting.TORRENTS_PROMOTIONS_GLOBAL_PROMOTION_ID).orElseThrow());
        enabledSitePromotion.setValue(enabled ? "true" : "false");
        promotionIdSetting.setValue(promotionId.toString());
        settingsService.setValue()
        redirectAttributes.addFlashAttribute("success", "全局促销设置已更新");
        return "redirect:/admin/torrents/promotions";
    }

    // 创建/更新促销方案
    @PostMapping({"/", "/{id}"})
    public String savePromotion(
            @PathVariable(required = false) Long id,
            @ModelAttribute PromotionsVO promotion,
            RedirectAttributes redirectAttributes) {
        Promotions promotions = promotionService.getPromotionsById(id);
        if (promotions == null) {
            promotions = new Promotions();
        }
        promotions.setName(promotion.getName());
        promotions.setUploadModifier(promotion.getUploadModifier());
        promotions.setDownloadModifier(promotion.getDownloadModifier());
        promotions.setCondition(promotion.getCondition());
        promotions.setColor(promotion.getColor());
        promotionService.saveOrUpdatePromotions(promotions);
        // 保存逻辑
        redirectAttributes.addFlashAttribute("success", "促销方案已保存");
        return "redirect:/admin/torrents/promotions";
    }

    // 删除促销方案
    @DeleteMapping("/{id}")
    public String deletePromotion(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        promotionService.deletePromotion(id);
        // 删除逻辑
        redirectAttributes.addFlashAttribute("success", "促销方案已删除");
        return "redirect:/admin/torrents/promotions";
    }

    // 提供JSON数据给编辑表单
    @GetMapping("/{id}")
    @ResponseBody
    public PromotionsVO getPromotion(@PathVariable Long id) {
        return promotionService.toVO(promotionService.getPromotionsById(id));
    }
}