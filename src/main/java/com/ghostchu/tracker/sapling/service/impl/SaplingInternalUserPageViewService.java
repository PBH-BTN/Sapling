package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.dto.SiteEventDTO;
import com.ghostchu.tracker.sapling.entity.Promotions;
import com.ghostchu.tracker.sapling.event.UserViewPageEvent;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.service.IPromotionsService;
import com.ghostchu.tracker.sapling.service.ISettingsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.util.MsgUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SaplingInternalUserPageViewService {
    @Autowired
    private ISettingsService settingsService;
    @Autowired
    private IPromotionsService promotionsService;
    @Autowired
    private IUsersService usersService;

    @EventListener(classes = UserViewPageEvent.class)
    public void sitePromotion(UserViewPageEvent event) {
        boolean useGlobalPromotion = Boolean.parseBoolean(settingsService.getValue(Setting.TORRENTS_PROMOTIONS_GLOBAL_ENABLED).orElse("false"));
        if (useGlobalPromotion) {
            Promotions promotions = promotionsService.getPromotionsById(Long.parseLong(settingsService.getValue(Setting.TORRENTS_PROMOTIONS_GLOBAL_PROMOTION_ID).orElseThrow()));
            if (promotions != null) {
                SiteEventDTO siteEventDTO = new SiteEventDTO();
                siteEventDTO.setTitle("全站促销");
                String description = """
                        全站种子现在正在 <span style="color:{}">{}</span> 促销中！ <a href="/torrents">现在去看看-></a>
                        """;
                siteEventDTO.setDescription(MsgUtil.fillArgs(description, promotions.getColor(), promotions.getName()));
                siteEventDTO.setAlertType("success");
                event.getSiteEventDTOList().add(siteEventDTO);
            }
        }
    }
}
