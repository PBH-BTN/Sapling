package com.ghostchu.tracker.sapling.controller.admin.site;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.BitbucketFormDTO;
import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IBitbucketService;
import com.ghostchu.tracker.sapling.vo.BitbucketVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/site/bitbucket")
@SaCheckPermission(Permission.ADMIN_SITE_BITBUCKET)
@SaCheckDisable
public class AdminSiteBitBucketController {
    @Autowired
    private IBitbucketService bitbucketService;

    @GetMapping
    public String listFiles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Model model) {

        IPage<Bitbucket> filePage = bitbucketService.pageFiles(page, size, search);
        model.addAttribute("bitbuckets", filePage.convert(bitbucketService::toVO));
        return "admin/site/bitbucket";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public BitbucketVO getFile(@PathVariable Long id) {
        return bitbucketService.toVO(bitbucketService.getBitBucket(id));
    }

    @PostMapping
    public String updateFile(@ModelAttribute BitbucketFormDTO bitbucketVO) {
        Bitbucket bitbucket = bitbucketService.getBitBucket(bitbucketVO.getId());
        if (bitbucket.isManaged()) {
            throw new IllegalStateException("不能编辑被系统托管的 BitBucket 文件");
        }
        bitbucket.setHandler(bitbucketVO.getHandler());
        bitbucket.setDisplayName(bitbucketVO.getDisplayName());
        bitbucket.setDirectAccess(bitbucketVO.isDirectAccess());
        bitbucketService.updateBitBucket(bitbucket);
        return "redirect:/admin/bitbucket";
    }


    @DeleteMapping("/{id}")
    public String deleteFile(@PathVariable Long id) {
        bitbucketService.deleteById(id);
        return "redirect:/admin/site/bitbucket";
    }
}
