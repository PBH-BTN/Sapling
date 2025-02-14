package com.ghostchu.tracker.sapling.controller.admin.torrents;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.CategoryFormDTO;
import com.ghostchu.tracker.sapling.entity.Categories;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.ICategoriesService;
import com.ghostchu.tracker.sapling.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/torrents/categories")
@SaCheckPermission(Permission.ADMIN_TORRENTS_CATEGORIES)
@SaCheckDisable
public class AdminTorrentsCategoriesController {
    @Autowired
    private ICategoriesService categoryService;

    @GetMapping
    public String categoryList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Model model) {

        IPage<Categories> categoryPage = categoryService.pageCategories(page, size, search);
        model.addAttribute("categories", categoryPage.convert(categoryService::toVO));
        return "admin/torrents/categories";
    }

    @PostMapping
    public String saveCategory(@ModelAttribute CategoryFormDTO categoryVO) {
        Categories category = categoryService.getCategoryById(categoryVO.getId());
        if (category == null) {
            category = new Categories();
        }
        category.setName(categoryVO.getName());
        category.setIcon(categoryVO.getIcon());
        category.setColor(categoryVO.getColor());
        categoryService.saveOrUpdateCategory(category);
        return "redirect:/admin/torrents/categories";
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/torrents/categories";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public CategoryVO getCategory(@PathVariable Long id) {
        return categoryService.toVO(categoryService.getCategoryById(id));
    }
}
