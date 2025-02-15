package com.ghostchu.tracker.sapling.controller.admin.site;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.CurrencyFormDTO;
import com.ghostchu.tracker.sapling.entity.Currencies;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.ICurrenciesService;
import com.ghostchu.tracker.sapling.vo.CurrencyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/site/currencies")
@SaCheckPermission(Permission.ADMIN_SITE_CURRENCIES)
@SaCheckDisable
public class AdminSiteCurrenciesController {
    @Autowired
    private ICurrenciesService currencyService;

    @GetMapping
    public String currencyList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Model model) {
        IPage<Currencies> currencyPage = currencyService.pageCurrencies(page, size, search);
        model.addAttribute("currencies", currencyPage);
        return "admin/site/currencies";
    }

    @PostMapping
    public String saveCurrency(@ModelAttribute CurrencyFormDTO currencyVO) {
        Currencies currency;
        if (currencyVO.getId() != null) {
            currency = currencyService.getCurrency(currencyVO.getId());
        } else {
            currency = new Currencies();
            currency.setManaged(false);
        }
        currency.setName(currencyVO.getName());
        currency.setColor(currencyVO.getColor());
        currencyService.saveCurrency(currency);
        return "redirect:/admin/site/currencies";
    }

    @DeleteMapping("/{id}")
    public String deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrency(id);
        return "redirect:/admin/site/currencies";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public CurrencyVO getCurrency(@PathVariable Long id) {
        return currencyService.toVO(currencyService.getCurrency(id));
    }
}